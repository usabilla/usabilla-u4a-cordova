var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var Usabilla = function() {};

Usabilla.prototype.feedback = function(success, fail, formId, appId, customVars) {
  var vars = {};
  var customVars = customVars || {};
  vars['FORM_ID'] = formId;
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
    "feedback", [
      vars
    ]);
};

window.Usabilla = new Usabilla();