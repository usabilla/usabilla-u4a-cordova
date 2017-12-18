var exec = require('cordova/exec');
var argscheck = require('cordova/argscheck');
var Usabilla = function() {};

Usabilla.prototype.feedback = function(success, fail, options) {
  var getValue = argscheck.getValue;

  var formId = options.formId;
  var isCoach = options.isCoach || false;
  var email = options.email || '';
  
  return cordova.exec(
    success, 
    fail, 
    "Usabilla", 
    "feedback", [
      formId,
      email,
      isCoach
    ]);
};

window.Usabilla = new Usabilla();