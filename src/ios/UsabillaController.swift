class SomeViewController: UIViewController, UsabillaDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()
        Usabilla.delegate = self
        Usabilla.loadFeedbackForm("Form ID")
    }

    //Called when your form succesfully load
    func formDidLoad(form: UINavigationController) {
        present(form, animated: true, completion: nil)
    }

    //Called when your forms can not be loaded. Returns a default form
    func formDidFailLoading(error: UBError) {
        //...
    }
}