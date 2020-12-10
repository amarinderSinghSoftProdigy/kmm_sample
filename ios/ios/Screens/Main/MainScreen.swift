import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        Text(LocalizedStringKey("Welcome!"))
        .userInfoNavigationBar(isLimitedAppAccess: scope.isLimitedAppAccess) {
            scope.tryLogOut()
        }
    }
    
    
}
