//
//  AuthPhoneVerification.swift
//  ios
//
//  Created by Dasha Gurinovich on 26.11.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct AuthPhoneVerification: View {
    let scope: ForgetPasswordScope.AwaitVerification
    let geometry: GeometryProxy
    
    @State var code: String = ""
    @ObservedObject var timerValue: SwiftDatasource<KotlinLong>
    
    @Binding var isCodeIncorrect: Bool
    @Binding var isResendUnsuccessful: Bool
    
    var body: some View {
        ZStack {
            VStack {
                let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(scope.phoneNumber)
                Text("verification_code_sent_hint \(formattedPhone)")
                    .modifier(MedicoText(textWeight: .medium, color: .textGrey))
                    .multilineTextAlignment(.center)
                    .padding([.trailing, .leading], geometry.size.width * 0.15)
                
                if let timerValue = timerValue.value as? Double,
                   timerValue > 0 && scope.attemptsLeft > 0 {
                    Text("\(TimeInterval(milliseconds: timerValue).timeString)")
                        .modifier(MedicoText(textWeight: .bold, fontSize: 15))
                        .padding([.top, .bottom])
                }
                else {
                    Text("attempts_left \(Int(scope.attemptsLeft))")
                        .modifier(MedicoText(textWeight: .bold, fontSize: 15, color: .lightBlue))
                        .padding([.top, .bottom])
                }
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                             text: $code,
                                             keyboardType: .numberPad)
                    .padding([.top, .bottom])
                
                MedicoButton(localizedStringKey: "submit",
                             isEnabled: !code.isEmpty && scope.attemptsLeft > 0) {
                    scope.submitOtp(otp: code)
                }
            }
            .padding()
            
            let otpHeight: CGFloat = 64
            ResendOtpView(resendAction: resendOtp)
                .frame(height: otpHeight)
                .position(x: geometry.size.width / 2, y: geometry.size.height - otpHeight)
        }
        
        .alert($isCodeIncorrect,
               withTitleKey: "otp_error",
               withMessageKey: "otp_error_description",
               withButtonTextKey: "okay")
        
        .alert($isResendUnsuccessful,
               withTitleKey: "otp_error",
               withMessageKey: "something_went_wrong",
               withButtonTextKey: "okay")
        
        .navigationBarTitle(LocalizedStringKey("phone_verification"), displayMode: .inline)
    }
    
    init(scope: ForgetPasswordScope.AwaitVerification, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        self.timerValue = SwiftDatasource(dataSource: scope.resendTimer)
        
        _isCodeIncorrect = Binding.constant(scope.codeValidity.isFalse)
        _isResendUnsuccessful = Binding.constant(scope.resendSuccess.isFalse)
    }
    
    private func resendOtp() {
        scope.resendOtp()
    }
}

struct ResendOtpView: View {
    let resendAction: () -> ()
    
    var body: some View {
        ZStack {
            AppColor.white.color
                
            HStack(alignment: .center) {
                Text(LocalizedStringKey("didnt_get_code"))
                    .modifier(MedicoText(textWeight: .medium))
                
                Text(LocalizedStringKey("resend"))
                    .modifier(MedicoText(textWeight: .semiBold, color: .lightBlue))
                    .onTapGesture {
                        resendAction()
                    }
            }
        }
    }
}
