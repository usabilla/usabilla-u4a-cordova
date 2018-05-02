@objc(Usabilla) class Usabilla : CDVPlugin {
  @objc(feedback:)
  func feedback(command: CDVInvokedUrlCommand) {
    var pluginResult = CDVPluginResult(
      status: CDVCommandStatus_ERROR
    )

    pluginResult = CDVPluginResult(
      status: CDVCommandStatus_OK,
      messageAs: "this is the result"
    )

    self.commandDelegate!.send(
      pluginResult,
      callbackId: command.callbackId
    )
  }
}