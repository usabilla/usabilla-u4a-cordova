import Usabilla

@objc(UsabillaCordova) class UsabillaCordova : CDVPlugin, ResultDelegate {
    var command: CDVInvokedUrlCommand?
    var formId: String?
    var appId: String?
    var customVariables: [String: Any]?
    var eventName: String?
    var masks: [String]?
    var maskChar: String?

    // Extracts the variables sent from Usabilla.js
    func extractCustomVariables(command: CDVInvokedUrlCommand) {
        for (_, element) in command.arguments.enumerated() {
            for (key, value) in element as! Dictionary<String, Any> {
                if (key == "EVENT_NAME") {
                    self.eventName = value as? String
                } else if (key == "APP_ID") {
                    self.appId = value as? String
                } else if (key == "FORM_ID") {
                    self.formId = value as? String
                } else if (key == "MASKS") {
                    self.masks = value as? [String]
                } else if (key == "MASK_CHAR") {
                    self.maskChar = value as? String
                } else if (key == "CUSTOM_VARS") {
                    self.customVariables = value as? [String : Any]
                } else {
                    debugPrint(key, value, separator: " -- ")
                }
            }
        }
    }

    // Iinitialize the SDK with your appId to target campaigns
    @objc(initialize:)
    func initialize(command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        let newCustomVariables = self.customVariables!.mapValues { String(describing: $0) }
        Usabilla.initialize(
            appID: self.appId,
            completion: {
                self.success(completed: true)
        })
        Usabilla.customVariables = newCustomVariables
    }

    // Load Usabilla passive forms with form ids
    @objc(loadFeedbackForm:)
    func loadFeedbackForm(command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command)
        
        let feedbackController: FeedbackController = FeedbackController()
        feedbackController.formId = self.formId
        feedbackController.delegate = self
        
        self.viewController?.present(
            feedbackController,
            animated: true,
            completion: nil
        )
    }

    // Load Usabilla passive forms with the visibile view screenshot
    @objc(loadFeedbackFormWithCurrentViewScreenshot:)
    func loadFeedbackFormWithCurrentViewScreenshot(command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command)
        
        let feedbackController: FeedbackController = FeedbackController()
        feedbackController.formId = self.formId
        feedbackController.delegate = self
        if let topController = UIApplication.topViewController() {
            let screenshot = Usabilla.takeScreenshot(topController.view)
            feedbackController.screenshot = screenshot
        }

        self.viewController?.present(
            feedbackController,
            animated: true,
            completion: nil
        )
    }

    // Send events to trigger campaigns
    @objc(sendEvent:)
    func sendEvent(command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        Usabilla.sendEvent(event: self.eventName!)
        self.success(completed: true)
    }
    
    // Reset campaign data to a clean state
    @objc(resetCampaignData:)
    func resetCampaignData(command: CDVInvokedUrlCommand) {
        self.command = command;
        Usabilla.resetCampaignData {
            self.success(completed: true)
        }
    }

    // Set Dismiss to close the campaign
    @objc(dismiss:)
    func dismiss(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        let _ = Usabilla.dismiss()
        self.success(completed: true)
    }

    // Default Data masking
    @objc(getDefaultDataMasks:)
    func getDefaultDataMasks(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        let str = Usabilla.defaultDataMasks
        self.success(completed: str)
    }

    // Set Data masking
    @objc(setDataMasking:)
    func setDataMasking(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command);
        if let maskCharacter = self.maskChar?.first, let mask = self.masks {
            Usabilla.setDataMasking(masks: mask, maskCharacter: maskCharacter)
        } else {
            Usabilla.setDataMasking(masks: Usabilla.defaultDataMasks, maskCharacter: "X")
        }
        self.success(completed: true)
    }
    
    func success(completed: Any) {
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

extension UIApplication {
    class func topViewController(controller: UIViewController? = UIApplication.shared.keyWindow?.rootViewController) -> UIViewController? {
        if let navigationController = controller as? UINavigationController {
            return topViewController(controller: navigationController.visibleViewController)
        }
        if let tabController = controller as? UITabBarController {
            if let selected = tabController.selectedViewController {
                return topViewController(controller: selected)
            }
        }
        if let presented = controller?.presentedViewController {
            return topViewController(controller: presented)
        }
        return controller
    }
}