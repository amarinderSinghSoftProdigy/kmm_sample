import SwiftUI
import core


struct ContentView: View {
    let authViewModel: AuthViewModelFacade
    
    @ObservedObject var authState: SwiftDatasource<DataAuthState>
    @ObservedObject var credentials: SwiftDatasource<DataAuthCredentials>
    
    init(authViewModel: AuthViewModelFacade) {
        self.authViewModel = authViewModel
        authState = SwiftDatasource(dataSource: authViewModel.state)
        credentials = SwiftDatasource(dataSource: authViewModel.credentials)
    }
    
    var body: some View {
        Text(credentials.value!.phoneNumberOrEmail).onTapGesture {
            self.authViewModel.updateAuthCredentials(credentials: DataAuthCredentials(phoneNumberOrEmail: "sdfsd", password: "dfsdf"))
            self.authViewModel.tryLogIn()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(authViewModel: MockAuthViewModel())
    }
}
