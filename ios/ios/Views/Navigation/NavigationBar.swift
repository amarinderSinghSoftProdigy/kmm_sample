//
//  NavigationBar.swift
//  Medico
//
//  Created by Dasha Gurinovich on 11.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct NavigationBar: ViewModifier {
    private let navigationBarContent: AnyView?

    private let navigationBarData: NavigationBarData?

    func body(content: Content) -> some View {
        if let navigationBarContent = self.navigationBarContent {
            return AnyView(
                content
                    .modifier(_BaseNavigationBarModifier(navigationBarContent: navigationBarContent))
            )
        }

        guard let data = self.navigationBarData else { return AnyView(content) }

        return AnyView (
            content
                .modifier(
                    _SlidingNavigationBarModifier(navigationSection: data.navigationSection,
                                                  navigationBarInfo: data.navigationBarInfo,
                                                  handleGoBack: data.handleGoBack)
                )
        )
    }

    init(navigationSection: DataSource<NavigationSection>,
         navigationBarInfo: DataSource<TabBarInfo>,
         handleGoBack: @escaping () -> ()) {
        self.navigationBarData = NavigationBarData(navigationSection: navigationSection,
                                                   navigationBarInfo: navigationBarInfo,
                                                   handleGoBack: handleGoBack)

        self.navigationBarContent = nil
    }

    init(navigationBarContent: AnyView) {
        self.navigationBarContent = navigationBarContent

        self.navigationBarData = nil
    }

    private struct NavigationBarData {
        let navigationSection: DataSource<NavigationSection>

        let navigationBarInfo: DataSource<TabBarInfo>
        let handleGoBack: (() -> ())
    }
}

private struct _BaseNavigationBarModifier: ViewModifier {
    let navigationBarContent: AnyView
    
    func body(content: Content) -> some View {
        ZStack(alignment: .topLeading) {
            content
                .padding(.top, 45)
            
            VStack(spacing: 0) {
                ZStack {
                    AppColor.navigationBar.color
                        .edgesIgnoringSafeArea(.all)

                    navigationBarContent
                        .padding([.leading, .trailing], 10)
                }
                .frame(height: 44)

                AppColor.lightGrey.color.frame(height: 1)
            }
        }
    }
}

struct _SlidingNavigationBarModifier: ViewModifier {
    @State private var showsSlidingPanel = false

    @ObservedObject var navigationSection: SwiftDataSource<NavigationSection>

    private let navigationBarInfo: DataSource<TabBarInfo>
    private let handleGoBack: (() -> ())

    func body(content: Content) -> some View {
        let navigationBar = _CustomizedNavigationBar(navigationBarInfo: navigationBarInfo,
                                                     closeSlidingPanel: closeSlidingPanel,
                                                     handleGoBack: handleGoBack)
        
        let view = AnyView(
            content
                .modifier(_BaseNavigationBarModifier(navigationBarContent: AnyView(navigationBar)))
        )

        guard let navigationSection = self.navigationSection.value else { return view }

        return AnyView (
            view
                .slidingNavigationPanelView(withNavigationSection: navigationSection,
                                            showsSlidingPanel: showsSlidingPanel,
                                            closeSlidingPanel: closeSlidingPanel)
        )
    }

    init(navigationSection: DataSource<NavigationSection>,
         navigationBarInfo: DataSource<TabBarInfo>,
         handleGoBack: @escaping () -> ()) {
        self.navigationSection = SwiftDataSource(dataSource: navigationSection)

        self.navigationBarInfo = navigationBarInfo
        self.handleGoBack = handleGoBack
    }

    private func closeSlidingPanel(isClosed: Bool) {
        withAnimation {
            self.showsSlidingPanel = !isClosed
        }
    }
}

private struct _CustomizedNavigationBar: View {
    @ObservedObject var navigationBarInfo: SwiftDataSource<TabBarInfo>
    
    let closeSlidingPanel: (Bool) -> ()
    let handleGoBack: () -> ()
    
    var body: some View {
        guard let info = navigationBarInfo.value else { return AnyView(EmptyView()) }
        print("INFO: \(info)")
        return AnyView(
            Group {
                switch info {
                case let simpleBarInfo as TabBarInfo.Simple:
                    SimpleTabBar(simpleBarInfo: simpleBarInfo,
                                 getScopeButton: getScopeButton)
                    
                case let searchBarInfo as TabBarInfo.Search:
                    SearchTabBar(searchBarInfo: searchBarInfo,
                                 getScopeButton: getScopeButton)
                    
                case let searchBarInfo as TabBarInfo.ActiveSearch:
                    ActiveSearchTabBar(searchBarInfo: searchBarInfo) {
                        handleGoBack()
                    }
                    
                default:
                    EmptyView()
                }
            }
        )
    }
    
    init(navigationBarInfo: DataSource<TabBarInfo>,
         closeSlidingPanel: @escaping (Bool) -> (),
         handleGoBack: @escaping () -> ()) {
        self.navigationBarInfo = SwiftDataSource(dataSource: navigationBarInfo)
        
        self.closeSlidingPanel = closeSlidingPanel
        self.handleGoBack = handleGoBack
    }
    
    private func getScopeButton(for icon: ScopeIcon) -> some View {
        Button(action: { onButtonClick(withScopeIcon: icon) }) {
            if icon == .back {
                HStack(spacing: 5) {
                    icon.image
                    
                    LocalizedText(localizationKey: "back", fontSize: 17)
                }
            }
            else {
                icon.image
            }
        }
    }
    
    private func onButtonClick(withScopeIcon scopeIcon: ScopeIcon) {
        switch scopeIcon {
        case .back:
            handleGoBack()
        case .hamburger:
            closeSlidingPanel(false)
        default:
            return
        }
    }
    
    private struct SimpleTabBar<Content: View>: View {
        let simpleBarInfo: TabBarInfo.Simple
        
        let getScopeButton: (ScopeIcon) -> Content
        
        @ObservedObject private var cartData: CartData
        
        var body: some View {
            let cartObjectsNumberPadding: CGFloat = 6
            
            ZStack {
                HStack {
                    getScopeButton(simpleBarInfo.icon)
                    
                    Spacer()
                    
                    if let cartItemsCount = cartData.cartItemsCount as? Int {
                        Button(action: { simpleBarInfo.goToCart() }) {

                            ZStack(alignment: .topTrailing) {
                                Image("Cart")
                                    .padding(cartObjectsNumberPadding)

                                if cartItemsCount > 0 {
                                    ZStack {
                                        AppColor.white.color
                                            .cornerRadius(7)

                                        Text(String(cartItemsCount))
                                            .medicoText(textWeight: .bold,
                                                        fontSize: 12,
                                                        color: .red)
                                    }
                                    .frame(width: 14, height: 14)
                                }
                            }
                        }
                        .padding(-cartObjectsNumberPadding)
                    }
                }
                .padding([.leading, .trailing], 6)
                
                Group {
                    if let title = simpleBarInfo.title {
                        switch title {
                        case let staticTitle as StringResource.Static:
                            LocalizedText(localizationKey: staticTitle.id,
                                          textWeight: .semiBold,
                                          fontSize: 17)
                            
                        case let rawTitle as StringResource.Raw:
                            Text(rawTitle.string ?? "")
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 17)
                            
                        default:
                            EmptyView()
                        }
                    }
                }
                .padding(.vertical, 3)
                .frame(maxWidth: 220)
            }
        }
        
        init(simpleBarInfo: TabBarInfo.Simple,
             getScopeButton: @escaping (ScopeIcon) -> Content) {
            self.simpleBarInfo = simpleBarInfo
            self.getScopeButton = getScopeButton
            
            let cartData = CartData()
            /*if let cartItemsCount = simpleBarInfo.cartItemsCount {
                SwiftDataSource(dataSource: cartItemsCount).onValueDidSet = {
                    cartData.cartItemsCount = $0
                }
            }*/
            self.cartData = cartData
        }
        
        private class CartData: ObservableObject {
            @Published var cartItemsCount: KotlinInt?
        }
    }
    
    private struct SearchTabBar<Content: View>: View {
        private let spacing: CGFloat = 19
        
        let searchBarInfo: TabBarInfo.Search
        let getScopeButton: (ScopeIcon) -> Content
        
        @ObservedObject var cartItemsCount: SwiftDataSource<KotlinInt>
        
        var body: some View {
            HStack(spacing: spacing) {
                getScopeButton(searchBarInfo.icon)
                
                HStack(spacing: spacing - 4) {
                    SearchBar(placeholderLocalizationKey: "search_products",
                              style: .small)
                        .onTapGesture {
                            searchBarInfo.goToSearch()
                        }
                    
                    Button(action: { searchBarInfo.goToCart() }) {
                        let cartObjectsNumberPadding: CGFloat = 6
                        
                        ZStack(alignment: .topTrailing) {
                            Image("Cart")
                                .padding(cartObjectsNumberPadding)
                            
                            if let cartItemsCount = self.cartItemsCount.value as? Int,
                               cartItemsCount > 0 {
                                ZStack {
                                    AppColor.white.color
                                        .cornerRadius(7)
                                    
                                    Text(String(cartItemsCount))
                                        .medicoText(textWeight: .bold,
                                                    fontSize: 12,
                                                    color: .red)
                                }
                                .frame(width: 14, height: 14)
                            }
                        }
                        .padding(-cartObjectsNumberPadding)
                    }
                }
            }
            .padding([.leading, .trailing], 6)
        }
        
        init(searchBarInfo: TabBarInfo.Search,
             getScopeButton: @escaping (ScopeIcon) -> Content) {
            self.searchBarInfo = searchBarInfo
            self.getScopeButton = getScopeButton
            
            self.cartItemsCount = SwiftDataSource(dataSource: searchBarInfo.cartItemsCount!)
        }
    }
    
    private struct ActiveSearchTabBar: View {
        @EnvironmentObject var scrollData: ListScrollData
        
        let searchBarInfo: TabBarInfo.ActiveSearch
        let onCancelTap: () -> ()
        
        @ObservedObject var search: SwiftDataSource<NSString>
        @ObservedObject var activeFilterIds: SwiftDataSource<NSArray>
        
        private var isFilterApplied: Bool {
            if let appliedFiltersNumber = activeFilterIds.value?.count {
                return appliedFiltersNumber > 0
            }
            
            return false
        }
        
        var body: some View {
            HStack {
                SearchBar(placeholderLocalizationKey: "search_products",
                          searchText: search.value,
                          style: .small,
                          showsCancelButton: false,
                          trailingButton: SearchBar.SearchBarButton(button: .filter(isHighlighted: isFilterApplied,
                                                                                    { searchBarInfo.toggleFilter() })),
                          onTextChange: { value, isFromKeyboard in searchBarInfo.searchProduct(input: value,
                                                                                               withAutoComplete: !isFromKeyboard) })
                
                Button(LocalizedStringKey("cancel")) {
                    scrollData.clear(list: .globalSearchProducts)
                    
                    onCancelTap()
                }
                .medicoText(fontSize: 17,
                            color: .blue)
            }
            .padding([.leading, .trailing], 6)
        }
        
        init(searchBarInfo: TabBarInfo.ActiveSearch,
             onCancelTap: @escaping () -> ()) {
            self.searchBarInfo = searchBarInfo
            self.onCancelTap = onCancelTap
            
            self.search = SwiftDataSource(dataSource: searchBarInfo.search)
            self.activeFilterIds = SwiftDataSource(dataSource: searchBarInfo.activeFilterIds)
        }
    }
}

extension ScopeIcon {
    var image: Image? {
        switch self {
        case .back:
            return Image("Back")
            
        case .hamburger:
            return Image("Menu")
            
        default:
            return nil
        }
    }
}
