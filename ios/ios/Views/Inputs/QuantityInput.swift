//
//  QuantityInput.swift
//  Medico
//
//  Created by Dasha Gurinovich on 7.09.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import Combine

struct QuantityInput: View {
    private var quantity: Binding<Double>
    private var freeQuantity: Binding<Double>
    
    private let maxQuantity: Double
    
    @State private var cursorPosition: Int?
    
    var body: some View {
        HStack {
            EditableInput(titleLocalizationKey: "QTY",
                          text: String(format: "%.1f", quantity.wrappedValue),
                          onTextChange: {
                            if let newValue = Double($0) {
                                quantity.wrappedValue = newValue
                            }
                          },
                          keyboardType: .decimalPad)
                .frame(width: 110)
                .onReceive(Just(quantity.wrappedValue)) {
                    quantity.wrappedValue = roundQuantity($0)
                    
                    if quantity.wrappedValue > maxQuantity {
                        quantity.wrappedValue = maxQuantity
                    }
                    
                    if freeQuantity.wrappedValue > quantity.wrappedValue {
                        freeQuantity.wrappedValue = quantity.wrappedValue
                    }
                }
            
            Spacer()
            
            let isFreeQuantityDisabled = quantity.wrappedValue == 0
            EditableInput(titleLocalizationKey: "FREE",
                          text: String(format: "%.1f", freeQuantity.wrappedValue),
                          onTextChange: {
                            if let newValue = Double($0) {
                                freeQuantity.wrappedValue = newValue
                            }
                          },
                          keyboardType: .decimalPad)
                .frame(width: 110)
                .disabled(isFreeQuantityDisabled)
                .opacity(isFreeQuantityDisabled ? 0.5 : 1)
                .onReceive(Just(freeQuantity.wrappedValue)) {
                    freeQuantity.wrappedValue = roundQuantity($0)
                    
                    if freeQuantity.wrappedValue > quantity.wrappedValue {
                        freeQuantity.wrappedValue = quantity.wrappedValue
                    }
                }
        }
    }
    
    init(quantity: Binding<Double>,
         freeQuantity: Binding<Double>,
         maxQuantity: Double) {
        self.quantity = quantity
        self.freeQuantity = freeQuantity
        
        self.maxQuantity = maxQuantity
    }
    
    private func roundQuantity(_ quantity: Double) -> Double {
        let floatingPoint = quantity.truncatingRemainder(dividingBy: 1)
        
        switch floatingPoint {
        case 0.0..<0.5:
            return quantity.rounded(.down)
            
        case 0.5..<1:
            return quantity.rounded(.down) + 0.5
            
        default:
            return quantity
        }
    }
}
