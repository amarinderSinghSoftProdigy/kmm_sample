//
//  RegistrationScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 1.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpScreen: View {
    let scope: SignUpScope
    
    var body: some View {
        ZStack {
            AppColor.primary.color.edgesIgnoringSafeArea(.all)
        
            getCurrentView()
        }
        .backButton { scope.goBack() }
        .hideKeyboardOnTap()
    }
    
    private func getCurrentView() -> some View {
        let progressFill: CGFloat
        let scopeView: AnyView
        
        switch scope {
        
        case let scope as SignUpScope.SelectUserType:
            progressFill = 0.2
            scopeView = AnyView(SelectUserTypeScreen(scope: scope))
            
        case let scope as SignUpScope.PersonalData:
            progressFill = 0.4
            scopeView = AnyView(SignUpPersonalDataScreen(scope: scope))
            
        case let scope as SignUpScope.AddressData:
            progressFill = 0.6
            scopeView = AnyView(EmptyView())
            
        case let scope as SignUpScope.TraderData:
            progressFill = 0.8
            scopeView = AnyView(EmptyView())
            
        default:
            progressFill = 0
            scopeView = AnyView(EmptyView())
        }
        
        return AnyView(
            scopeView
                .modifier(ProgressViewModifier(progressFill: progressFill))
        )
    }
}

struct ProgressViewModifier: ViewModifier {
    let progressHeight: CGFloat = 4
    
    let progressFill: CGFloat
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            ZStack {
                content
                    .padding(.top, progressFill)
                
                VStack {
                    self.getProgressView(withGeometry: geometry)
                    
                    Spacer()
                }
            }
        }
    }
    
    private func getProgressView(withGeometry geometry: GeometryProxy) -> some View {
        ZStack(alignment: .leading) {
            AppColor.white.color
        
            Rectangle()
                .fill(appColor: .yellow)
                .animation(nil)
                .frame(width: geometry.size.width * CGFloat(progressFill))
                .animation(.linear)
        }
        .frame(height: progressHeight)
    }
}

struct SignUpButton: ViewModifier {
    @State private var padding: CGFloat = 0
    
    let showsSkipButton: Bool
    let isEnabled: Bool
    let action: () -> ()
    
    init(isEnabled: Bool, showsSkipButton: Bool = false, action: @escaping () -> ()) {
        self.isEnabled = isEnabled
        self.showsSkipButton = showsSkipButton
        
        self.action = action
    }
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            let topStackPadding = geometry.size.height * 0.18
            
            VStack(spacing: 32) {
                content
                    .frame(minWidth: 0,
                           maxWidth: geometry.size.width,
                           minHeight: 0,
                           maxHeight: geometry.size.height - topStackPadding,
                           alignment: .center)
                    
                self.createButtonsContainer(withTopStackPadding: topStackPadding)
            }
        }
        .padding()
    }
    
    private func createButtonsContainer(withTopStackPadding topStackPadding: CGFloat) -> some View {
        return VStack(spacing: 16) {
            MedicoButton(localizedStringKey: "next", isEnabled: isEnabled, action: action)
            
            if showsSkipButton {
                Text(LocalizedStringKey("skip_for_now"))
                    .modifier(MedicoText(color: .lightBlue))
            }
        }
        .background(GeometryReader { gp -> Color in
            let frame = gp.frame(in: .local)
            DispatchQueue.main.async {
                self.padding = topStackPadding - frame.size.height
            }
            return Color.clear
        })
        .padding(.bottom, padding)
    }
}
