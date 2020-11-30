import SwiftUI
import core_arm64

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        Text(LocalizedStringKey("log_out")).onTapGesture {
            scope.tryLogOut()
        }
    }
}
