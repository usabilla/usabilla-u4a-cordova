# README #

* Summary of set up
A plugin for using Usabilla in hybrid apps.


[![license](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/usabilla/usabilla-u4a-react-native/blob/develop/LICENSE)


# Usabilla for Cordova

Usabilla for Apps allows you to collect feedback from your users with great ease and flexibility.
This Cordova bridge to the Native Usabilla SDK allows you to load passive feedback forms and submit results from a Cordova/PhoneGap app.

## Installation

To install the Usabilla SDK into your Cordova App:
1. In a terminal window, navigate to the root directory of your project and run :

```
cordova plugin add cordova-usabilla@2.0.0 --save
```

### Additional setup
#### iOS

1. The native Usabilla SDK is written in Swift, So you will have to add the Swift version to your `config.xml` file.
```
<preference name="UseSwiftLanguageVersion" value="3.3" />
```
This version will depend on which version of XCode you are using. 

#### Android

1. Be aware that Cordova's main `MainActivity.java` will be replaced with our custom. Usabilla is updated through fragmentManager and that activities rely on fragments.

### Requirements

This version of the react native bridge works with the latest release of `XCode 9.3`.

## Usage

Prior to any usage the tool needs to be started:

```
  Usabilla.initApp(
    function() {
        console.log('success');
    },
    function () {
        console.log('error');
    },
    YOUR_APP_ID_HERE,
    customVars}
```

### Load a Passive Feedback form

```
  Usabilla.feedback(
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

This method will take a screenshot of the current visible view and pre-fill the form with it.

In order to set custom variables in the Usabilla native library it's necessary to call the method:

### Campaigns

In order to be able to run campaigns in your app, you should first start by initializing the SDK as seen before

This call loads and updates all your campaigns locally and you can start targeting them by sending events from your app using the method:

```
  Usabilla.sendEvent(
    function() {
        self.setButtonsDisabled(false);
    }, 
    function () {
        self.setButtonsDisabled(false);
    },
    YOUR_EVENT_ID_HERE);
```

The Usabilla SDK allows you to reset all the campaign data by calling:

```
  Usabilla.resetCampaign(
    function() {
        console.log('success');
    },
    function() {
        console.log('error');
    }
  );
```
