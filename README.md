[![license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/usabilla/usabilla-u4a-react-native/blob/develop/LICENSE)

# Usabilla for Cordova

Usabilla for Apps allows you to collect feedback from your users with great ease and flexibility.
This Cordova bridge to the Native Usabilla SDK allows you to load passive feedback forms and submit results from a Cordova/PhoneGap app.


## Support

If you need help, want to report an issue, or have a question please reach out to the support team via our [Help Center](https://help.surveymonkey.com/en/contact/#getfeedback) or email [support@usabilla.com](mailto:support@usabilla.com)

## Installation

To install the Usabilla SDK into your Cordova App:
1. In a terminal window, navigate to the root directory of your project and run :

```
cordova plugin add usabilla-cordova --save
```

## Additional setup
#### iOS

1. The native Usabilla SDK is written in Swift, So you will have to add the Swift version to your `config.xml` file.
```
<preference name="UseSwiftLanguageVersion" value="4.0" />
```
This version of the Usabilla Cordova bridge supports stable `Cordova v9`, works with the latest release of `XCode 11` and targets the use of `Android X` libraries.

The Native SDK is build for Xcode 11+, and is compiled with Module Format Stability allowing it to use different version of Swift 
If you use an earlier version of Xcode, change the pod-file, so it uses the correct Usabilla version.
see the [native SDKs readme.md](https://github.com/usabilla/usabilla-u4a-ios-swift-sdk) for instructions. 


#### Android

1. Be aware that Cordova's main `MainActivity.java` will be replaced with our custom. Usabilla is updated through fragmentManager and that activities rely on fragments.

### Requirements

This version of the Cordova bridge works with the latest release of `XCode 9.4`.

## Usage

Prior to any usage the tool needs to be started:

```
  Usabilla.initialize(
    function() {
        console.log('success');
    },
    function () {
        console.log('error');
    },
    YOUR_APP_ID_HERE,
    customVars}
```
**NOTE**: `customVars` should be a valid JSON object with the limitations as the key,value should be of String type.

### Load a Passive Feedback form

```
  Usabilla.loadFeedbackForm(
    function() {
        console.log('success');
    }, 
    function () {
        console.log('error');
    },
    YOUR_FORM_ID_HERE);
```

This callback has a parameter containing the information:
  - formId (string)
  - isRedirectToAppStoreEnabled (boolean)

### Pre-fill a Passive Feedback form with a custom screenshot

Usabilla for Cordova allows you to attach a screenshot to a form before sending it by calling:

```
  Usabilla.loadFeedbackFormWithCurrentViewScreenshot(
    function() {
        console.log('success');
    }, 
    function () {
        console.log('error');
    },
    YOUR_FORM_ID_HERE);
```

This method will take a screenshot of the current visible view and pre-fill the form with it.

### Campaigns

In order to be able to run campaigns in your app, you should first start by initializing the SDK as seen before.

This call loads and updates all your campaigns locally and you can start targeting them by sending events from your app using the method:

```
  Usabilla.sendEvent(
    function() {
        self.setButtonsDisabled(false);
    }, 
    function () {
        self.setButtonsDisabled(false);
    },
    YOUR_EVENT_NAME_HERE);
```

The Usabilla SDK allows you to reset all the campaign data by calling:

```
  Usabilla.resetCampaignData(
    function() {
        console.log('success');
    },
    function() {
        console.log('error');
    }
  );
```

