//
//  NotificationDetailsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 3.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct NotificationDetailsScreen: View {
    let scope: NotificationScopePreview<DataNotificationDetails.TypeSafeSubscription,
                                        DataNotificationOption.Subscription>
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            Text(scope.notification.title)
                .medicoText(textWeight: .medium,
                            fontSize: 20,
                            multilineTextAlignment: .leading)
            
            VStack(alignment: .leading, spacing: 32) {
                switch self.scope {
                case is NotificationScopePreviewSubscriptionRequest:
                    SubscriptionDetails(details: scope.details) {
                        scope.changeOptions(option: $0)
                    }
                default:
                    EmptyView()
                }
                
                if scope.notification.actions.count > 0 {
                    HStack {
                        ForEach(scope.notification.actions, id: \.self) { action in
                            MedicoButton(localizedStringKey: action.actionStringId,
                                         height: 35,
                                         fontSize: 12,
                                         fontWeight: .bold,
                                         buttonColor: action.isHighlighted ? .yellow : .navigationBar) {
                                scope.selectAction(action: action)
                            }
                        }
                    }
                }
            }
            .padding(12)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(AppColor.white.color.cornerRadius(5))
            .scrollView()
            .textFieldsModifiers()
        }
        .padding(.horizontal, 16)
        .padding(.top, 32)
    }
    
    private struct SubscriptionDetails: View {
        @ObservedObject var details: SwiftDataSource<DataNotificationDetails.TypeSafeSubscription>
        let onPaymentOptionChange: (DataNotificationOption.Subscription) -> ()
        
        var body: some View {
            if details.value?.customerData.customerType == DataUserType.seasonBoy.serverValue {
                self.seasonBoyView
            }
            else {
                self.nonSeasonBoyView
            }
        }
        
        init(details: DataSource<DataNotificationDetails.TypeSafeSubscription>,
             onPaymentOptionChange: @escaping (DataNotificationOption.Subscription) -> ()) {
            self.details = SwiftDataSource(dataSource: details)
            
            self.onPaymentOptionChange = onPaymentOptionChange
        }
        
        private var nonSeasonBoyView: some View {
            guard let details = self.details.value else { return AnyView(EmptyView()) }
            
            return AnyView(
                VStack(alignment: .leading, spacing: 24) {
                    HStack(alignment: .top, spacing: 35) {
                        UserNameImage(username: details.customerData.tradeName)
                            .frame(width: 96, height: 96)
                        
                        VStack(alignment: .leading, spacing: 5) {
                            SmallAddressView(location: details.customerData.addressData.location)
                            
                            Text("25 km from you")
                                .medicoText(color: .lightBlue,
                                            multilineTextAlignment: .leading)
                            
//                            LocalizedText(localizationKey: "see_on_the_map",
//                                          textWeight: .bold,
//                                          color: .lightBlue,
//                                          multilineTextAlignment: .leading)
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 12) {
                        let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(details.customerData.phoneNumber)
                        UserInfoItemDetailsPanel(titleKey: "phone", valueKey: formattedPhone)
                        
                        if let gstin = details.customerData.gstin {
                            UserInfoItemDetailsPanel(titleKey: "gstin_number", valueKey: gstin)
                        }
                        
                        HStack(alignment: .top) {
                            LocalizedText(localizationKey: "payment_method",
                                          multilineTextAlignment: .leading)
                                .padding(.top, details.isReadOnly ? 0 : 6)
                            
                            if details.isReadOnly {
                                LocalizedText(localizationKey: details.option.paymentMethod.serverValue,
                                              textWeight: .medium,
                                              multilineTextAlignment: .leading)
                            }
                            else {
                                Spacer()
                                
                                PickerSelector(placeholder: "",
                                               chosenElement: details.option.paymentMethod.serverValue,
                                               data: self.getPaymentMethodOptions(),
                                               height: 30,
                                               chosenOptionTextWeight: .semiBold) {
                                    let paymentMethod: DataPaymentMethod = $0 == DataPaymentMethod.credit.serverValue ?
                                        .credit : .cash
                                    
                                    handleOptionChange(withPaymentMethod: paymentMethod,
                                                       withDiscountRate: details.option.discountRate,
                                                       withCreditDays: details.option.creditDays)
                                }
                                .background(
                                    RoundedRectangle(cornerRadius: 5)
                                        .stroke(AppColor.navigationBar.color)
                                )
                                .frame(width: 136)
                            }
                        }
                        
                        if details.isReadOnly {
                            UserInfoItemDetailsPanel(titleKey: "discount_rate:", valueKey: details.option.discountRate)
                        }
                        else {
                            let text = Binding(get: { details.option.discountRate },
                                               set: {
                                                handleOptionChange(withPaymentMethod: details.option.paymentMethod,
                                                                   withDiscountRate: $0,
                                                                   withCreditDays: details.option.creditDays)
                                               })
                            
                            NumberTextFieldPanel(titleLocalizedKey: "discount_rate:",
                                                 text: text)
                        }
                        
                        if details.option.paymentMethod == .credit {
                            if details.isReadOnly {
                                UserInfoItemDetailsPanel(titleKey: "credit_days:", valueKey: details.option.creditDays)
                            }
                            else {
                                let text = Binding(get: { details.option.creditDays },
                                                   set: {
                                                    handleOptionChange(withPaymentMethod: details.option.paymentMethod,
                                                                        withDiscountRate: details.option.discountRate,
                                                                        withCreditDays: $0)
                                                   })
                                
                                NumberTextFieldPanel(titleLocalizedKey: "credit_days:",
                                                     text: text)
                            }
                        }
                    }
                }
            )
        }
        
        private var seasonBoyView: some View {
            VStack(alignment: .leading, spacing: 5) {
                UserInfoItemDetailsPanel(titleKey: "email_address:",
                                         valueKey: details.value?.customerData.email ?? "")
                UserInfoItemDetailsPanel(titleKey: "address:",
                                         valueKey: details.value?.customerData.addressData.address ?? "")
                
                let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(details.value?.customerData.phoneNumber ?? "")
                UserInfoItemDetailsPanel(titleKey: "phone",
                                         valueKey: formattedPhone)
            }
        }
        
        private func getPaymentMethodOptions() -> [String] {
            var options = [String]()
            
            let iterator = DataPaymentMethod.values().iterator()
            while iterator.hasNext() {
                guard let paymentMethod = iterator.next_() as? DataPaymentMethod else { continue }
                
                options.append(paymentMethod.serverValue)
            }
            
            return options
        }
        
        private func handleOptionChange(withPaymentMethod paymentMethod: DataPaymentMethod,
                                        withDiscountRate discountRate: String,
                                        withCreditDays creditDays: String) {
            let newOption = DataNotificationOption.Subscription(
                paymentMethod: paymentMethod,
                discountRate: discountRate,
                creditDays: creditDays
            )
            
            onPaymentOptionChange(newOption)
        }
        
        struct NumberTextFieldPanel: View {
            let titleLocalizedKey: String
            let text: Binding<String>
            
            var body: some View {
                HStack {
                    LocalizedText(localizationKey: titleLocalizedKey)
                    
                    Spacer()
                    
                    CustomPlaceholderTextField(text: text,
                                               textWeight: .semiBold,
                                               elementsAlignment: .center) {
                        LocalizedText(localizationKey: "0",
                                      fontSize: 13,
                                      color: .placeholderGrey)
                    }
                    .keyboardType(.numberPad)
                    .padding(4)
                    .background(
                        RoundedRectangle(cornerRadius: 5)
                            .stroke(AppColor.navigationBar.color)
                    )
                    .frame(width: 72, height: 30)
                }
            }
        }
    }
}
