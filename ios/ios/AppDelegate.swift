import Firebase
import UIKit
import core

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var navigator: UiNavigator!

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        
        setUpAppNavigator()
        
        return true
    }
    
    private func setUpAppNavigator() {
        #if DEBUG
        let testsHelper = TestsHelper()
        let useMocks = testsHelper.testingEnabled
        #else
        let useMocks = false
        #endif
        
        let start = UiLink().appStart(context: self, useMocks: useMocks, loggerLevel: Logger.Level.log)
        navigator = start.navigator
        
        #if DEBUG
        testsHelper.overrideCurrentScope()
        
//        DebugScopeCreator().createLimitedAppAccessShortcut(type: .stockist,
//                                                           isDocumentUploaded: false)
        
        DebugScopeCreator().uploadAadhaar(email: "d@qwe.by",
                                          phone: "375291341670")
        #endif
    }

    // MARK: UISceneSession Lifecycle

    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }

    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }


}

