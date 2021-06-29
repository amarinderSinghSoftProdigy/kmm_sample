import SwiftUI
import Combine
import core

struct AuthScreen: View {
    private let blackRectangleHeight: CGFloat = 44
    
    let scope: LogInScope
    
    @ObservedObject var credentials: SwiftDataSource<DataAuthCredentials>
    
    init(scope: LogInScope) {
        self.scope = scope
        
        credentials = SwiftDataSource(dataSource: scope.credentials)
    }
    
    var body: some View {
        ZStack(alignment: .bottom) {
            AppColor.navigationBar.color
                .edgesIgnoringSafeArea(.all)
            
            VStack {
                Group {
                    Spacer()
                    
                    VStack(spacing: 50) {
                        Image("medico_logo")
                            .resizable()
                            .scaledToFit()
                            .frame(width: 216, height: 54)

                        if let credentialsValue = self.credentials.value {
                            AuthTab(
                                scope: scope,
                                credentials: credentialsValue
                            )
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 45)
                    .background(
                        RoundedRectangle(cornerRadius: 16)
                            .fill(appColor: .white)
                    )
                    
                    Spacer()
                    
                    self.createAccountView
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .padding(.horizontal, 20)
                .background(appColor: .lightBlue)
                
                LocalizedText(localizationKey: "copyright",
                              textWeight: .medium,
                              fontSize: 12)
                    .padding(.vertical, 14)
            }
        }
        .textFieldsModifiers()
        .screenLogger(withScreenName: "AuthScreen",
                      withScreenClass: AuthScreen.self)
    }
    
    private var createAccountView: some View {
        VStack(spacing: 13) {
            LocalizedText(localizationKey: "new_to_medico",
                          textWeight: .medium,
                          color: .white)
            
            MedicoButton(localizedStringKey: "create_account",
                         isEnabled: true,
                         width: 287,
                         height: 42,
                         fontColor: .white,
                         buttonColor: .clear) {
                scope.goToSignUp()
            }
            .strokeBorder(.white, fill: .clear, lineWidth: 2)
        }
        .padding(.bottom, 65)
    }

    private struct AuthTab: View {
        let scope: LogInScope
        let credentials: DataAuthCredentials
        
        var body: some View {
            VStack(alignment: .trailing, spacing: 24) {
                FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number_or_email",
                                             text: credentials.phoneNumberOrEmail,
                                             onTextChange: updateLogin,
                                             keyboardType: .emailAddress,
                                             disableAutocorrection: true,
                                             autocapitalization: .none)
                    .strokeBorder(.blueWhite,
                                  fill: .white,
                                  lineWidth: 2)
                
                FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password",
                                               text: credentials.password,
                                               onTextChange: updatePassword)
                    .textContentType(.password)
                    .strokeBorder(.blueWhite,
                                  fill: .white,
                                  lineWidth: 2)
                
                LocalizedText(localizationKey: "forgot_password",
                              textWeight: .bold,
                              color: .lightBlue)
                    .onTapGesture {
                        scope.goToForgetPassword()
                    }
                
                MedicoButton(localizedStringKey: "log_in",
                             isEnabled: !credentials.phoneNumberOrEmail.isEmpty && !credentials.password.isEmpty,
                             fontColor: .white,
                             buttonColor: .lightBlue) {
                    scope.tryLogIn()
                }
                .padding(.top, 20)
            }
            .frame(maxWidth: 500)
        }
        
        init(scope: LogInScope,
             credentials: DataAuthCredentials) {
            self.scope = scope
            self.credentials = credentials
        }
        
        private func updateLogin(withNewValue newValue: String) {
            let password = self.credentials.password
                
            scope.updateAuthCredentials(emailOrPhone: newValue, password: password)
        }
        
        private func updatePassword(withNewValue newValue: String) {
            let login = self.credentials.phoneNumberOrEmail
                
            scope.updateAuthCredentials(emailOrPhone: login, password: newValue)
        }
    }
}
