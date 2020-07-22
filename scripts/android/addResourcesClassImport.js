#!/usr/bin/env node

function getRegexGroupMatches(string, regex, index) {
    index || (index = 1);

    var matches = [];
    var match;
    if (regex.global) {
        while (match = regex.exec(string)) {
            matches.push(match[index]);
            console.log('Match:', match);
        }
    }
    else {
        if (match = regex.exec(string)) {
            matches.push(match[index]);
        }
    }

    return matches;
}

module.exports = function (ctx) {
    // If Android platform is not installed, don't even execute
    if (ctx.opts.cordova.platforms.indexOf('android') < 0)
        return;
        
        var fs = require('fs'),
        path = require('path'),
        Q = require('q'),
        glob = require('glob');

    var deferral = Q.defer();

    var platformSourcesRoot = path.join(ctx.opts.projectRoot, 'platforms/android/app/src/main/java');
    var pluginSourcesRoot = path.join(ctx.opts.plugin.dir, 'src/android');

    var androidPluginsData = JSON.parse(fs.readFileSync(path.join(ctx.opts.projectRoot, 'plugins', 'android.json'), 'utf8'));
    var appPackage = androidPluginsData.installed_plugins[ctx.opts.plugin.id]['PACKAGE_NAME'];

    console.log('Found plugin sources root', pluginSourcesRoot);

    glob(pluginSourcesRoot + '/**/*.java', { absolute: true }, function (err, files) {
        if (err) {
            console.error('Error when reading file:', err);
            deferral.reject();
            return;
        }

        files.forEach(function (file) {

                var filename = path.basename(file);

                console.log('Checking file for R references', file);

                fs.readFile(file, 'utf-8', function (err, contents) {
                    if (err) {
                        console.error('Error when reading file:', err);
                        deferral.reject();
                        return;
                    }

                    if (contents.match(/[^\.\w]R\./)) {
                        console.log('Trying to get packages from file:', filename);
                        var packages = getRegexGroupMatches(contents, /package ([^;]+);/);

                        for (var p = 0; p < packages.length; p++) {
                            try {
                                var package = packages[p];

                                var sourceFile = path.join(platformSourcesRoot, package.replace(/\./g, '/'), filename);
                                if (!fs.existsSync(sourceFile))
                                    throw 'Can\'t find file in installed platform directory: "' + sourceFile + '".';

                                var sourceFileContents = fs.readFileSync(sourceFile, 'utf8');
                                if (!sourceFileContents)
                                    throw 'Can\'t read file contents.';

                                var newContents = sourceFileContents
                                    .replace(/(import ([^;]+).R;)/g, '')
                                    .replace(/(package ([^;]+);)/g, '$1 import ' + appPackage + '.R;');

                                fs.writeFileSync(sourceFile, newContents, 'utf8');
                                break;
                            } catch (ex) {
                                console.log('Could not add import to "' +  filename + '" using package "' + package + '". ' + ex);
                            }
                        }
                    }
                });

                deferral.resolve();
            });
    });

    return deferral.promise;
}
