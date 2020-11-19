import SwiftUI
import core

struct MainScreen: View {
    let authViewModel: AuthViewModelFacade
    
    var body: some View {
        Text(LocalizedStringKey("log_out")).onTapGesture {
            authViewModel.logOut()
        }
    }
}

struct MainScreen_Previews: PreviewProvider {
    static var previews: some View {
        MainScreen(authViewModel: MockAuthViewModel())
    }
}
