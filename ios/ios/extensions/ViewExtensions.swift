//
//  ViewExtensions.swift
//  ios
//
//  Created by Arnis on 18.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

extension View {
    @ViewBuilder func isHidden(_ hidden: Bool, remove: Bool = false) -> some View {
        if hidden {
            if !remove {
                self.hidden()
            }
        } else {
            self
        }
    }
    
    func alert(_ isPresented: Binding<Bool>,
               withTitleKey titleKey: String,
               withMessageKey messageKey: String,
               withButtonTextKey buttonTextKey: String) -> some View {
        return self.alert(isPresented: isPresented) {
            Alert(title: Text(LocalizedStringKey(titleKey)),
                  message: Text(LocalizedStringKey(messageKey)),
                  dismissButton: Alert.Button.default(Text(LocalizedStringKey(buttonTextKey))))
        }
    }
    
    func backButton(action: @escaping () -> ()) -> some View {
        let backButton = AnyView(
            Button(action: action) {
                Image("Back")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
            }
            .frame(width: 12, height: 21)
        )
        
        return self.navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: backButton)
    }
}
