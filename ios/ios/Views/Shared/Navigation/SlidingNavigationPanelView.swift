//
//  SlidingNavigationPanelView.swift
//  ios
//
//  Created by Dasha Gurinovich on 10.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SlidingNavigationPanelView: ViewModifier {
    let navigationSection: NavigationSection
    
    @ObservedObject var user: SwiftDataSource<DataUser>
    
    let showsSlidingPanel: Bool
    let changeSlidingPanelState: (Bool) -> ()
    
    init(navigationSection: NavigationSection,
         showsSlidingPanel: Bool,
         changeSlidingPanelState: @escaping (Bool) -> ()) {
        self.navigationSection = navigationSection
        
        self.user = SwiftDataSource(dataSource: navigationSection.user)
        
        self.showsSlidingPanel = showsSlidingPanel
        self.changeSlidingPanelState = changeSlidingPanelState
    }
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            ZStack {
                AppColor.primary.color
                
                content
                    .frame(maxWidth: geometry.size.width,
                           maxHeight: geometry.size.height,
                           alignment: .topLeading)
                    .zIndex(1)
            
                _SlidingPanelView(navigationSection: navigationSection,
                                  userName: user.value?.fullName(),
                                  userType: user.value?.type,
                                  geometry: geometry,
                                  isShown: showsSlidingPanel,
                                  changeSlidingPanelState: changeSlidingPanelState)
                    .zIndex(2)
            }
        }
        .gesture(getDragGesture())
    }
    
    private func getDragGesture() -> some Gesture {
        DragGesture(minimumDistance: 20, coordinateSpace: .local)
            .onEnded({ value in
                // Left swipe
                if value.translation.width < 0 {
                    self.changeSlidingPanelState(true)
                }
                
                // Right swipe
                if value.translation.width > 0 {
                    self.changeSlidingPanelState(false)
                }
            })
    }
}

private struct _SlidingPanelView: View {
    let navigationSection: NavigationSection
    let changeSlidingPanelState: (Bool) -> ()
    
    let geometry: GeometryProxy
    let isShown: Bool
    
    let userName: String?
    let userType: DataUserType?
    
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
         userName: String?,
         userType: DataUserType?,
         geometry: GeometryProxy,
         isShown: Bool,
         changeSlidingPanelState: @escaping (Bool) -> ()) {
        self.navigationSection = navigationSection
        
        self.userName = userName
        self.userType = userType
        
        self.geometry = geometry
        self.isShown = isShown
        
        self.changeSlidingPanelState = changeSlidingPanelState
    }
    
    private var blurView: some View {
        BlurEffectView()
            .testingIdentifier("blur_view")
            .transition(.identity)
            .onTapGesture { self.changeSlidingPanelState(true) }
    }
    
    private var userPanel: some View {
        ZStack(alignment: .bottomLeading) {
            Image("AccountInfoBackground")

            VStack(alignment: .leading, spacing: 6) {
                Image("DefaultUserPhoto")
                    .testingIdentifier("user_photo")

                Text(userName ?? "")
                    .medicoText(textWeight: .bold,
                                testingIdentifier: "user_name")

                LocalizedText(localizationKey: userType?.localizedName ?? "",
                              textWeight: .medium)
            }
            .padding()
        }
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
