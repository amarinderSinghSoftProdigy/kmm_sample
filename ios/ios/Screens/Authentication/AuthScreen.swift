import SwiftUI
import Combine
import core

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
            ZStack(alignment: .bottom) {
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
                        .frame(idealWidth: .infinity, maxHeight: 44, alignment: Alignment.top)
                }

                AuthTab(
                    phoneOrEmail: credentials.value!.phoneNumberOrEmail,
                    password: credentials.value!.password,
                    scope: scope
                )
                    .frame(maxWidth: .infinity)
                    .background(appColor: .primary)
                    .padding()
                    .alert(isPresented: $isError) {
                        Alert(title: Text("Log in Error"), message: Text("Log in or password is wrong. Please try again or restore your password"), dismissButton: Alert.Button.default(Text("OKAY")))
                    }
            }.edgesIgnoringSafeArea(.top)
        }
    }
}

struct AuthTab: View {
    @State var phoneOrEmail: String
    @State var password: String
    
    let scope: LogInScope
    
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
                    guard let phoneNumberOrEmail = scope.credentials.value?.phoneNumberOrEmail,
                          pe != phoneNumberOrEmail else { return }
                    
                    scope.updateAuthCredentials(emailOrPhone: pe, password: password)
                }
            
            FloatingPlaceholderSecureField(placeholderLocalizedStringKey: "password", text:  $password)
                .onReceive(Just(password)) { pass in
                    guard let password = scope.credentials.value?.password,
                          pass != password else { return }
                    
                    scope.updateAuthCredentials(emailOrPhone: phoneOrEmail, password: pass)
                }
            
            Text(LocalizedStringKey("forgot_password"))
                .font(Font.caption)
                .padding(.top, 4)
                .foregroundColor(appColor: .lightBlue)
                .onTapGesture {
                    scope.goToForgetPassword()
                }
            
            Button(action: {
                scope.tryLogIn()
            }) {
                Text(LocalizedStringKey("log_in"))
                    .fontWeight(Font.Weight.semibold)
                    .frame(maxWidth: .infinity)
            }.medicoButton(isEnabled: true)
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
}
