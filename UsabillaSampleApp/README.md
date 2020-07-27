[![license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/usabilla/usabilla-u4a-react-native/blob/develop/LICENSE)

# Usabilla for Cordova

Demonstrates how to use the usabilla cordova plugin.

## Getting Started

This project is a starting point for a Cordova application.

### Create Cordova Application
For help getting started with Cordova and to setup a new app [online documentation](https://cordova.apache.org/#getstarted).

### Resource Generator

This tool will crop and resize JPEG and PNG source images to generate icons and splash screens for modern devices. `cordova-res` was developed to be used with Cordova, but Capacitor and other native runtimes are supported.

#### Install

```bash
$ npm install -g cordova-res
```

#### Usage

To generate resources with all the default options, just run:

```bash
$ cordova-res
```

`cordova-res` accepts a platform as first argument. If specified, resources are generated only for that platform:

```bash
$ cordova-res ios
```

Otherwise, if `config.xml` exists, `cordova-res` will look for platforms (e.g. `<platform name="ios">`) and generate resources only for the configured platforms.

### Add Platform

- To add native platforms like android & ios , just run:

```bash
$ cordova platform add android ios
```

### Add Usabilla plugin

- To add the Usabilla SDK into your Cordova application, just run:

```bash
$ cordova plugin add usabilla-cordova --save
```

### Setup SDK

-  Update Configurations to run sample app
``` js
<script type="text/javascript" src="js/config.js"></script>
```
``` js
/// Usabilla Configuration
const appId = 'YOUR_APP_ID_HERE';
const formId = 'YOUR_FORM_ID_HERE';
let event = 'YOUR_EVENT_TAG_HERE';
const customVariable = {'YOUR_KEY_HERE': 'YOUR_VALUE_HERE'};
```

### Functions

- Initialize the SDK and set custom variables for targeting campaigns
```
initialize → String appId , Object customVariable
```
``` js
    const customVariable = {'YOUR_KEY_HERE': 'YOUR_VALUE_HERE'};
    Usabilla.initialize(function() {}, function (err) {}, appId, customVariable);
```
- Show the passive form and return a JSON Object containing the results - rating, pageIndex and sent flag
```
loadFeedbackForm → String formId
```
``` js
    Usabilla.loadFeedbackForm(function() {}, function (err) {}, formId);
```
- Load the passive form with the current screen captured and return a JSON Object containing the results - rating, pageIndex and sent flag
```
loadFeedbackFormWithCurrentViewScreenshot → String formId
```
``` js
    Usabilla.loadFeedbackFormWithCurrentViewScreenshot(function() {}, function (err) {}, formId);
```
- Load the campaign and return a map containing the results - rating, pageIndex and sent flag
```
sendEvent → String event
```
``` js
    Usabilla.sendEvent(function() {}, function (err) {}, event);
```
- Reset campaign data, so they can be triggered again from a fresh count
```
resetCampaignData()
```
``` js
    Usabilla.resetCampaignData(function() {}, function (err) {});
```
- Dismiss the currently visible form
```
dismiss()
```
``` js
    Usabilla.dismiss(function() {}, function (err) {});
```
- Set data masking based on passed/default rules using a passed/default character for obfuscation
```
setDataMasking → Array masks, String character
```
``` js
    const masks = 'YOUR_Masking_Regex';
    const maskCharacter = 'YOUR_Masking_Character';
    Usabilla.setDataMasking(function() {}, function (err) {}, masks, maskCharacter);
```
- Return the array of default masking rules
```
getDefaultDataMasks()
```
``` js
    Usabilla.getDefaultDataMasks(function() {}, function (err) {});
```
- Store form(s) in memory to be used in offline mode
```
preloadFeedbackForms → List formIds
```
``` js
    Usabilla.preloadFeedbackForms(function() {}, function (err) {}, formIds);
```
- Remove cached forms stored for offline use
```
removeCachedForms()
```
``` js
    Usabilla.removeCachedForms(function() {}, function (err) {});
```
- Set debug mode to enable logging on console of Usabilla SDK errors and informations
```
setDebugEnabled → Boolean debugEnabled
```
``` js
    Usabilla.setDebugEnabled(function() {}, function (err) {}, debugEnabled);
```
- Set filename used for localization `[Available only for iOS]`
```
loadLocalizedStringFile → String localizedStringFile
```
``` js
    Usabilla.loadLocalizedStringFile(function() {}, function (err) {}, localizedStringFile);
```