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
        VStack(spacing: 0) {
            ZStack {
                AppColor.navigationBar.color
                    .edgesIgnoringSafeArea(.all)

                navigationBarContent
                    .padding([.leading, .trailing], 10)
            }
            .frame(height: 44)

            AppColor.lightGrey.color.frame(height: 1)

            content
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
        
        let spacing: CGFloat = 19
        
        return AnyView(
            Group {
                switch info {
                case let simpleBarInfo as TabBarInfo.Simple:
                    ZStack {
                        HStack {
                            self.getScopeButton(for: simpleBarInfo.icon)
                            
                            Spacer()
                        }
                        
                        if let titleId = simpleBarInfo.titleId {
                            LocalizedText(localizationKey: titleId,
                                          textWeight: .semiBold,
                                          fontSize: 17)
                        }
                    }
                    
                case let searchBarInfo as TabBarInfo.Search:
                    HStack(spacing: spacing) {
                        self.getScopeButton(for: searchBarInfo.icon)
                        
                        HStack(spacing: spacing - 4) {
                            SearchBar(style: .small)
                                .onTapGesture {
                                    searchBarInfo.goToSearch()
                                }
                            
                            Button(action: { }) {
                                let cartObjectsNumberPadding: CGFloat = 6
                                
                                ZStack(alignment: .topTrailing) {
                                    Image("Cart")
                                        .padding(cartObjectsNumberPadding)
                                    
                                    ZStack {
                                        AppColor.white.color
                                            .cornerRadius(7)
                                        
                                        Text("10")
                                            .medicoText(textWeight: .bold,
                                                        fontSize: 12,
                                                        color: .red)
                                    }
                                    .frame(width: 14, height: 14)
                                }
                                .padding(-cartObjectsNumberPadding)
                            }
                        }
                    }
                    .padding([.leading, .trailing], 6)
                    
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
