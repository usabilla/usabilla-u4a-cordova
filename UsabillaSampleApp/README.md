[![license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/usabilla/usabilla-u4a-react-native/blob/develop/LICENSE)

# Usabilla for Cordova

## Resource Generator

This tool will crop and resize JPEG and PNG source images to generate icons and splash screens for modern devices. `cordova-res` was developed to be used with Cordova, but Capacitor and other native runtimes are supported.

### Install

```bash
$ npm install -g cordova-res
```

### Usage

To generate resources with all the default options, just run:

```bash
$ cordova-res
```

`cordova-res` accepts a platform as first argument. If specified, resources are generated only for that platform:

```bash
$ cordova-res ios
```

Otherwise, if `config.xml` exists, `cordova-res` will look for platforms (e.g. `<platform name="ios">`) and generate resources only for the configured platforms.