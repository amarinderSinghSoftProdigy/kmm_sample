import Firebase
import UIKit
import core

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    var navigator: UiNavigator!
    var notificationsManager: NotificationsManager!

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        FirebaseApp.configure()
        
        DIContainer.shared.initialize()
        
        registerForRemoteNotifications(with: application)
        
        setUpAppNavigator()
        
        return true
    }
    
    private func setUpAppNavigator() {
        #if DEBUG
        let useMocks = false
        let useNavigatorSafeCasts = false
        let useNetworkInterceptor = true
        let crashOnServerError = true
        #else
        let useMocks = false
        let useNavigatorSafeCasts = true
        let useNetworkInterceptor = false
        let crashOnServerError = false
        #endif
        
        let link = UiLink()
        let start = link.appStart(context: self,
                                  useMocks: useMocks,
                                  useNavigatorSafeCasts: useNavigatorSafeCasts,
                                  useNetworkInterceptor: useNetworkInterceptor,
                                  crashOnServerError: crashOnServerError,
                                  loggerLevel: Logger.Level.log,
                                  networkUrl: .dev)
        navigator = start.navigator
        notificationsManager = NotificationsManager(firebaseMessaging: start.firebaseMessaging)
        
        link.setStartingScope()
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
        notificationsManager.setDeviceToken(deviceToken)
    }
    
    // Called when the notification was fetched with the app in the foreground or background
    func application(_ application: UIApplication,
                     didReceiveRemoteNotification userInfo: [AnyHashable: Any],
                     fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        notificationsManager.handleNotificationFetch(withUserInfo: userInfo)
        notificationsManager.handleNotificationReceive(withUserInfo: userInfo)

        completionHandler(UIBackgroundFetchResult.newData)
    }
    
    // Called when the notification was received when the app was in the foreground
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                willPresent notification: UNNotification,
                                withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void) {
        let userInfo = notification.request.content.userInfo
        
        notificationsManager.handleNotificationReceive(withUserInfo: userInfo)
        
        completionHandler([.alert, .badge, .sound])
    }

    // Handles the user's response to a delivered notification
    func userNotificationCenter(_ center: UNUserNotificationCenter,
                                didReceive response: UNNotificationResponse,
                                withCompletionHandler completionHandler: @escaping () -> Void) {
        let userInfo = response.notification.request.content.userInfo
        
        notificationsManager.handleNotificationTap(withUserInfo: userInfo)

        if userInfo[NotificationsManager.isLocalNotificationKey] as? Bool != true {
            notificationsManager.handleNotificationReceive(withUserInfo: userInfo)
        }

        completionHandler()
    }
}
