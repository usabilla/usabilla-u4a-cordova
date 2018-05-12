var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var Usabilla = function() {};

Usabilla.prototype.feedback = function(success, fail, formId) {
  var vars = {};
  vars['FORM_ID'] = formId;

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "feedback", [
      vars
    ]);
};

Usabilla.prototype.initApp = function(success, fail, appId, customVars) {
  var vars = {};
  var customVars = customVars || {};
  vars['APP_ID'] = appId;
  
  Object.keys(customVars).map((key) => {
    var value = customVars[key];
    if (typeof value != "object") {
      vars[key] = value;
    }
  });

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "initApp", [
      vars
    ]);
};

Usabilla.prototype.resetCampaing = function(success, fail) {
  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "resetCampaing");
};

Usabilla.prototype.sendEvent = function(success, fail, eventId) {
  var vars = {};
  var customVars = customVars || {};
  vars['EVENT_ID'] = eventId;

  return cordova.exec(
    success, 
    fail, 
    "UsabillaCordova", 
    "sendEvent", [
      vars
    ]);
};

window.Usabilla = new Usabilla();