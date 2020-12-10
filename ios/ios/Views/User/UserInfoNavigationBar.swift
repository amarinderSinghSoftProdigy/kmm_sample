//
//  UserInfoNavigationBar.swift
//  ios
//
//  Created by Dasha Gurinovich on 10.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct UserInfoNavigationBar: ViewModifier {
    let isLimitedAppAccess: Bool
    let logOutAction: () -> ()
    
    @State private var showsSlidingPanel = false
    
    var slidingPanelButton: some View {
        Button(action: { self.showsSlidingPanel = true }) {
            Image("Menu")
                .resizable()
                .aspectRatio(contentMode: .fit)
        }
    }
    
    private var slidingPanel: some View {
        ZStack {
            BlurEffectView()
                .onTapGesture { self.showsSlidingPanel = false }
            
            GeometryReader { geometry in
                VStack(spacing: -1) {
                    ZStack(alignment: .bottomLeading) {
                        Image("AccountInfoBackground")
                        
                        VStack(alignment: .leading, spacing: 6) {
                            Image("DefaultUserPhoto")
                            
                            Text("John Smith")
                                .modifier(MedicoText(textWeight: .bold))
                            
                            Text("Stockist")
                                .modifier(MedicoText(textWeight: .medium))
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
                                    
                                    Text(LocalizedStringKey("log_out"))
                                        .modifier(MedicoText(textWeight: .semiBold, fontSize: 15, color: .grey))
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
            }
        }
        .navigationBarHidden(true)
    }
    
    func body(content: Content) -> some View {
        let baseNavigationBarView = AnyView(
            ZStack {
                content
                
                if showsSlidingPanel {
                    slidingPanel
                }
            }
            .navigationBarTitle("", displayMode: .inline)
            .navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: slidingPanelButton)
        )
        
        if !isLimitedAppAccess {
            
        }
        
        return baseNavigationBarView
    }
}
