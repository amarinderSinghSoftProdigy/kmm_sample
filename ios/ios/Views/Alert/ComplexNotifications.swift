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
    
    @State var isSendEnabled = false
    
    var body: some View {
        let titleKey: String
        let button: CustomAlert<AnyView>.AlertButton
        var cancelButton: CustomAlert<AnyView>.AlertButton? = nil
        let body: AnyView
        var outsideTapAction = self.dismissAction

        switch self.notification {

        case let notification as ManagementScope.ChoosePaymentMethod:
            titleKey = "choose_payment_method_description"
            
            button = .init(text: "save",
                           isEnabled: isSendEnabled,
                           action: { notification.sendRequest() })
            body = AnyView(ManagementScopeChoosePaymentMethodView(notification: notification,
                                                                  isSendEnabled: $isSendEnabled))
            
            if let dismissAction = self.dismissAction {
                cancelButton = .init(text: "cancel",
                                     isEnabled: true,
                                     action: dismissAction)
            }
            
            outsideTapAction = { self.hideKeyboard() }
            
        case let notification as ManagementScope.AddRetailerAddress.Congratulations:
            titleKey = "congratulations"
            button = .init(text: "retailers_list",
                           isEnabled: true,
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
                        cancelButton: cancelButton,
                        outsideTapAction: outsideTapAction) {
                body
            }
        )
    }
    
    private struct ManagementScopeChoosePaymentMethodView: View {
        let notification: ManagementScope.ChoosePaymentMethod
        
        @ObservedObject var paymentMethod: SwiftDataSource<DataPaymentMethod>
        @ObservedObject var daysNumber: SwiftDataSource<NSString>
        
        var body: some View {
            Group {
                getOptionView(for: .credit)
                
                getOptionView(for: .cash)
            }
        }
        
        init(notification: ManagementScope.ChoosePaymentMethod,
             isSendEnabled: Binding<Bool>) {
            self.notification = notification
            
            self.paymentMethod = SwiftDataSource(dataSource: notification.paymentMethod)
            self.daysNumber = SwiftDataSource(dataSource: notification.creditDays)
            
            SwiftDataSource(dataSource: notification.isSendEnabled).onValueDidSet = {
                isSendEnabled.wrappedValue = $0 == true
            }
        }
        
        private func getOptionView(for option: DataPaymentMethod) -> some View {
            VStack(spacing: 0) {
                CustomAlert<AnyView>.Separator()
                
                Button(action: { notification.changePaymentMethod(paymentMethod: option) }) {
                    VStack(spacing: 12) {
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
                        
                        if paymentMethod.value == option && paymentMethod.value == .credit {
                            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "no_of_credit_days",
                                                         text: self.daysNumber.value as String?,
                                                         onTextChange: { notification.changeCreditDays(days: $0) },
                                                         height: 40,
                                                         keyboardType: .numberPad)
                        }
                    }
                    .padding(.horizontal, 17)
                    .padding(.vertical, 10)
                }
                .frame(minHeight: 44)
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
}
