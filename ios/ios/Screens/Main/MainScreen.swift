import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        getCurrentView()
            .userInfoNavigationBar(isLimitedAppAccess: scope.isLimitedAppAccess) {
                scope.tryLogOut()
            }
    }
    
    private func getCurrentView() -> some View {
        let view: AnyView
        
        view = AnyView(LimitedAppAccessScreen())
        
        return view
    }
}
