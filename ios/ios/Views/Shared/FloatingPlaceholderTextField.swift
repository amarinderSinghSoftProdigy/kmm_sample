//
//  FloatingPlaceholderTextView.swift
//  ios
//
//  Created by Dasha Gurinovich on 25.11.20.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

struct FloatingPlaceholderTextField: View {
    
    let placeholderLocalizedStringKey: String
    let text: Binding<String>
    
    let height: CGFloat
    
    @State var showLightUp = false
    
    var body: some View {
        VStack(alignment: .center, spacing: 0) {
            ZStack(alignment: .leading) {
                RoundedRectangle(cornerRadius: 8)
                    .fill(appColor: .white)
                    .frame(height: height)
                
                let padding: CGFloat = 16
                
                let textFieldHeight = height - 12
                TextField("", text: text, onEditingChanged: { changed in
                    self.showLightUp = true
                }, onCommit: {
                    UIApplication.shared.endEditing()
                    
                    self.showLightUp = false
                })
                    .font(.custom("Barlow-Regular", size: 15))
                    .frame(height: textFieldHeight, alignment: .bottomLeading)
                    .padding([.leading, .trailing], padding)
                
                let fontSize: CGFloat = showLightUp ? 11 : 15
                let textOffset: CGFloat = showLightUp ? -10 : 0
                let textColor: AppColor = showLightUp ? .lightBlue : .placeholderGray

                Text(LocalizedStringKey(placeholderLocalizedStringKey))
                    .font(.custom("Barlow-Regular", size: fontSize))
                    .offset(y: textOffset)
                    .foregroundColor(appColor: textColor)
                    .padding([.leading, .trailing], padding)
            }
            
            Rectangle()
                .fill(appColor: .lightBlue)
                .frame(height: 1)
                .isHidden(!showLightUp)
        }
    }
    
    init(placeholderLocalizedStringKey: String, text: Binding<String>,
         height: CGFloat = 50) {
        self.placeholderLocalizedStringKey = placeholderLocalizedStringKey
        self.text = text
        
        self.height = height
    }
}

//struct FloatingPlaceholderTextField_Previews: PreviewProvider {
//    @State static var result: String = ""
//
//    static var previews: some View {
//        TextField(LocalizedStringKey(""), text: $result)
//        .modifier(FloatingPlaceholderTextField(placeholderLocalizedStringKey: "phone_number"))
//    }
//}
