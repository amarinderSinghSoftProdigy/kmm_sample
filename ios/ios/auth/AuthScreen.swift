import SwiftUI
import Combine
import core

struct AuthScreen: View {
    let authViewModel: AuthViewModelFacade
    
    @ObservedObject var authState: SwiftDatasource<DataAuthState>
    @ObservedObject var credentials: SwiftDatasource<DataAuthCredentials>
    
    init(authViewModel: AuthViewModelFacade) {
        self.authViewModel = authViewModel
        authState = SwiftDatasource(dataSource: authViewModel.authState)
        credentials = SwiftDatasource(dataSource: authViewModel.credentials)
    }
    
    var body: some View {
        ZStack(alignment: .bottom) {
            ZStack(alignment: .top) {
                Color.secondary.edgesIgnoringSafeArea(.all)
                
                Image("auth_logo")
                    .resizable()
                    .scaledToFit()
                
                Rectangle()
                    .fill(LinearGradient(gradient: Gradient(colors: [Color(hex: "003657").opacity(0.0), Color(hex: "003657").opacity(1.0)]), startPoint: .top, endPoint: .bottom))
                    .aspectRatio(1.03878, contentMode: .fit)
                
                Color(hex: "0084D4").opacity(0.7).edgesIgnoringSafeArea(.all)
                
                Rectangle()
                    .fill(Color.black)
                    .opacity(0.2)
                    .frame(idealWidth: .infinity, maxHeight: 44, alignment: Alignment.top)
            }
            
            let isError = Binding.constant(authState.value == DataAuthState.error)
            
            AuthTab(
                phoneOrEmail: credentials.value!.phoneNumberOrEmail,
                password: credentials.value!.password,
                isLoading: authState.value == DataAuthState.inProgress,
                authViewModel: authViewModel
            )
                .frame(maxWidth: .infinity)
                .background(Color.primary)
                .padding()
                .alert(isPresented: isError.projectedValue) {
                    Alert(title: Text("Log in Error"), message: Text("Log in or password is wrong. Please try again or restore your password"), dismissButton: Alert.Button.default(Text("OKAY")))
                }
        }.edgesIgnoringSafeArea(.top)
    }
}

struct AuthTab: View {
    @State var phoneOrEmail: String
    @State var password: String
    var isLoading: Bool
    
    let authViewModel: AuthViewModelFacade
    
    var body: some View {
        VStack(alignment: .leading) {
            HStack {
                Text(LocalizedStringKey("log_in"))
                    .foregroundColor(Color.secondary)
                    .font(Font.system(size: 24))
                    .fontWeight(Font.Weight.semibold)
                Spacer()
                Image("medico_logo")
                    .resizable()
                    .scaledToFit()
                    .frame(width: 135, alignment: Alignment.trailing)
            }.frame(maxWidth: .infinity).padding([.bottom])
            TextField(LocalizedStringKey("phone_number_or_email"), text: $phoneOrEmail)
                .authInputField()
                .onReceive(Just(phoneOrEmail)) { pe in
                    if pe != authViewModel.credentials.value!.phoneNumberOrEmail {
                        authViewModel.updateAuthCredentials(emailOrPhone: pe, password: password)
                    }
                }
            SecureField(LocalizedStringKey("password"), text: $password)
                .authInputField()
                .onReceive(Just(password)) { pass in
                    if pass != authViewModel.credentials.value!.password {
                        authViewModel.updateAuthCredentials(emailOrPhone: phoneOrEmail, password: pass)
                    }
                }
            Text(LocalizedStringKey("forgot_password"))
                .font(Font.caption)
                .padding(.top, 4)
                .foregroundColor(Color.medicoLightBlue)
            Button(action: {
                authViewModel.tryLogIn()
            }) {
                ZStack(alignment: .trailing) {
                    Text(LocalizedStringKey("log_in"))
                        .fontWeight(Font.Weight.semibold)
                        .frame(maxWidth: .infinity)
                    if isLoading, #available(iOS 14.0, *) {
                        ProgressView()
                    }
                }
            }.medicoButton(isEnabled: !isLoading)
            .padding(.top)
            Text(LocalizedStringKey("sign_up_to_medico"))
                .font(Font.caption)
                .underline()
                .padding(.top, 4)
                .padding(.bottom)
                .foregroundColor(Color.medicoLightBlue)
        }.padding(20)
    }
}


struct AuthInputField: ViewModifier {
    func body(content: Content) -> some View {
        content
            .autocapitalization(UITextAutocapitalizationType.none)
            .disableAutocorrection(true)
            .frame(height: 48)
            .padding([.leading, .trailing], 12)
            .background(RoundedRectangle(cornerRadius: 8).fill(Color.white))
        
    }
}

struct MedicoButton: ViewModifier {
    let isEnabled: Bool
    
    func body(content: Content) -> some View {
        content
            .frame(maxWidth: .infinity)
            .padding()
            .disabled(!isEnabled)
            .foregroundColor(Color.secondary)
            .background(RoundedRectangle(cornerRadius: 8).fill(isEnabled ? Color.medicoYellow : Color.gray))
        
    }
}

struct AuthScreen_Previews: PreviewProvider {
    static var previews: some View {
        AuthScreen(authViewModel: MockAuthViewModel())
    }
}
