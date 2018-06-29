import Usabilla

protocol ResultDelegate: class {
    func success(completed: Bool)
    func error()
}

class FeedbackController: UIViewController, UsabillaDelegate {
    var formId: String?
    var screenshot: UIImage?

    weak var delegate: ResultDelegate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        Usabilla.delegate = self
        
        Usabilla.loadFeedbackForm(self.formId!, screenshot: screenshot)
    }
    
    //Called when your form succesfully load
    func formDidLoad(form: UINavigationController) {
        present(form, animated: true, completion: nil)
    }
    
    //Called when your forms can not be loaded. Returns a default form
    func formDidFailLoading(error: UBError) {
        delegate?.error()
    }
    
    func formDidClose(formID: String, withFeedbackResults results: [FeedbackResult], isRedirectToAppStoreEnabled: Bool) {
        self.dismiss(animated: false);
        delegate?.success(completed: results[0].abandonedPageIndex == nil)
    }
}
