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
        .navigationBarTitle(LocalizedStringKey("phone_verification"), displayMode: .inline)
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
    
    @ObservedObject var attemptsLeft: SwiftDatasource<KotlinInt>
    @ObservedObject var timerValue: SwiftDatasource<KotlinLong>
    
    var body: some View {
        VStack {
            Text("verification_code_sent_hint \(phoneNumber)")
                .modifier(MedicoText(textWeight: .medium, color: .textGrey))
                .multilineTextAlignment(.center)
                .padding([.trailing, .leading], geometry.size.width * 0.15)
            
            if let attemptsLeft = self.attemptsLeft.value as? Int {
                if let timerValue = timerValue.value as? Double,
                   timerValue > 0 && attemptsLeft > 0 {
                    Text("\(TimeInterval(milliseconds: timerValue).timeString)")
                        .modifier(MedicoText(textWeight: .bold, fontSize: 15))
                        .padding([.top, .bottom])
                }
                else {
                    Text("attempts_left \(attemptsLeft)")
                        .modifier(MedicoText(textWeight: .bold, fontSize: 15, color: .lightBlue))
                        .padding([.top, .bottom])
                }
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                             text: code,
                                             onTextChange: { newValue in code = newValue},
                                             keyboardType: .numberPad)
                    .padding([.top, .bottom])
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
        
        self.attemptsLeft = SwiftDatasource(dataSource: leftAttempts)
        self.timerValue = SwiftDatasource(dataSource: timerValue)
    }
}

fileprivate struct ResendOtpView: View {
    @ObservedObject var isResendActive: SwiftDatasource<KotlinBoolean>
    
    let resendAction: () -> ()
    
    var body: some View {
        ZStack {
            AppColor.white.color
                
            HStack(alignment: .center) {
                Text(LocalizedStringKey("didnt_get_code"))
                    .modifier(MedicoText(textWeight: .medium))
                
                let resendColor: AppColor = isResendActive.value == true ? .lightBlue : .grey
                
                Text(LocalizedStringKey("resend"))
                    .modifier(MedicoText(textWeight: .semiBold, color: resendColor))
                    .onTapGesture {
                        if isResendActive.value != true { return }
                        
                        resendAction()
                    }
            }
        }
    }
    
    init(isResendActive: DataSource<KotlinBoolean>,
         resendAction: @escaping () -> ()) {
        self.isResendActive = SwiftDatasource(dataSource: isResendActive)
        
        self.resendAction = resendAction
    }
}
