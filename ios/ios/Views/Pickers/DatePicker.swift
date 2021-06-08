//
//  DatePicker.swift
//  Medico
//
//  Created by Dasha Gurinovich on 27.05.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct DatePicker: View {
    private var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd / MM / yyyy"
        
        return formatter
    }
    
    let placeholderLocalizationKey: String
    
    @Binding var date: Date?
    
    var body: some View {
        Group {
            if self.date != nil {
                ZStack(alignment: .bottomLeading) {
                    let date = Binding(get: { self.date ?? Date() },
                                       set: { self.date = $0 })
                    
                    SwiftUI.DatePicker(selection: date,
                                       displayedComponents: .date) {
                        EmptyView()
                    }
                    .labelsHidden()
                    
                    VStack(alignment: .leading, spacing: 2) {
                        LocalizedText(localizationKey: placeholderLocalizationKey,
                                      fontSize: 11,
                                      color: .lightBlue)
                            
                        Text(dateFormatter.string(from: date.wrappedValue))
                            .medicoText(fontSize: 15,
                                        color: AppColor.black)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .userInteractionDisabled()
                }
                
                .scaledToFit()
            }
            else {
                HStack {
                    LocalizedText(localizationKey: placeholderLocalizationKey,
                                  fontSize: 15)

                    Spacer()

                    Image(systemName: "chevron.right")
                        .foregroundColor(appColor: .grey3)
                        .font(.system(size: 14, weight: .semibold))
                }
                .background(appColor: .white)
                .onTapGesture {
                    self.date = Date()
                }
            }
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 14)
        .frame(height: 48)
        .frame(maxWidth: .infinity, maxHeight: 48, alignment: .leading)
        .background(
            RoundedRectangle(cornerRadius: 2)
                .foregroundColor(appColor: .white)
        )
    }
}
