@objc(UsabillaCordova) class UsabillaCordova : CDVPlugin, ResultDelegate {
    var command: CDVInvokedUrlCommand?
    var formId: String?
    var appId: String?
    var customVariables: [String: Any]?

    func extractCustomVariables(command: CDVInvokedUrlCommand) {
        var arguments: [String: Any] = [:]
        for (_, element) in command.arguments.enumerated() {
            for (key, value) in element as! Dictionary<String, Any> {
                if (key == "APP_ID") {
                    self.appId = value as? String
                } else if (key == "FORM_ID") {
                    self.formId = value as? String
                } else {
                    arguments[key] = value
                }
            }
        }
        self.customVariables = arguments
    }

    @objc(feedback:)
    func feedback(command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command)
        
        let feedbackController: FeedbackController = FeedbackController()
        feedbackController.customVariables = self.customVariables
        feedbackController.formId = self.formId
        feedbackController.appId = self.appId
        feedbackController.delegate = self
        
        self.viewController?.present(
            feedbackController,
            animated: true,
            completion: nil
        )
    }

    @objc(sendEvent:)
    func sendEvent(command: CDVInvokedUrlCommand) {
        self.command = command;
    }
    
    func success(completed: Bool) {
        let result = ["completed": completed] as [AnyHashable : Any]
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_OK,
            messageAs: result
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command?.callbackId
        )
    }
    
    func error() {
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: "Unexpected error"
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command?.callbackId
        )
    }
}
