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
    let closeSlidingPanel: (Bool) -> ()
    
    init(navigationSection: NavigationSection,
         showsSlidingPanel: Bool,
         closeSlidingPanel: @escaping (Bool) -> ()) {
        self.navigationSection = navigationSection
        
        self.user = SwiftDataSource(dataSource: navigationSection.user)
        
        self.showsSlidingPanel = showsSlidingPanel
        self.closeSlidingPanel = closeSlidingPanel
    }
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            ZStack {
                AppColor.primary.color
                    .hideKeyboardOnTap()
                
                content
                    .frame(maxWidth: geometry.size.width,
                           maxHeight: geometry.size.height,
                           alignment: .topLeading)
                    .zIndex(1)
                
                _SlidingPanelView(navigationSection: navigationSection,
                                  user: user.value,
                                  geometry: geometry,
                                  isShown: showsSlidingPanel,
                                  closeSlidingPanel: closeSlidingPanel)
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
                    self.closeSlidingPanel(true)
                }
                
                // Right swipe
                if value.translation.width > 0 {
                    self.closeSlidingPanel(false)
                }
            })
    }
}

private struct _SlidingPanelView: View {
    let navigationSection: NavigationSection
    let closeSlidingPanel: (Bool) -> ()
    
    let geometry: GeometryProxy
    let isShown: Bool
    
    let user: DataUser?
    
    var body: some View {
        ZStack(alignment: .leading) {
            if isShown {
                self.blurView
                    
                let width = min(geometry.size.width * 0.81, 300)
                
                VStack(spacing: -1) {
                    self.userPanel
                    
                    self.optionsPanel
                }
                .frame(width: width, alignment: .leading)
                .shadow(radius: 9)
                .transition(.move(edge: .leading))
                .onAppear { self.hideKeyboard() }
            }
        }
    }
    
    init(navigationSection: NavigationSection,
         user: DataUser?,
         geometry: GeometryProxy,
         isShown: Bool,
         closeSlidingPanel: @escaping (Bool) -> ()) {
        self.navigationSection = navigationSection
        
        self.user = user
        
        self.geometry = geometry
        self.isShown = isShown
        
        self.closeSlidingPanel = closeSlidingPanel
    }
    
    private var blurView: some View {
        BlurEffectView()
            .testingIdentifier("blur_view")
            .transition(.identity)
            .edgesIgnoringSafeArea(.all)
            .onTapGesture { self.closeSlidingPanel(true) }
    }
    
    private var userPanel: some View {
        ZStack(alignment: .bottomLeading) {
            Image("AccountInfoBackground")
                .resizable()
                .edgesIgnoringSafeArea(.all)

            VStack(alignment: .leading, spacing: 6) {
                Image("DefaultUserPhoto")
                    .testingIdentifier("user_photo")

                Text(user?.fullName() ?? "")
                    .medicoText(textWeight: .bold,
                                testingIdentifier: "user_name")

                LocalizedText(localizationKey: user?.type.localizedName ?? "",
                              textWeight: .medium)
                
                Text(user?.subscription?.validUntil ?? "")
                    .medicoText(textWeight: .mediumItalic,
                                fontSize: 12)
            }
            .padding()
        }
        .frame(height: 175)
    }
    
    private var optionsPanel: some View {
        ZStack(alignment: .bottomTrailing) {
            AppColor.primary.color
                .testingIdentifier("sliding_panel")
                .edgesIgnoringSafeArea(.all)
            
            Group {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 20) {
                        ForEach(navigationSection.main, id: \.self) { option in
                            NavigationCell(navigationOption: option, style: .navigation) {
                                self.closeSlidingPanel(true)
                            }
                        }
                    }
                    .scrollView()
                    
                    Spacer()
                    
                    AppColor.grey1.color
                        .frame(height: 1)
                    
                    VStack(spacing: 20) {
                        ForEach(navigationSection.footer, id: \.self) { option in
                            NavigationCell(navigationOption: option, style: .plain)
                        }
                    }
                }
                
                if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String {
                    LocalizedText(localizedStringKey: LocalizedStringKey("version \(version)"),
                                  textWeight: .semiBold,
                                  fontSize: 15,
                                  color: .grey1)
                }
            }
            .padding()
            .padding(.bottom, 20)
            
        }
    }
    
    private struct NavigationCell: View {
        let navigationOption: NavigationOption
        
        let style: TableViewCell.Style
        
        let onTapActionCallback: (() -> ())?
        
        var body: some View {
            TableViewCell(textLocalizationKey: navigationOption.textLocalizationKey,
                          imageName: navigationOption.imageName,
                          style: style,
                          extraChipTextLocalizationKey: navigationOption == .NewOrders() ? "new" : nil,
                          onTapAction: {
                            _ = navigationOption.select()
                            
                            onTapActionCallback?()
                          })
        }
        
        init(navigationOption: NavigationOption,
             style: TableViewCell.Style,
             onTapActionCallback: (() -> ())? = nil) {
            self.navigationOption = navigationOption
            
            self.style = style
            
            self.onTapActionCallback = onTapActionCallback
        }
    }
}

extension NavigationOption {
    var imageName: String? {
        switch self {
        case .Dashboard():
            return "Dashboard"
            
        case .LogOut():
            return "Exit"
            
        case .Settings():
            return "Settings"
            
        case .Stockists():
            return "Stockist"
            
        case .Retailers():
            return "Retailer"
            
        case .Hospitals():
            return "Hospital"
            
        case .SeasonBoys():
            return "SeasonBoy"
            
        case .Stores():
            return "Store"
            
        case .Help():
            return "Help"
            
        case .NewOrders():
            return "NewOrders"
            
        case .Orders():
            return "Orders"
            
        case .Invoices():
            return "Invoice"
            
        case .OrdersHistory():
            return "OrdersHistory"
            
        default:
            return nil
        }
    }
    
    var textLocalizationKey: String? {
        switch self {
        case .Dashboard():
            return "dashboard"
            
        case .LogOut():
            return "log_out"
            
        case .Settings():
            return "settings"
            
        case .Stockists():
            return "stockists"
            
        case .Retailers():
            return "retailers"
            
        case .Hospitals():
            return "hospitals"
            
        case .SeasonBoys():
            return "seasonBoys"
            
        case .Stores():
            return "stores"
            
        case .Help():
            return "help"
            
        case .NewOrders():
            return "purchase_orders"
            
        case .Orders():
            return "my_orders"
            
        case .Invoices():
            return "invoices"
            
        case .OrdersHistory():
            return "orders_history"
            
        default:
            return nil
        }
    }
}
