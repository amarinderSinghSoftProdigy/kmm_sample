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
    let isLimitedAppAccess: Bool
    let user: DataUser
    let logOutAction: () -> ()
    
    @State private var showsSlidingPanel = false
    
    var slidingPanelButton: some View {
        Button(action: { self.changeSlidingPanelState(isHidden: false) }) {
            Image("Menu")
                .resizable()
                .aspectRatio(contentMode: .fit)
        }
    }
    
    private var blurView: some View {
        BlurEffectView()
            .transition(.identity)
            .onTapGesture { self.changeSlidingPanelState(isHidden: true) }
    }
    
    func body(content: Content) -> some View {
        let baseNavigationBarView = AnyView(
            GeometryReader { geometry in
                ZStack {
                    AppColor.primary.color
                    
                    content
                        .frame(width: geometry.size.width,
                                height: geometry.size.height,
                                alignment: .top)
                        .zIndex(1)
                
                    ZStack {
                        if showsSlidingPanel {
                            self.blurView
                            
                            self.getSlidingPanel(for: geometry)
                        }
                    }
                    .zIndex(2)
                }
            }
            .gesture(getDragGesture())
            .navigationBarTitle("", displayMode: .inline)
            .navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: slidingPanelButton)
        )
        
        if !isLimitedAppAccess {
            
        }
        
        return baseNavigationBarView
    }
    
    private func changeSlidingPanelState(isHidden: Bool) {
        withAnimation {
            self.showsSlidingPanel = !isHidden
        }
    }
    
    private func getSlidingPanel(for geometry: GeometryProxy) -> some View {
        return AnyView(
            VStack(spacing: -1) {
                ZStack(alignment: .bottomLeading) {
                    Image("AccountInfoBackground")
                    
                    VStack(alignment: .leading, spacing: 6) {
                        Image("DefaultUserPhoto")
                        
                        Text(user.fullName())
                            .modifier(MedicoText(textWeight: .bold))
                        
                        LocalizedText(localizedStringKey: user.type.localizedName,
                                      textWeight: .medium)
                    }
                    .padding()
                }
                
                ZStack {
                    AppColor.primary.color
                    
                    VStack(alignment: .leading, spacing: 20) {
                        Spacer()
                        
                        AppColor.grey.color
                            .frame(height: 1)
                        
                        Button(action: logOutAction) {
                            HStack(spacing: 24) {
                                Image("Exit")
                                
                                LocalizedText(localizedStringKey: "log_out",
                                              textWeight: .semiBold,
                                              fontSize: 15,
                                              color: .grey)
                            }
                        }
                    }
                    .padding()
                    .padding(.bottom, 20)
                }
            }
            .frame(width: geometry.size.width * 0.81)
            .frame(maxWidth: 400, alignment: .leading)
            .shadow(radius: 9)
            .navigationBarHidden(true)
            .transition(.move(edge: .leading))
        )
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
