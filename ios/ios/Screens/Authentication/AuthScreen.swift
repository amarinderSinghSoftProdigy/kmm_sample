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
        ZStack (alignment: .bottom) {
            self.background

            if let credentialsValue = self.credentials.value {
                AuthTab(
                    scope: scope,
                    credentials: credentialsValue
                )
                .frame(maxWidth: .infinity)
                .background(appColor: .primary)
                .padding()
                .padding(.top, blackRectangleHeight)
                .frame(maxHeight: .infinity)
            }
            
            LocalizedText(localizedStringKey: "copyright",
                          textWeight: .semiBold,
                          fontSize: 16,
                          color: .white)
                .opacity(0.8)
                .padding(.bottom, 30)
        }
        .keyboardResponder()
    }
    
    var background: some View {
        ZStack(alignment: .top) {
            AppColor.darkBlue.color.edgesIgnoringSafeArea(.all)
            
            Image("auth_logo")
                .resizable()
                .scaledToFit()
            
            let darkBlue = AppColor.darkBlue.color
            Rectangle()
                .fill(LinearGradient(gradient: Gradient(colors: [darkBlue.opacity(0.0), darkBlue.opacity(1.0)]), startPoint: .top, endPoint: .bottom))
                .aspectRatio(1.03878, contentMode: .fit)
            
            AppColor.lightBlue.color.opacity(0.7).edgesIgnoringSafeArea(.all)
            
            Rectangle()
                .fill(Color.black)
                .opacity(0.2)
                .frame(idealWidth: .infinity, maxHeight: blackRectangleHeight, alignment: Alignment.top)
        }
        .edgesIgnoringSafeArea(.top)
    }
}

struct AuthTab: View {
    let scope: LogInScope
    let credentials: DataAuthCredentials
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                LocalizedText(localizedStringKey: "log_in",
                              textWeight: .bold,
                              fontSize: 24)
                
                Spacer()
                
                Image("medico_logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 135, alignment: Alignment.trailing)
            }.frame(maxWidth: .infinity).padding([.bottom])
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number_or_email",
                                         text: credentials.phoneNumberOrEmail,
                                         onTextChange: updateLogin,
                                         keyboardType: .emailAddress)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password",
                                           text: credentials.password,
                                           onTextChange: updatePassword)
                .textContentType(.password)
            
            LocalizedText(localizedStringKey: "forgot_password",
                          color: .lightBlue)
                .padding(.top, 4)
                .onTapGesture {
                    scope.goToForgetPassword()
                }
            
            MedicoButton(localizedStringKey: "log_in",
                         isEnabled: !credentials.phoneNumberOrEmail.isEmpty && !credentials.password.isEmpty) {
                scope.tryLogIn()
            }
            .padding(.top)
            
            (Text(LocalizedStringKey("sign_up"))
                .font(.custom(TextWeight.bold.fontName, size: 14))
            + Text(LocalizedStringKey("to_medico")))
                .underline()
                .modifier(MedicoText(color: .lightBlue))
                .testingIdentifier("sign_up_to_medico")
                .padding(.top, 4)
                .padding(.bottom)
                .onTapGesture {
                    scope.goToSignUp()
                }
        }
        .padding(20)
        .navigationBarHidden(true)
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
