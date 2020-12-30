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
                            .padding(.horizontal, -8)
                            .padding(.vertical, -10)
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
                    
                self.getSlidingPanel(for: geometry)
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
    
    private var bottomOptionsView: some View {
        ForEach(navigationSection.footer, id: \.self) { option in
            Group {
                if let localizedStringKey = option.textLocalizationKey {
                    Button(action: { _ = option.select() }) {
                        HStack(spacing: 24) {
                            if let imageName = option.imageName {
                                Image(imageName)
                            }
                            
                            LocalizedText(localizedStringKey: localizedStringKey,
                                          textWeight: .semiBold,
                                          fontSize: 15,
                                          color: .grey1)
                        }
                    }
                    .testingIdentifier("\(localizedStringKey)_button")
                }
            }
        }
    }
    
    private func getSlidingPanel(for geometry: GeometryProxy) -> some View {
        return AnyView(
            VStack(spacing: -1) {
                if let user = self.user {
                    ZStack(alignment: .bottomLeading) {
                        Image("AccountInfoBackground")

                        VStack(alignment: .leading, spacing: 6) {
                            Image("DefaultUserPhoto")
                                .testingIdentifier("user_photo")

                            Text(user.fullName())
                                .modifier(MedicoText(textWeight: .bold))
                                .testingIdentifier("user_name")

                            LocalizedText(localizedStringKey: user.type.localizedName,
                                          textWeight: .medium)
                        }
                        .padding()
                    }
                }
                
                ZStack {
                    AppColor.primary.color
                        .testingIdentifier("sliding_panel")
                    
                    VStack(alignment: .leading, spacing: 20) {
                        Spacer()
                        
                        AppColor.grey1.color
                            .frame(height: 1)
                        
                        self.bottomOptionsView
                    }
                    .padding()
                    .padding(.bottom, 20)
                }
            }
            .frame(width: geometry.size.width * 0.81)
            .frame(maxWidth: 400, alignment: .leading)
            .shadow(radius: 9)
            .transition(.move(edge: .leading))
        )
    }
}
