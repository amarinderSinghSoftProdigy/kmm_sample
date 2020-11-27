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
    
    var body: some View {
        ZStack {
            VStack {
                let formattedPhone = PhoneNumberUtil.shared.getFormattedPhoneNumber(scope.phoneNumber)
                Text("verification_code_sent_hint \(formattedPhone)")
                    .font(.custom("Barlow-Regular", size: 14))
                    .foregroundColor(appColor: .textGrey)
                    .multilineTextAlignment(.center)
                    .padding([.trailing, .leading], geometry.size.width * 0.15)
                
                if let timerValue = timerValue.value {
                    Text("\(TimeInterval(milliseconds: Double(truncating: timerValue)).timeString)")
                        .font(.custom("Barlow-Medium", size: 15))
                        .foregroundColor(appColor: .darkBlue)
                        .padding([.top, .bottom])
                }
                
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "verification_code",
                                             text: $code,
                                             keyboardType: .numberPad)
                    .padding([.top, .bottom])
                
                MedicoButton(action: {
                    scope.submitOtp(otp: code)
                }, localizedStringKey: "submit", isEnabled: !code.isEmpty)
            }
            .padding()
            
            let otpHeight: CGFloat = 64
            ResendOtpView(resendAction: resendOtp)
                .frame(height: otpHeight)
                .position(x: geometry.size.width / 2, y: geometry.size.height - otpHeight)
        }
        .navigationBarTitle(LocalizedStringKey("phone_verification"), displayMode: .inline)
    }
    
    init(scope: ForgetPasswordScope.AwaitVerification, geometry: GeometryProxy) {
        self.scope = scope
        self.geometry = geometry
        
        self.timerValue = SwiftDatasource(dataSource: scope.resendTimer)
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
                    .modifier(MedicoText())
                
                Text(LocalizedStringKey("resend"))
                    .modifier(MedicoText(textWeight: .medium, color: .lightBlue))
                    .onTapGesture {
                        resendAction()
                    }
            }
        }
    }
}
