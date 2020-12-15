import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    @ObservedObject var user: SwiftDataSource<DataUser>
    
    var body: some View {
        getCurrentView()
    }
    
    init(scope: MainScope) {
        self.scope = scope
        
        self.user = SwiftDataSource(dataSource: scope.user)
    }
    
    private func getCurrentView() -> some View {
        guard let user = self.user.value else { return AnyView(EmptyView()) }
        
        let view: AnyView
        
        switch self.scope {
        
        case let scope as MainScope.LimitedAccess:
            view = AnyView(LimitedAppAccessScreen(scope: scope, user: user))
            
        default:
            view = AnyView(EmptyView())
        }
        
        return AnyView(
            view
                .userInfoNavigationBar(isLimitedAppAccess: scope is MainScope.LimitedAccess,
                                       forUser: user) {
                    scope.tryLogOut()
                }
        )
    }
}
