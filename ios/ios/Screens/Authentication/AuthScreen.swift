import SwiftUI
import Combine
import core_arm64

struct AuthScreen: View {
    let scope: LogInScope
    @Binding var isError: Bool
    
    @ObservedObject var credentials: SwiftDatasource<DataAuthCredentials>
    
    init(scope: LogInScope) {
        self.scope = scope
        credentials = SwiftDatasource(dataSource: scope.credentials)
        _isError = Binding.constant(scope.success.isFalse)
    }
    
    var body: some View {
        Background {
            ZStack {
                let blackRectangleHeight: CGFloat = 44
                
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

                if let credentialsValue = self.credentials.value {
                    AuthTab(
                        scope: scope,
                        phoneOrEmail: credentialsValue.phoneNumberOrEmail,
                        password: credentialsValue.password
                    )
                    .frame(maxWidth: .infinity)
                    .background(appColor: .primary)
                    .padding()
                    .padding(.top, blackRectangleHeight)
                    .alert(isPresented: $isError) {
                        Alert(title: Text("Log in Error"), message: Text("Log in or password is wrong. Please try again or restore your password"), dismissButton: Alert.Button.default(Text("OKAY")))
                    }
                }
            }.edgesIgnoringSafeArea(.top)
        }
    }
}

struct AuthTab: View {
    @State var phoneOrEmail: String
    @State var password: String
    
    let scope: LogInScope
    let initialPhoneOrEmail: String
    let initialPassword: String
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text(LocalizedStringKey("log_in"))
                    .font(Font.system(size: 24))
                    .fontWeight(Font.Weight.semibold)
                    .foregroundColor(appColor: .darkBlue)
                Spacer()
                Image("medico_logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 135, alignment: Alignment.trailing)
            }.frame(maxWidth: .infinity).padding([.bottom])
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number_or_email", text: $phoneOrEmail, keyboardType: .emailAddress)
                .autocapitalization(UITextAutocapitalizationType.none)
                .disableAutocorrection(true)
                .onReceive(Just(phoneOrEmail)) { pe in
                    if pe == initialPhoneOrEmail { return }
                    
                    scope.updateAuthCredentials(emailOrPhone: pe, password: password)
                }
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password", text:  $password)
                .onReceive(Just(password)) { pass in
                    if pass == initialPassword { return }
                    
                    scope.updateAuthCredentials(emailOrPhone: phoneOrEmail, password: pass)
                }
            
            Text(LocalizedStringKey("forgot_password"))
                .font(Font.caption)
                .padding(.top, 4)
                .foregroundColor(appColor: .lightBlue)
                .onTapGesture {
                    scope.goToForgetPassword()
                }
            
            MedicoButton(action: {
                scope.tryLogIn()
            }, localizedStringKey: "log_in")
            .padding(.top)
            
            Text(LocalizedStringKey("sign_up_to_medico"))
                .font(Font.caption)
                .underline()
                .padding(.top, 4)
                .padding(.bottom)
                .foregroundColor(appColor: .lightBlue)
        }
        .padding(20)
        .navigationBarHidden(true)
    }
    
    init(scope: LogInScope,
         phoneOrEmail: String,
         password: String) {
        self.scope = scope
        
        self.initialPhoneOrEmail = phoneOrEmail
        self.initialPassword = password
        
        _phoneOrEmail = State(initialValue: phoneOrEmail)
        _password = State(initialValue: password)
    }
}
