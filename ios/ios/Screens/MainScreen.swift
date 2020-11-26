import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        Text(LocalizedStringKey("log_out")).onTapGesture {
            scope.tryLogOut()
        }
    }
}
