var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var Usabilla = function() {};

Usabilla.prototype.feedback = function(success, fail, options) {
  var getValue = argscheck.getValue;

  var formId = options.formId;
  
  return cordova.exec(
    success, 
    fail, 
    "Usabilla", 
    "feedback", [
      formId
    ]);
};

window.Usabilla = new Usabilla();