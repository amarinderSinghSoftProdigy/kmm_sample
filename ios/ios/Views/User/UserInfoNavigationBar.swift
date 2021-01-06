//
//  UserInfoNavigationBar.swift
//  ios
//
//  Created by Dasha Gurinovich on 10.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct UserInfoNavigationBar: ViewModifier {
    let scope: NavAndSearchMainScope
    let navigationSection: NavigationSection
    
    @State private var showsSlidingPanel = false
    
    @ObservedObject var user: SwiftDataSource<DataUser>
    
    private var slidingPanelButton: some View {
        Button(action: { self.changeSlidingPanelState(isHidden: false) }) {
            Image("Menu")
                .aspectRatio(contentMode: .fit)
        }
    }
    
    private var navigationBar: some View {
        VStack(spacing: 0){
            ZStack {
                AppColor.navigationBar.color
                    .edgesIgnoringSafeArea(.all)
                
                let spacing: CGFloat = 19
                HStack(spacing: spacing) {
                    self.slidingPanelButton
                    
                    if user.value?.isVerified == true {
                        SearchBar()
                            .onTapGesture {
                                scope.goToSearch()
                            }
                    }
                    else {
                        Spacer()
                    }
                }
                .padding([.leading, .trailing], spacing)
            }
            .frame(height: 44)
            
            AppColor.lightGrey.color
                .frame(height: 1)
        }
    }
    
    init(scope: NavAndSearchMainScope,
         navigationSection: NavigationSection) {
        self.scope = scope
        self.navigationSection = navigationSection
        
        self.user = SwiftDataSource(dataSource: scope.user)
    }
    
    func body(content: Content) -> some View {
        let baseNavigationBarView = AnyView(
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color
                    
                    VStack(spacing: 0) {
                        navigationBar
                        
                        content
                    }
                    .frame(maxWidth: geometry.size.width,
                           maxHeight: geometry.size.height,
                           alignment: .topLeading)
                    .zIndex(1)
                
                    SlidingPanelView(navigationSection: navigationSection,
                                     user: user.value,
                                     geometry: geometry,
                                     isShown: showsSlidingPanel,
                                     changeSlidingPanelState: changeSlidingPanelState)
                        .zIndex(2)
                }
            }
            .gesture(getDragGesture())
            .navigationBarHidden(true)
        )
        
        return baseNavigationBarView
    }
    
    private func changeSlidingPanelState(isHidden: Bool) {
        withAnimation {
            self.showsSlidingPanel = !isHidden
        }
    }
    
    private func getDragGesture() -> some Gesture {
        DragGesture(minimumDistance: 20, coordinateSpace: .local)
            .onEnded({ value in
                // Left swipe
                if value.translation.width < 0 {
                    self.changeSlidingPanelState(isHidden: true)
                }
                
                // Right swipe
                if value.translation.width > 0 {
                    self.changeSlidingPanelState(isHidden: false)
                }
            })
    }
}

struct SlidingPanelView: View {
    let navigationSection: NavigationSection
    let changeSlidingPanelState: (Bool) -> ()
    
    let geometry: GeometryProxy
    let isShown: Bool
    
    let user: DataUser?
    
    var body: some View {
        ZStack {
            if isShown {
                self.blurView
                    
                VStack(spacing: -1) {
                    self.userPanel
                    
                    self.optionsPanel
                }
                .frame(width: geometry.size.width * 0.81)
                .frame(maxWidth: 400, alignment: .leading)
                .shadow(radius: 9)
                .transition(.move(edge: .leading))
            }
        }
    }
    
    init(navigationSection: NavigationSection,
         user: DataUser?,
         geometry: GeometryProxy,
         isShown: Bool,
         changeSlidingPanelState: @escaping (Bool) -> ()) {
        self.navigationSection = navigationSection
        self.changeSlidingPanelState = changeSlidingPanelState
        
        self.geometry = geometry
        self.isShown = isShown
        
        self.user = user
    }
    
    private var blurView: some View {
        BlurEffectView()
            .testingIdentifier("blur_view")
            .transition(.identity)
            .onTapGesture { self.changeSlidingPanelState(true) }
    }
    
    private var userPanel: some View {
        guard let user = self.user else { return AnyView(EmptyView()) }
        
        return AnyView(
            ZStack(alignment: .bottomLeading) {
                Image("AccountInfoBackground")

                VStack(alignment: .leading, spacing: 6) {
                    Image("DefaultUserPhoto")
                        .testingIdentifier("user_photo")

                    Text(user.fullName())
                        .medicoText(textWeight: .bold,
                                    testingIdentifier: "user_name")

                    LocalizedText(localizationKey: user.type.localizedName,
                                  textWeight: .medium)
                }
                .padding()
            }
        )
    }
    
    private var optionsPanel: some View {
        ZStack {
            AppColor.primary.color
                .testingIdentifier("sliding_panel")
            
            VStack(alignment: .leading, spacing: 20) {
                VStack(spacing: 20) {
                    ForEach(navigationSection.main, id: \.self) { option in
                        NavigationCell(navigationOption: option, style: .main)
                    }
                }
                
                Spacer()
                
                AppColor.grey1.color
                    .frame(height: 1)
                
                VStack(spacing: 20) {
                    ForEach(navigationSection.footer, id: \.self) { option in
                        NavigationCell(navigationOption: option, style: .bottom)
                    }
                }
            }
            .padding()
            .padding(.bottom, 20)
        }
    }
    
    private struct NavigationCell: View {
        let navigationOption: NavigationOption
        
        let style: Style
        
        var body: some View {
            guard let localizationKey = navigationOption.textLocalizationKey else {
                return AnyView(EmptyView())
            }
            
            return AnyView(
                Button(action: { _ = navigationOption.select() }) {
                    HStack(spacing: 24) {
                        if let imageName = navigationOption.imageName {
                            Image(imageName)
                        }
                        
                        LocalizedText(localizationKey: localizationKey,
                                      textWeight: style.textWeight,
                                      fontSize: 15,
                                      color: style.foregroundColor)
                        
                        if style.hasNavigationArrow {
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .foregroundColor(appColor: style.foregroundColor)
                        }
                    }
                }
                .testingIdentifier("\(localizationKey)_button")
            )
        }
        
        enum Style {
            case main
            case bottom
            
            var hasNavigationArrow: Bool {
                return self == .main
            }
            
            var textWeight: TextWeight {
                switch self {
                case .main:
                    return .medium
                case .bottom:
                    return .semiBold
                }
            }
            
            var foregroundColor: AppColor {
                switch self {
                case .main:
                    return .darkBlue
                case .bottom:
                    return .grey1
                }
            }
        }
    }
}

extension NavigationOption {
    var imageName: String? {
        switch self {
        case .LogOut():
            return "Exit"
            
        case .Settings():
            return "Settings"
            
        default:
            return nil
        }
    }
    
    var textLocalizationKey: String? {
        switch self {
        case .LogOut():
            return "log_out"
            
        case .Settings():
            return "settings"
            
        default:
            return nil
        }
    }
}
