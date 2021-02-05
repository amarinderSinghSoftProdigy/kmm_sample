//
//  AlertViewModifiers.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

// MARK: Errors
struct ErrorAlert: ViewModifier {
    let errorsHandler: Scope.Host
    
    @ObservedObject var error: SwiftDataSource<DataErrorCode>
    
    init(errorsHandler: Scope.Host) {
        self.errorsHandler = errorsHandler
        
        self.error = SwiftDataSource(dataSource: errorsHandler.alertError)
    }
    
    func body(content: Content) -> some View {
        ZStack {
            content
            
            ZStack {
                if let error = self.error.value {
                    let dismissAction = { errorsHandler.dismissAlertError() }
                    
                    CustomAlert<AnyView>(titleKey: error.title,
                                         descriptionKey: error.body,
                                         button: .standard(action: dismissAction),
                                         dismissAction: dismissAction)
                }
            }
            .animation(.default)
        }
    }
}

// MARK: Notifications
class NotificationObservable: ObservableObject {
    var data: Data?
    
    class Data {
        let notificationsHandler: CommonScopeWithNotifications
        let onDismiss: (() -> ())?
        
        init(notificationsHandler: CommonScopeWithNotifications,
             onDismiss: (() -> ())? = nil) {
            self.notificationsHandler = notificationsHandler
            self.onDismiss = onDismiss
        }
    }
}

struct NotificationAlertSender: ViewModifier {
    @EnvironmentObject var notificationObserver: NotificationObservable
    
    let notificationsHandler: CommonScopeWithNotifications
    
    func body(content: Content) -> some View {
        content
            .onAppear {
                self.notificationObserver.data = .init(notificationsHandler: notificationsHandler)
            }
            .onDisappear {
                self.notificationObserver.data = nil
            }
    }
}

struct NotificationAlert: ViewModifier {
    let notificationsHandler: CommonScopeWithNotifications
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    
    init(notificationsHandler: CommonScopeWithNotifications) {
        self.notificationsHandler = notificationsHandler
        
        self.notification = SwiftDataSource(dataSource: notificationsHandler.notifications)
    }
    
    func body(content: Content) -> some View {
        ZStack {
            content
            
            ZStack {
                if let notification = self.notification.value {
                    let dismissAction = { _ = notificationsHandler.dismissNotification() }
                    
                    if notification.isSimple {
                        CustomAlert<AnyView>(titleKey: notification.title,
                                             descriptionKey: notification.body,
                                             button: .standard(action: dismissAction),
                                             dismissAction: notification.isDismissible ? dismissAction : nil)
                    }
                    else {
                        ComplexNotificationAlert(notification: notification,
                                                 dismissAction: notification.isDismissible ? dismissAction : nil)
                    }
                }
            }
            .animation(.default)
        }
    }
}

private struct ComplexNotificationAlert: View {
    let notification: ScopeNotification
    let dismissAction: (() -> ())?
    
    var body: some View {
        let titleKey: String
        let button: CustomAlert<AnyView>.AlertButton
        let body: AnyView
        var dismissAction = self.dismissAction

        switch self.notification {

        case let notification as ManagementScopeChoosePaymentMethod:
            titleKey = "choose_payment_method_description"
            button = .init(text: "save",
                           action: { notification.sendRequest() })
            body = AnyView(ManagementScopeChoosePaymentMethodView(notification: notification))

        case let notification as ManagementScopeChooseNumberOfDays:
            titleKey = "edit_number_of_days"
            button = .init(text: "save",
                           action: { notification.save() })
            body = AnyView(ManagementScopeChooseNumberOfDaysView(notification: notification))
            
            if dismissAction == nil { dismissAction = { self.hideKeyboard() } }

        default:
            return AnyView(EmptyView())
        }

        return AnyView(
            CustomAlert(titleKey: titleKey,
                        button: button,
                        dismissAction: dismissAction) {
                body
            }
        )
    }
    
    private struct ManagementScopeChoosePaymentMethodView: View {
        let notification: ManagementScopeChoosePaymentMethod
        
        @ObservedObject var paymentMethod: SwiftDataSource<DataPaymentMethod>
        
        var body: some View {
            Group {
                getOptionView(for: .credit)
                
                getOptionView(for: .cash)
            }
        }
        
        init(notification: ManagementScopeChoosePaymentMethod) {
            self.notification = notification
            
            self.paymentMethod = SwiftDataSource(dataSource: notification.paymentMethod)
        }
        
        private func getOptionView(for option: DataPaymentMethod) -> some View {
            VStack(spacing: 0) {
                CustomAlert<AnyView>.Separator()
                
                Button(action: { notification.changePaymentMethod(paymentMethod: option) }) {
                    HStack {
                        LocalizedText(localizationKey: getLocalizationKey(for: option),
                                      fontSize: 17,
                                      color: .black)

                        Spacer()

                        if paymentMethod.value == option {
                            Image(systemName: "checkmark")
                                .foregroundColor(appColor: .lightBlue)
                        }
                    }
                    .padding(.horizontal, 17)
                }
                .frame(height: 44)
            }
        }
        
        private func getLocalizationKey(for option: DataPaymentMethod) -> String {
            switch option {
            case .cash:
                return "cash_on_delivery"
                
            case .credit:
                return "credit"
                
            default:
                return ""
            }
        }
    }
    
    private struct ManagementScopeChooseNumberOfDaysView: View {
        let notification: ManagementScopeChooseNumberOfDays
        
        @ObservedObject var daysNumber: SwiftDataSource<KotlinInt>
        
        var body: some View {
            let text: Binding<String> = Binding(
                get: {
                    if let days = self.daysNumber.value {
                        return "\(days)"
                    }
                    
                    return ""
                },
                set: {
                    if let days = Int32($0) {
                        notification.changeDays(days: days)
                    }
                })
            
            CustomPlaceholderTextField(text: text) {
                LocalizedText(localizationKey: "0",
                              fontSize: 13,
                              color: .placeholderGrey)
            }
            .keyboardType(.numberPad)
            .padding(4)
            .frame(height: 25)
            .background(AppColor.white.color.cornerRadius(5))
            .padding([.horizontal, .bottom], 16)
        }
        
        init(notification: ManagementScopeChooseNumberOfDays) {
            self.notification = notification

            self.daysNumber = SwiftDataSource(dataSource: notification.days)
        }
    }
}

// MARK: Custom Alert
private struct CustomAlert<Content: View>: View {
    let titleKey: String
    let descriptionKey: String?
    
    let primaryButton: AlertButton
    let dismissAction: (() -> ())?
    
    let customBody: Content?
    
    var body: some View {
        ZStack {
            BlurEffectView()
                .edgesIgnoringSafeArea(.all)
                .onTapGesture {
                    dismissAction?()
                }
            
            VStack(spacing: 0) {
                VStack(spacing: 6) {
                    LocalizedText(localizationKey: titleKey,
                                  textWeight: .semiBold,
                                  fontSize: 17,
                                  color: .black)
                    
                    if let descriptionKey = self.descriptionKey,
                       !descriptionKey.isEmpty {
                        LocalizedText(localizationKey: descriptionKey,
                                      color: .black)
                    }
                }
                .padding(20)
                
                self.customBody

                VStack(spacing: 0) {
                    Separator()
                    
                    Button(action: { primaryButton.action?() }) {
                        ZStack {
                            Color.clear
                            
                            LocalizedText(localizationKey: primaryButton.text,
                                          textWeight: .medium,
                                          fontSize: 17,
                                          color: .lightBlue)
                        }
                    }
                    .frame(height: 44)
                }
            }
            .frame(width: 270)
            .background(AppColor.white.color.opacity(0.82).cornerRadius(13))
        }
    }
    
    init(titleKey: String,
         descriptionKey: String? = nil,
         button: AlertButton,
         dismissAction: (() -> ())? = nil) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.primaryButton = button
        self.dismissAction = dismissAction
        
        self.customBody = nil
    }
    
    init(titleKey: String,
         descriptionKey: String? = nil,
         button: AlertButton,
         dismissAction: (() -> ())? = nil,
         @ViewBuilder content: () -> Content) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.customBody = content()
        
        self.primaryButton = button
        self.dismissAction = dismissAction
    }
    
    struct Separator: View {
        var body: some View {
            AppColor.grey1.color
                .opacity(0.8)
                .frame(height: 0.5)
        }
    }
    
    struct AlertButton {
        let text: String
        let action: (() -> ())?
        
        static func standard(action: (() -> ())?) -> AlertButton {
            AlertButton(text: "okay", action: action)
        }
    }
}
