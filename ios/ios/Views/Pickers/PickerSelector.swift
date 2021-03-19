//
//  PickerSelector.swift
//  ios
//
//  Created by Dasha Gurinovich on 4.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct PickerSelector: View {
    private let padding: CGFloat = 16
    
    let placeholder: String
    let chosenElement: String?
    let data: [String]
    
    let chosenOptionTextWeight: TextWeight
    let height: CGFloat
    let optionsHeight: CGFloat
    
    let onChange: (String) -> Void
    
    @State private var expanded: Bool = false
    
    var body: some View {
        ZStack {
            AppColor.white.color
                .cornerRadius(8)
            
            VStack(alignment: .leading, spacing: 0) {
                Button(action: {
                    self.expanded.toggle()
                }) {
                    HStack {
                        let text = chosenElement?.isEmpty == false ? chosenElement! : placeholder
                        
                        LocalizedText(localizationKey: text,
                                      textWeight: chosenOptionTextWeight,
                                      fontSize: 15,
                                      color: .black,
                                      multilineTextAlignment: .leading)
                            .frame(maxWidth: .infinity,
                                   alignment: .leading)
                        
                        Spacer()
                        
                        Image(systemName: "chevron.right")
                            .foregroundColor(appColor: .lightGrey)
                            .rotationEffect(.degrees(expanded ? 90 : 0))
                            .animation(.linear(duration: 0.2))
                    }
                    .padding([.leading, .trailing], padding)
                }
                .frame(maxWidth: .infinity)
                .frame(height: height)
                .background(RoundedRectangle(cornerRadius: 8)
                                .fill(appColor: .white))
                
                if self.expanded {
                    getOptionsViews()
                        .transition(AnyTransition.opacity
                                        .combined(with: AnyTransition.offset(y: -height)))
                }
            }
            .animation(.linear(duration: 0.2))
        }
    }
    
    init(placeholder: String,
         chosenElement: String?,
         data: [String],
         height: CGFloat = 50,
         optionsHeight: CGFloat? = nil,
         chosenOptionTextWeight: TextWeight = .regular,
         onChange: @escaping (String) -> Void) {
        self.placeholder = placeholder
        self.chosenElement = chosenElement
        self.data = data
        
        self.height = height
        self.optionsHeight = optionsHeight ?? height
        self.chosenOptionTextWeight = chosenOptionTextWeight
        
        self.onChange = onChange
    }
    
    private func getOptionsViews() -> some View {
        return VStack(alignment: .leading, spacing: 0) {
            ForEach(data, id: \.self) { text in
                Button(action: {
                        self.expanded = false
                        updateChosenValue(with: text)
                }) {
                    VStack(alignment: .leading, spacing: 0) {
                        AppColor.lightGrey.color
                            .frame(height: 1)
                        
                        Text(text)
                            .medicoText(fontSize: 15)
                            .padding([.top, .bottom], 8)
                            .frame(height: optionsHeight)
                            .padding([.leading, .trailing], padding)
                    }
                }
                .frame(maxWidth: .infinity)
            }
        }
    }
    
    private func updateChosenValue(with newValue: String) {
        if chosenElement == newValue { return }
        
        onChange(newValue)
    }
}
