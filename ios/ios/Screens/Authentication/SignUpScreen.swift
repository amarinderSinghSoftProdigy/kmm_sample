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
        Background {
            ZStack {
                AppColor.primary.color.edgesIgnoringSafeArea(.all)
            
                getCurrentView()
            }
            .backButton { scope.goBack() }
        }
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
            scopeView = AnyView(EmptyView())
            
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
    let progressFill: CGFloat
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            VStack {
                ZStack(alignment: .leading) {
                    AppColor.white.color
                
                    Rectangle()
                        .fill(appColor: .yellow)
                        .animation(nil)
                        .frame(width: geometry.size.width * CGFloat(progressFill))
                        .animation(.linear)
                }
                .frame(height: 4)
                
                content
                    .frame(minWidth: 0,
                           maxWidth: .infinity,
                           minHeight: 0,
                           maxHeight: .infinity,
                           alignment: .topLeading)
            }
            .padding(geometry.safeAreaInsets)
            .frame(width: geometry.size.width, height: geometry.size.height - geometry.safeAreaInsets.top)
        }
    }
}

struct SignUpButton: ViewModifier {
    @State private var padding: CGFloat = 0
    
    let showsSkipButton: Bool
    let action: () -> ()
    
    init(showsSkipButton: Bool = false, action: @escaping () -> ()) {
        self.showsSkipButton = showsSkipButton
        
        self.action = action
    }
    
    func body(content: Content) -> some View {
        GeometryReader { geometry in
            let topStackPadding = geometry.size.height * 0.18
            
            VStack {
                content
                    .frame(minWidth: 0,
                           maxWidth: .infinity,
                           minHeight: 0,
                           maxHeight: .infinity,
                           alignment: .topLeading)
                    
                self.createButtonsContainer(withTopStackPadding: topStackPadding)
            }
        }
        .padding()
    }
    
    private func createButtonsContainer(withTopStackPadding topStackPadding: CGFloat) -> some View {
        return VStack(spacing: 16) {
            MedicoButton(localizedStringKey: "next", action: action)
            
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
