var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var Usabilla = function() {};

Usabilla.prototype.initialize = function(success, fail, appId, customVars) {
  var vars = {};
  var customVars = customVars || {};
  vars['APP_ID'] = appId;
  
  Object.keys(customVars).map(function(key) {
    var value = customVars[key];
    if (typeof value != "object") {
      vars[key] = value;
    }
  });

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "initialize", [
      vars
    ]);
};

Usabilla.prototype.loadFeedbackForm = function(success, fail, formId) {
  var vars = {};
  vars['FORM_ID'] = formId;

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "loadFeedbackForm", [
      vars
    ]);
};

Usabilla.prototype.loadFeedbackFormWithCurrentViewScreenshot = function(success, fail, formId) {
  var vars = {};
  vars['FORM_ID'] = formId;
  
  return cordova.exec(
    success,
    fail,
    "UsabillaCordova",
    "loadFeedbackFormWithCurrentViewScreenshot", [
      vars
    ]);
};

Usabilla.prototype.sendEvent = function(success, fail, eventId) {
  var vars = {};
  var customVars = customVars || {};
  vars['EVENT_NAME'] = eventId;

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "sendEvent", [
      vars
    ]);
};

Usabilla.prototype.resetCampaignData = function(success, fail) {
  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "resetCampaignData");
};

window.Usabilla = new Usabilla();