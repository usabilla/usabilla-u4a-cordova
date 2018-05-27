#!/usr/bin/env node

'use strict';

const fs = require('fs');
const xml2js = require('xml2js');
const utilities = require("../lib/utilities");

module.exports = function(context) {

  var androidManifestPath = utilities.getAndroidManifestPath(context);

  if (androidManifestPath !== null) {
    const parseString = xml2js.parseString;
    const manifestPath = androidManifestPath + '/AndroidManifest.xml';
    const androidManifest = fs.readFileSync(manifestPath).toString();

    if (androidManifest) {
      parseString(androidManifest, (err, manifest) => {
        if (err) return console.error(err);
        var newManifest = androidManifest.replace('android:name="MainActivity"', 'android:name="com.usabilla.MainActivity"');
        fs.writeFileSync(manifestPath, newManifest);
      });
    }
  }
};