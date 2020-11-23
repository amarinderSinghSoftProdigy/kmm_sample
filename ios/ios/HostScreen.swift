import SwiftUI
import core

struct HostScreen: View {
    @State var isSplashScreenActive = true
    
    let authViewModel: AuthViewModel
    
    @ObservedObject var currentScope: SwiftDatasource<Scope>
    
    init(authViewModel: AuthViewModel) {
        self.authViewModel = authViewModel
        currentScope = SwiftDatasource(dataSource: navigator.scope)
    }
    
    var body: some View {
        ZStack {
            if isSplashScreenActive {
                ZStack {
                    Color.primary.edgesIgnoringSafeArea(.all)
                    Image("medico_logo")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 150)
                }.onAppear {
                    DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                        withAnimation(.easeInOut(duration: 0.3)) {
                            self.isSplashScreenActive = false
                        }
                    }
                }
            } else {
                switch currentScope.value {
                case is Scope.LogIn:
                    AuthScreen(authViewModel: authViewModel, scope: currentScope.value as! Scope.LogIn)
                case is Scope.Main:
                    MainScreen(authViewModel: authViewModel)
                case is Scope.ForgetPassword.ForgetPasswordPhoneNumberInput:
                    AuthPhoneNumberInputScreen(authViewModel: authViewModel, scope: currentScope.value as! Scope.ForgetPassword.ForgetPasswordPhoneNumberInput)
                default:
                    Group {}
                }
                if currentScope.value!.isInProgress {
                    VisualEffectView(effect: UIBlurEffect(style: .dark))
                        .edgesIgnoringSafeArea(.all)
                    if #available(iOS 14.0, *) {
                        ProgressView().progressViewStyle(CircularProgressViewStyle(tint: .white))
                    }
                }
            }
        }
    }
}

struct VisualEffectView: UIViewRepresentable {
    var effect: UIVisualEffect?
    func makeUIView(context: UIViewRepresentableContext<Self>) -> UIVisualEffectView { UIVisualEffectView() }
    func updateUIView(_ uiView: UIVisualEffectView, context: UIViewRepresentableContext<Self>) {
        uiView.effect = effect
    }
}

//struct HostScreen_Previews: PreviewProvider {
//    static var previews: some View {
//        HostScreen(authViewModel: MockAuthViewModel())
//    }
//}
