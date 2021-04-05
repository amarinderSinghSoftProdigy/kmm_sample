//
//  NumberPicker.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct NumberPicker: View {
    let quantity: Int
    
    let onQuantityIncrease: () -> ()
    let onQuantityDecrease: () -> ()
    
    var body: some View {
        HStack(spacing: 20) {
            Button(action: { onQuantityDecrease() }) {
                Group {
                    Image(systemName: "minus")
                        .resizable()
                        .foregroundColor(appColor: quantity > 0 ? .lightBlue : .grey3)
                        .font(Font.title.weight(.semibold))
                        .frame(width: 14, height: 2)
                }
                .frame(width: 14, height: 14)
            }
            .disabled(quantity <= 0)
            
            Text(String(quantity))
                .medicoText(textWeight: .bold,
                            fontSize: 22)
            
            Button(action: { onQuantityIncrease() }) {
                Image(systemName: "plus")
                    .resizable()
                    .foregroundColor(appColor: .lightBlue)
                    .font(Font.title.weight(.semibold))
                    .frame(width: 14, height: 14)
            }
        }
    }
}
