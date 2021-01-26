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
        guard let error = self.error.value else { return AnyView(content) }
        
        return AnyView(
            ZStack {
                content
                
                CustomAlert<AnyView>(titleKey: error.title,
                                     descriptionKey: error.body,
                                     button: .standard(action: { errorsHandler.dismissAlertError() }))
            }
        )
    }
}

// MARK: Notifications
struct NotificationAlert: ViewModifier {
    let notificationsHandler: CommonScopeWithNotifications
    let onDismiss: (() -> ())?
    
    @ObservedObject var notification: SwiftDataSource<ScopeNotification>
    
    init(notificationsHandler: CommonScopeWithNotifications,
         onDismiss: (() -> ())? = nil) {
        self.notificationsHandler = notificationsHandler
        self.onDismiss = onDismiss
        
        self.notification = SwiftDataSource(dataSource: notificationsHandler.notifications)
    }
    
    func body(content: Content) -> some View {
        guard let notification = self.notification.value else { return AnyView(content) }
        
        return AnyView(
            ZStack {
                content
                
                if notification.isSimple {
                    CustomAlert<AnyView>(titleKey: notification.title,
                                         descriptionKey: notification.body,
                                         button: .standard(action: {
                                            notificationsHandler.dismissNotification()
                                            onDismiss?()
                                         }))
                }
                else {
                    ComplexNotificationAlert(notification: notification)
                        .zIndex(10)
                }
            }
        )
    }
}

private struct ComplexNotificationAlert: View {
    let notification: ScopeNotification
    
    var body: some View {
        Group {
            switch self.notification {

            case let notification as ManagementScopeChoosePaymentMethod:
                ManagementScopeChoosePaymentMethodView(notification: notification)
                
            case let notification as ManagementScopeChooseNumberOfDays:
                ManagementScopeChooseNumberOfDaysView(notification: notification)
                
            case let notification as ManagementScopeThankYou:
                CustomAlert<AnyView>(titleKey: "thank_you_for_your_request",
                                     button: .init(text: "continue",
                                                   action: { notification.finishSubscribe() } ))
                
            default:
                EmptyView()
            }
        }
    }
    
    private struct ManagementScopeChoosePaymentMethodView: View {
        let notification: ManagementScopeChoosePaymentMethod
        
        @ObservedObject var paymentMethod: SwiftDataSource<DataPaymentMethod>
        
        var body: some View {
            CustomAlert(titleKey: "choose_payment_method_description",
                        button: .init(text: "save",
                                      action: { notification.sendRequest() })) {
                Group {
                    getOptionView(for: .credit)
                    
                    getOptionView(for: .cash)
                }
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
        
        let text: Binding<String>
        
        var body: some View {
            CustomAlert(titleKey: "edit_number_of_days",
                        button: .init(text: "save",
                                      action: { notification.save() })) {
                ZStack(alignment: .leading) {
                    if text.wrappedValue.isEmpty {
                        LocalizedText(localizationKey: "0",
                                      fontSize: 13,
                                      color: .placeholderGrey)
                    }
                    
                    TextField("", text: text)
                        .keyboardType(.numberPad)
                        .medicoText(multilineTextAlignment: .leading)
                }
                .padding(4)
                .frame(height: 25)
                .background(AppColor.white.color.cornerRadius(5))
                .padding([.horizontal, .bottom], 16)
            }
            .textFieldsModifiers()
        }
        
        init(notification: ManagementScopeChooseNumberOfDays) {
            self.notification = notification
            
            self.text = Binding(get: { "\(SwiftDataSource(dataSource: notification.days).value ?? 0)" },
                                set: { notification.changeDays(days: Int32($0) ?? 0) })
        }
    }
}

// MARK: Custom Alert
private struct CustomAlert<Content: View>: View {
    let titleKey: String
    let descriptionKey: String?
    
    let primaryButton: AlertButton
    
    let customBody: Content?
    
    var body: some View {
        ZStack {
            BlurEffectView()
                .edgesIgnoringSafeArea(.all)
            
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
         button: AlertButton) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.primaryButton = button
        
        self.customBody = nil
    }
    
    init(titleKey: String,
         descriptionKey: String? = nil,
         button: AlertButton,
         @ViewBuilder content: () -> Content) {
        self.titleKey = titleKey
        self.descriptionKey = descriptionKey
        
        self.customBody = content()
        
        self.primaryButton = button
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
