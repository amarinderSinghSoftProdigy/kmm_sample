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
    @State private var showsSlidingPanel = false
    
    let navigationSection: NavigationSection?
    @ObservedObject var navigationBarInfo: SwiftDataSource<TabBarInfo>
    
    let handleGoBack: () -> ()
    
    func body(content: Content) -> some View {
        let view = AnyView(
            VStack(spacing: 0) {
                ZStack {
                    AppColor.navigationBar.color
                        .edgesIgnoringSafeArea(.all)
                    
                    _NavigationBar(navigationBarInfo: navigationBarInfo.value,
                                   onButtonClick: onButtonClick)
                        .padding([.leading, .trailing], 10)
                }
                .frame(height: 44)
                
                AppColor.lightGrey.color.frame(height: 1)
                
                content
            }
        )
        
        guard let navigationSection = self.navigationSection else { return view }
        
        return AnyView(
            view
                .slidingNavigationPanelView(withNavigationSection: navigationSection,
                                            showsSlidingPanel: showsSlidingPanel,
                                            changeSlidingPanelState: changeSlidingPanelState)
        )
    }
    
    init(navigationSection: NavigationSection?,
         navigationBarInfo: DataSource<TabBarInfo>,
         handleGoBack: @escaping () -> ()) {
        self.navigationSection = navigationSection
        
        self.navigationBarInfo = SwiftDataSource(dataSource: navigationBarInfo)
        
        self.handleGoBack = handleGoBack
    }
    
    private func onButtonClick(withScopeIcon scopeIcon: ScopeIcon) {
        switch scopeIcon {
        case .back:
            handleGoBack()
        case .hamburger:
            changeSlidingPanelState(isHidden: false)
        default:
            return
        }
    }
    
    private func changeSlidingPanelState(isHidden: Bool) {
        withAnimation {
            self.showsSlidingPanel = !isHidden
        }
    }
}

private struct _NavigationBar: View {
    let navigationBarInfo: TabBarInfo?
    let onButtonClick: (ScopeIcon) -> ()
    
    var body: some View {
        guard let info = navigationBarInfo else { return AnyView(EmptyView()) }
        
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
                            SearchBar()
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
                    .padding([.leading, .trailing], 10)
                    
                default:
                    EmptyView()
                }
            }
        )
    }
    
    private func getScopeButton(for icon: ScopeIcon) -> some View {
        Button(action: { onButtonClick(icon) }) {
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
