import Firebase
import UIKit
import core

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var navigator: UiNavigator!

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        
        DIContainer.shared.initialize()
        
        registerForRemoteNotifications(with: application)
        
        setUpAppNavigator()
        
        return true
    }
    
    private func setUpAppNavigator() {
        #if DEBUG
        let testsHelper = TestsHelper()
        let useMocks = testsHelper.testingEnabled
        let useNavigatorSafeCasts = false
        let useNetworkInterceptor = true
        #else
        let useMocks = false
        let useNavigatorSafeCasts = true
        let useNetworkInterceptor = false
        #endif
        
        let link = UiLink()
        let start = link.appStart(context: self,
                                  useMocks: useMocks,
                                  useNavigatorSafeCasts: useNavigatorSafeCasts,
                                  useNetworkInterceptor: useNetworkInterceptor,
                                  loggerLevel: Logger.Level.log)
        navigator = start.navigator
        link.setStartingScope()
        
        #if DEBUG
        testsHelper.overrideCurrentScope()
        #endif
    }
    
    private func registerForRemoteNotifications(with application: UIApplication) {
        UNUserNotificationCenter.current().delegate = self

        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(options: authOptions) { _, _ in }
        
        application.registerForRemoteNotifications()
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

extension AppDelegate: UNUserNotificationCenterDelegate {
    func application(_ application: UIApplication,
                     didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        DIContainer.shared.resolve(type: NotificationsService.self)?
            .setDeviceToken(deviceToken)
    }
}
