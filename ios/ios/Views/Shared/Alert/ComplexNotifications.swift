//
//  ComplexNotifications.swift
//  Medico
//
//  Created by Dasha Gurinovich on 10.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ComplexNotificationAlert: View {
    let notification: ScopeNotification
    let dismissAction: (() -> ())?
    
    var body: some View {
        let titleKey: String
        let button: CustomAlert<AnyView>.AlertButton
        let body: AnyView
        var dismissAction = self.dismissAction

        switch self.notification {

        case let notification as ManagementScope.ChoosePaymentMethod:
            titleKey = "choose_payment_method_description"
            button = .init(text: "save",
                           action: { notification.sendRequest() })
            body = AnyView(ManagementScopeChoosePaymentMethodView(notification: notification))

        case let notification as ManagementScope.ChooseNumberOfDays:
            titleKey = "edit_number_of_days"
            button = .init(text: "save",
                           action: { notification.save() })
            body = AnyView(ManagementScopeChooseNumberOfDaysView(notification: notification))
            
            if dismissAction == nil { dismissAction = { self.hideKeyboard() } }
            
        case let notification as PreviewUserScope.Congratulations:
            titleKey = "congratulations"
            button = .init(text: "retailers_list",
                           action: dismissAction)
            body = AnyView(
                LocalizedText(localizedStringKey: LocalizedStringKey("retailer_added_template \(notification.tradeName)"),
                              testingIdentifier: "alert_text",
                              fontSize: 13,
                              color: .black)
                    .padding(.bottom, 20)
            )

        default:
            return AnyView(
                SimpleNotificationAlert(notification: notification,
                                        dismissAction: dismissAction))
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
        let notification: ManagementScope.ChoosePaymentMethod
        
        @ObservedObject var paymentMethod: SwiftDataSource<DataPaymentMethod>
        
        var body: some View {
            Group {
                getOptionView(for: .credit)
                
                getOptionView(for: .cash)
            }
        }
        
        init(notification: ManagementScope.ChoosePaymentMethod) {
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
        let notification: ManagementScope.ChooseNumberOfDays
        
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
        
        init(notification: ManagementScope.ChooseNumberOfDays) {
            self.notification = notification

            self.daysNumber = SwiftDataSource(dataSource: notification.days)
        }
    }
}
