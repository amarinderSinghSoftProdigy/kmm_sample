import SwiftUI
import core

struct MainScreen: View {
    let scope: MainScope
    
    var body: some View {
        getCurrentView()
    }
    
    private func getCurrentView() -> some View {
        let view: AnyView
        
        switch self.scope {
        
        case let scope as MainScope.LimitedAccess:
            view = AnyView(LimitedAppAccessScreen(scope: scope))
            
        case let scope as MainScope.ProductInfo:
            view = AnyView(ProductDetails(scope: scope))
            
        default:
            view = AnyView(EmptyView())
        }
        
        if let navAndSearchScope = scope as? NavAndSearchMainScope {
            return AnyView(
                view
                    .userInfoNavigationBar(withScope: navAndSearchScope,
                                           withNavigationSection: scope.navigationSection)
            )
        }
    
        return view
    }
}
