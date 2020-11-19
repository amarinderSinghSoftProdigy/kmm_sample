import SwiftUI
import core

struct HostScreen: View {
    let authViewModel: AuthViewModelFacade
    
    @ObservedObject var authState: SwiftDatasource<DataAuthState>
    
    init(authViewModel: AuthViewModelFacade) {
        self.authViewModel = authViewModel
        authState = SwiftDatasource(dataSource: authViewModel.authState)
    }
    
    var body: some View {
        if authState.value == DataAuthState.success {
            MainScreen(authViewModel: authViewModel)
        } else {
            AuthScreen(authViewModel: authViewModel)
        }
    }
}

struct HostScreen_Previews: PreviewProvider {
    static var previews: some View {
        HostScreen(authViewModel: MockAuthViewModel())
    }
}
