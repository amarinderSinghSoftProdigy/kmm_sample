//
//  AuthPhoneVerification.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct OtpPhoneVerification: View {
    let scope: OtpScope.AwaitVerification
    let geometry: GeometryProxy
    
    var body: some View {
        ZStack {
            VStack {
                Spacer()
                
                let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(scope.phoneNumber)
                OtpDetailsView(phoneNumber: formattedPhone,
                               geometry: geometry,
                               leftAttempts: scope.attemptsLeft,
                               timerValue: scope.resendTimer) { code in
                    scope.submitOtp(otp: code)
                }
                
                Spacer()
            }
            
            VStack {
                Spacer()
                
                let otpHeight: CGFloat = 64
                ResendOtpView(isResendActive: scope.resendActive,
                              resendAction: resendOtp)
                    .frame(height: otpHeight)
            }
        }
        .screenLogger(withScreenName: "OtpPhoneVerification",
                      withScreenClass: OtpPhoneVerification.self)
    }
    
    init(scope: OtpScope.AwaitVerification, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
    }
    
    private func resendOtp() {
        scope.resendOtp()
    }
}

fileprivate struct OtpDetailsView: View {
    let phoneNumber: String
    let geometry: GeometryProxy
    
    let buttonAction: (String) -> ()
    
    @State private var code: String = ""
    
    @ObservedObject var attemptsLeft: SwiftDataSource<KotlinInt>
    @ObservedObject var timerValue: SwiftDataSource<KotlinLong>
    
    var body: some View {
        VStack {
            LocalizedText(localizedStringKey: LocalizedStringKey("verification_code_sent_hint \(phoneNumber)"),
                          testingIdentifier: "verification_code_sent_hint",
                          textWeight: .medium,
                          color: .textGrey)
                .padding(.horizontal, geometry.size.width * 0.15)
            
            if let attemptsLeft = self.attemptsLeft.value as? Int {
                if let timerValue = timerValue.value as? Double,
                   timerValue > 0 && attemptsLeft > 0 {
                    Text("\(TimeInterval(milliseconds: timerValue).timeString)")
                        .medicoText(textWeight: .bold,
                                    fontSize: 15,
                                    testingIdentifier: "timer")
                        .padding(.vertical)
                }
                else {
                    LocalizedText(localizedStringKey: LocalizedStringKey("attempts_left \(attemptsLeft)"),
                                  testingIdentifier: "attempts_left",
                                  textWeight: .bold,
                                  fontSize: 15,
                                  color: .lightBlue)
                        .padding(.vertical)
                }
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                             text: code,
                                             onTextChange: { newValue in code = newValue},
                                             keyboardType: .numberPad)
                    .padding(.vertical)
                    .textContentType(.oneTimeCode)
                
                MedicoButton(localizedStringKey: "submit",
                             isEnabled: !code.isEmpty && attemptsLeft > 0) {
                    buttonAction(code)
                }
            }
        }
        .padding()
    }
    
    init(phoneNumber: String,
         geometry: GeometryProxy,
         leftAttempts: DataSource<KotlinInt>,
         timerValue: DataSource<KotlinLong>,
         buttonAction: @escaping (String) -> ()) {
        self.phoneNumber = phoneNumber
        self.geometry = geometry
        
        self.buttonAction = buttonAction
        
        self.attemptsLeft = SwiftDataSource(dataSource: leftAttempts)
        self.timerValue = SwiftDataSource(dataSource: timerValue)
    }
}

fileprivate struct ResendOtpView: View {
    @ObservedObject var isResendActive: SwiftDataSource<KotlinBoolean>
    
    let resendAction: () -> ()
    
    var body: some View {
        ZStack {
            AppColor.white.color
                
            HStack(alignment: .center) {
                LocalizedText(localizationKey: "didnt_get_code",
                              textWeight: .medium)
                
                let resendColor: AppColor = isResendActive.value == true ? .lightBlue : .grey1
                
                LocalizedText(localizationKey: "resend",
                              textWeight: .semiBold,
                              color: resendColor)
                    .onTapGesture {
                        resendAction()
                    }
                    .disabled(isResendActive.value != true)
            }
        }
    }
    
    init(isResendActive: DataSource<KotlinBoolean>,
         resendAction: @escaping () -> ()) {
        self.isResendActive = SwiftDataSource(dataSource: isResendActive)
        
        self.resendAction = resendAction
    }
}
