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
        getCurrentView()
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
            scopeView = AnyView(SignUpAddressScreen(scope: scope))
            
        case let scope as SignUpScope.Details.DetailsTraderData:
            progressFill = 0.8
            scopeView = AnyView(SignUpTraderDetails(scope: scope))
            
        case let scope as SignUpScope.Details.DetailsAadhaar:
            progressFill = 0.8
            scopeView = AnyView(SignUpAadhaarCardDetailsScreen(scope: scope))
            
        case let scope as SignUpScope.LegalDocuments:
            progressFill = 1.0
            scopeView = AnyView(SignUpLegalDocumentsScreen(scope: scope))
            
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
    @State private var buttonPadding: CGFloat = 0
    
    let isEnabled: Bool
    let buttonTextKey: String
    
    let padding: CGFloat
    
    let action: () -> ()
    let skipButtonAction: (() -> ())?
    
    init(isEnabled: Bool,
         buttonTextKey: String = "next",
         padding: CGFloat = 16,
         skipButtonAction: (() -> ())? = nil,
         action: @escaping () -> ()) {
        self.isEnabled = isEnabled
        self.buttonTextKey = buttonTextKey
        
        self.padding = padding
        
        self.action = action
        self.skipButtonAction = skipButtonAction
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
        .padding(padding)
    }
    
    private func createButtonsContainer(withTopStackPadding topStackPadding: CGFloat) -> some View {
        return VStack(spacing: 16) {
            MedicoButton(localizedStringKey: buttonTextKey, isEnabled: isEnabled, action: action)
            
            if let skipButtonAction = self.skipButtonAction {
                LocalizedText(localizationKey: "skip_for_now",
                              color: .lightBlue)
                    .onTapGesture {
                        skipButtonAction()
                    }
            }
        }
        .background(GeometryReader { gp -> Color in
            let frame = gp.frame(in: .local)
            DispatchQueue.main.async {
                self.buttonPadding = topStackPadding - frame.size.height
            }
            return Color.clear
        })
        .padding(.bottom, buttonPadding)
    }
}
