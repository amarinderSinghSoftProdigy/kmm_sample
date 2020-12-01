import SwiftUI
import Combine
import core

struct AuthScreen: View {
    private let blackRectangleHeight: CGFloat = 44
    
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
            ZStack (alignment: .bottom) {
                self.background

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
                    .frame(maxHeight: .infinity)
                }
                
                Text("© Copyright mediostores.com 2021")
                    .modifier(MedicoText(textWeight: .semiBold, fontSize: 16, color: .white))
                    .opacity(0.8)
                    .padding(.bottom, 30)
            }.edgesIgnoringSafeArea(.top)
        }
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
                    .modifier(MedicoText(textWeight: .bold, fontSize: 24))
                
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
                .modifier(MedicoText(color: .lightBlue))
                .padding(.top, 4)
                .onTapGesture {
                    scope.goToForgetPassword()
                }
            
            MedicoButton(localizedStringKey: "log_in") {
                scope.tryLogIn()
            }
            .padding(.top)
            
            (Text(LocalizedStringKey("sign_up"))
                .font(.custom("Barlow-Bold", size: 14))
            + Text(LocalizedStringKey("to_medico")))
                .underline()
                .modifier(MedicoText(color: .lightBlue))
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
         phoneOrEmail: String,
         password: String) {
        self.scope = scope
        
        self.initialPhoneOrEmail = phoneOrEmail
        self.initialPassword = password
        
        _phoneOrEmail = State(initialValue: phoneOrEmail)
        _password = State(initialValue: password)
    }
}
