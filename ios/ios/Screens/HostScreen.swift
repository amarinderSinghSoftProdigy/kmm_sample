import SwiftUI
import core

//MARK: Host Screen
struct HostScreen: View {
    @ObservedObject var currentScope: SwiftDataSource<Scope.Host>
    
    var body: some View {
        if let scope = currentScope.value {
            BaseScopeView(scope: scope)
        }
    }
    
    init() {
        currentScope = SwiftDataSource(dataSource: UIApplication.shared.navigator.scope)
    }
}

//MARK: Base Scope View
struct BaseScopeView: View {
    let scope: Scope.Host
    
    var body: some View {
        
        print("Bottom Sheet: \(scope.bottomSheet)")
        
        return ZStack(alignment: .topLeading) {
            AppColor.primary.color.edgesIgnoringSafeArea(.all)
                .hideKeyboardOnTap()
            
            currentView

            BottomSheetView(bottomSheet: scope.bottomSheet,
                            dismissBottomSheet: { scope.dismissBottomSheet() })

            NotificationsListener()

            ErrorAlert(errorsHandler: scope)
            
            ActivityScreen(isInProgress: scope.isInProgress)
        }
    }
    
    var currentView: AnyView {
        switch scope {
        
        case let scopeValue as LogInScope:
            return AnyView(AuthScreen(scope: scopeValue))
            
        case let scopeValue as WelcomeScope:
            return AnyView(WelcomeScreen(welcomeOption: WelcomeOption.Thanks { scopeValue.accept() },
                                         userName: scopeValue.fullName))
            
        case let scopeValue as TabBarScope:
            return AnyView(TabBarScreen(tabBarScope: scopeValue))
            
        default:
            return AnyView(EmptyView())
        }
    }
    
    init(scope: Scope.Host) {
        self.scope = scope
    }
    
    private struct ActivityScreen: View {
        @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
        
        var body: some View {
            ZStack {
                if let isInProgress = self.isInProgress.value,
                   isInProgress == true {
                    ActivityView()
                }
            }
            .animation(.linear(duration: 0.2))
        }

        init(isInProgress: DataSource<KotlinBoolean>) {
            self.isInProgress = SwiftDataSource(dataSource: isInProgress)
        }
    }
    
    private struct NotificationsListener: View {
        @EnvironmentObject var notificationObserver: NotificationObservable
        
        var body: some View {
            ZStack {
                if let notificationData = self.notificationObserver.data {
                    NotificationAlert(notificationsHandler: notificationData.notificationsHandler)
                }
            }
            .animation(.default)
        }
    }
}

//MARK: TabBar Screen
struct TabBarScreen: View {
    
    let tabBarScope: TabBarScope
    
    @ObservedObject var scope: SwiftDataSource<Scope.ChildTabBar>
    
    var body: some View {
       currentView
          .navigationBar(withNavigationSection: tabBarScope.navigationSection,
                           withNavigationBarInfo: tabBarScope.tabBar,
                           handleGoBack: { tabBarScope.goBack() })
    }
    
    init(tabBarScope: TabBarScope) {
        self.tabBarScope = tabBarScope
        self.scope = SwiftDataSource(dataSource: tabBarScope.childScope)
    }
    
    private var currentView: some View {
        
        print("Scope Vaue Changed to: \(scope.value)")
        
        return Group {
            switch scope.value {
                
            case let scope as OtpScope:
                OtpFlowScreen(scope: scope)
                
            case let scope as PasswordScope:
                PasswordScreen(scope: scope)
                
            case let scope as SignUpScope:
                SignUpScreen(scope: scope)
                
            case let scope as LimitedAccessScope:
                LimitedAppAccessScreen(scope: scope)
                
            case let scope as ProductInfoScope:
                ProductDetails(scope: scope)
                
            case let scope as BuyProductScope<DataWithTradeName>:
                BuyProductScreen(scope: scope)
                
            case let scope as SettingsScope:
                SettingsScreen(scope: scope)
                
            case let scope as ManagementScope.User:
                UserManagementScreen(scope: scope)
                
            case let scope as ManagementScope.AddRetailer:
                AddRetailerScreen(scope: scope)
                
            case let scope as DashboardScope:
                DashboardScreen(scope: scope)
                
            case let scope as NotificationScope.All:
                NotificationsScreen(scope: scope)
                
            case let scope as NotificationScopePreview<DataNotificationDetails.TypeSafeSubscription,
                                                       DataNotificationOption.Subscription>:
                NotificationDetailsScreen(scope: scope)
                
            case let scope as StoresScope:
                StoresScreen(scope: scope)
                
            case let scope as SearchScope:
                GlobalSearchScreen(scope: scope)
                
            case let scope as CartScope:
                CartScreen(scope: scope)
                
            case let scope as HelpScope:
                HelpScreen(scope: scope)
                
            case let scope as CartPreviewScope:
                PlaceOrderScreen(scope: scope)
                
            case let scope as CartOrderCompletedScope:
                CompletedCartOrderScreen(scope: scope)
                
            case let scope as OrdersScope:
                OrdersScreen(scope: scope)
                
            case let scope as ViewOrderScope:
                ViewOrderScreen(scope: scope)
                
            case let scope as ConfirmOrderScope:
                ConfirmOrderScreen(scope: scope)
                
            case let scope as InvoicesScope:
                InvoicesScreen(scope: scope)
                
            case let scope as ViewInvoiceScope:
                InvoiceDetailsScreen(scope: scope)
                
            case let scope as OrderPlacedScope:
                OrderPlacedScreen(scope: scope)
                
            case let scope as InStoreSellerScope:
                InStoreSellers(scope: scope)
                
            case let scope as InStoreUsersScope:
                InStoreUsers(scope: scope)
                
            case let scope as InStoreAddUserScope:
                InStoreCreateUser(scope: scope)
                
            case let scope as InStoreProductsScope:
                InStoreProducts(scope: scope)
                
            default:
                EmptyView()
            }
        }
    }
}

//MARK: BottomSheet View
struct BottomSheetView: View {
    @ObservedObject var bottomSheet: SwiftDataSource<BottomSheet>

    let dismissBottomSheet: () -> ()

    var body: some View {
        ZStack(alignment: .topLeading) {
            switch bottomSheet.value {

            case let uploadBottomSheet as BottomSheet.UploadDocuments:
                Color.clear.filePicker(bottomSheet: uploadBottomSheet,
                                       onBottomSheetDismiss: dismissBottomSheet)

            case let managementItemSheet as BottomSheet.PreviewManagementItem:
                Color.clear
                    .modifier(EntityInfoBottomSheet(bottomSheet: managementItemSheet,
                                                    onBottomSheetDismiss: dismissBottomSheet))
                
            case let modifyOrderEntrySheet as BottomSheet.ModifyOrderEntry:
                Color.clear
                    .modifier(ModifyOrderEntryBottomSheet(bottomSheet: modifyOrderEntrySheet,
                                                          onBottomSheetDismiss: dismissBottomSheet))
                
            case let previewStockistSheet as BottomSheet.PreviewStockist:
                Color.clear
                    .modifier(PreviewStockistBottomSheet(bottomSheet: previewStockistSheet,
                                                         onBottomSheetDismiss: dismissBottomSheet))
            
            case let viewTaxInfoSheet as BottomSheet.ViewTaxInfo:
                Color.clear
                    .modifier(ViewTaxInfoBottomSheet(bottomSheet: viewTaxInfoSheet,
                                                         onBottomSheetDismiss: dismissBottomSheet))
                
            case let viewItemTaxSheet as BottomSheet.ViewItemTax:
                Color.clear
                    .modifier(ViewItemTaxBottomSheet(bottomSheet: viewItemTaxSheet,
                                                         onBottomSheetDismiss: dismissBottomSheet))
            case let inStoreProductSheet as BottomSheet.InStoreViewProduct:
                Color.clear
                    .modifier(InStoreViewProductBottomSheet(bottomSheet: inStoreProductSheet,
                                                         onBottomSheetDismiss: dismissBottomSheet))
            default:
                Color.clear
            }
        }
        .animation(.linear(duration: 0.2))
    }
    
    init(bottomSheet: DataSource<BottomSheet>,
         dismissBottomSheet: @escaping () -> ()) {
        self.bottomSheet = SwiftDataSource(dataSource: bottomSheet)

        self.dismissBottomSheet = dismissBottomSheet
    }
}
