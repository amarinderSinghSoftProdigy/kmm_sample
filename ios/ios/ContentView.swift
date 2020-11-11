import SwiftUI
import core


struct ContentView: View {
    let authViewModel: AuthViewModelFacade
    
    @ObservedObject var testData: SwiftDatasource<DataTestData>
    
    init(authViewModel: AuthViewModelFacade) {
        self.authViewModel = authViewModel
        testData = SwiftDatasource(dataSource: authViewModel.testData)
    }
    
    var body: some View {
        Text(String(testData.value?.test ?? "")).onTapGesture {
            self.authViewModel.asyncTest()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView(authViewModel: MockAuthViewModel())
    }
}
