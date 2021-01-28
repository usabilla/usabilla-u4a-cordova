import Foundation
import Usabilla
import UIKit

@objc(UsabillaCordova)
class UsabillaCordova: CDVPlugin {

    @objc weak var formNavigationController: UINavigationController?
    
    var command: CDVInvokedUrlCommand?
    var formId: String?
    var appId: String?
    var customVariables: [String: String]?
    var eventName: String?
    var masks: [String]?
    var maskChar: String?
    var formIds: [String]?
    var debugEnabled: Bool?
    var localizedStringFile: String?

    // Extracts the variables sent from Usabilla.js
    func extractCustomVariables(command: CDVInvokedUrlCommand) {
        Usabilla.delegate = self
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
                    self.customVariables = value as? [String : String]
                } else if (key == "FORM_IDs") {
                    self.formIds = value as? [String]
                } else if (key == "DEBUG_ENABLED") {
                    self.debugEnabled = value as? Bool
                } else if (key == "LOCALIZED_STRING_FILENAME") {
                    self.localizedStringFile = value as? String
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
        Usabilla.initialize(
            appID: self.appId,
            completion: {
                self.success(completed: true)
        })
        guard let variable = self.customVariables else {
            print("ERROR : Expected customVariables as Dictionary of String [String : String]")
            return
        }
        Usabilla.customVariables = variable
    }

    // Load Usabilla passive forms with form ids
    @objc(loadFeedbackForm:)
    func loadFeedbackForm(command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command)
        let formID = self.formId!
        Usabilla.loadFeedbackForm(formID)
    }

    // Load Usabilla passive forms with the visibile view screenshot
    @objc(loadFeedbackFormWithCurrentViewScreenshot:)
    func loadFeedbackFormWithCurrentViewScreenshot(command: CDVInvokedUrlCommand) {
        self.command = command;
        self.extractCustomVariables(command: command)
        let formID = self.formId!
        if let rootVC = UIApplication.shared.keyWindow?.rootViewController {
            let screenshot = self.takeScreenshot(view: rootVC.view)
            Usabilla.loadFeedbackForm(formID, screenshot: screenshot)
        }
    }

    func takeScreenshot(view: UIView) -> UIImage {
        return Usabilla.takeScreenshot(view)!
    }

    // Send events to trigger campaigns
    @objc(sendEvent:)
    func sendEvent(command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        Usabilla.sendEvent(event: self.eventName!)
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
    
    // preloadFeedbackForms
    @objc(preloadFeedbackForms:)
    func preloadFeedbackForms(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        Usabilla.preloadFeedbackForms(withFormIDs: self.formIds!)
        self.success(completed: true)
    }
    
    // removeCachedForms
    @objc(removeCachedForms:)
    func removeCachedForms(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        let _ = Usabilla.removeCachedForms()
        self.success(completed: true)
    }
    
    // setDebugEnabled
    @objc(setDebugEnabled:)
    func setDebugEnabled(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        Usabilla.debugEnabled = self.debugEnabled!
        self.success(completed: true)
    }
    
    // loadLocalizedStringFile
    @objc(loadLocalizedStringFile:)
    func loadLocalizedStringFile(_ command: CDVInvokedUrlCommand) {
        self.command = command;
        extractCustomVariables(command: command)
        Usabilla.localizedStringFile = self.localizedStringFile!
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
    
    func error(error: Any) {
        let result = ["error": error] as [AnyHashable : Any]
        let pluginResult = CDVPluginResult(
            status: CDVCommandStatus_ERROR,
            messageAs: result
        )
        self.commandDelegate!.send(
            pluginResult,
            callbackId: command?.callbackId
        )
    }
}

extension UsabillaCordova: UsabillaDelegate {
    func formDidLoad(form: UINavigationController) {
        formNavigationController = form
        if let rootVC = UIApplication.shared.keyWindow?.rootViewController {
            rootVC.present(formNavigationController!, animated: true, completion: nil)
        }
    }

    func formDidFailLoading(error: UBError) {
        formNavigationController = nil
        self.error(error: "The form could not be loaded")
    }

    func formDidClose(formID: String, withFeedbackResults results: [FeedbackResult], isRedirectToAppStoreEnabled: Bool) {
        var rnResults: [[String : Any]] = []
        for result in results {
            let dictionary: Dictionary = ["rating": result.rating ?? 0, "abandonedPageIndex": result.abandonedPageIndex ?? 0, "sent": result.sent] as [String : Any]
            rnResults.append(dictionary)
        }
        
        let resultCB = ["formId": formID, "results": rnResults, "isRedirectToAppStoreEnabled": isRedirectToAppStoreEnabled] as [String : Any]
        formNavigationController = nil
        self.success(completed: resultCB)
    }
    
    func campaignDidClose(withFeedbackResult result: FeedbackResult, isRedirectToAppStoreEnabled: Bool) {
        let rnResult: [String : Any] = ["rating": result.rating ?? 0, "abandonedPageIndex": result.abandonedPageIndex ?? 0, "sent": result.sent] as [String : Any]
        
        let resultCB = ["result": rnResult, "isRedirectToAppStoreEnabled": isRedirectToAppStoreEnabled] as [String : Any]
        formNavigationController = nil
        self.success(completed: resultCB)
    }
}
