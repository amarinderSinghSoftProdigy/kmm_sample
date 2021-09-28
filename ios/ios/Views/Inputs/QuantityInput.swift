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
    
    @State private var quantityText: String
    @State private var freeQuantityText: String
    
    @State private var quantityUpdated: Bool = false
    
    private let maxQuantity: Double
    
    @State private var cursorPosition: Int?
    private var quantitiesCorrect: Binding<Bool>
    
    var body: some View {
        VStack(alignment: .trailing, spacing: 5) {
            HStack {
                let handleQuantityChange: (String) -> Void = { value in
                    self.handleQuantityChange(newValue: value,
                                              quantity: quantity,
                                              quantityText: &quantityText)
                }
                
                let handleFreeQuantityChange: (String) -> Void = { value in
                    self.handleQuantityChange(newValue: value,
                                              quantity: freeQuantity,
                                              quantityText: &freeQuantityText)
                }
                
                EditableInput(titleLocalizationKey: "QTY",
                              text: quantityText,
                              onTextChange: handleQuantityChange,
                              keyboardType: .decimalPad,
                              constTrailingCursor: true)
                    .frame(width: 110)
                    .onReceive(Just(quantity.wrappedValue)) {
                        guard quantityUpdated else { return }
                        
                        quantity.wrappedValue = roundQuantity($0)

                        if quantity.wrappedValue > maxQuantity {
                            quantity.wrappedValue = maxQuantity
                        }

                        if freeQuantity.wrappedValue > quantity.wrappedValue {
                            freeQuantity.wrappedValue = quantity.wrappedValue
                            freeQuantityText = freeQuantity.wrappedValue.clean
                        }
                        
                        quantityText = quantity.wrappedValue.clean

                        validateQuantity()
                    }
                
                Spacer()
                
                let isFreeQuantityDisabled = quantity.wrappedValue == 0
                EditableInput(titleLocalizationKey: "FREE",
                              text: freeQuantityText,
                              onTextChange: handleFreeQuantityChange,
                              keyboardType: .decimalPad,
                              constTrailingCursor: true)
                    .frame(width: 110)
                    .disabled(isFreeQuantityDisabled)
                    .opacity(isFreeQuantityDisabled ? 0.5 : 1)
                    .onReceive(Just(freeQuantity.wrappedValue)) {
                        guard quantityUpdated else { return }
                        
                        freeQuantity.wrappedValue = roundQuantity($0)
                        
                        if freeQuantity.wrappedValue > quantity.wrappedValue {
                            freeQuantity.wrappedValue = quantity.wrappedValue
                        }
                        
                        freeQuantityText = freeQuantity.wrappedValue.clean
                        
                        validateQuantity()
                    }
            }
            
            if !quantitiesCorrect.wrappedValue {
                LocalizedText(localizationKey: "qty_error",
                              textWeight: .medium,
                              fontSize: 12,
                              color: .red)
            }
        }
    }
    
    init(quantity: Binding<Double>,
         freeQuantity: Binding<Double>,
         maxQuantity: Double,
         quantitiesCorrect: Binding<Bool>) {
        self.quantity = quantity
        self.freeQuantity = freeQuantity
        
        self._quantityText = State(initialValue: quantity.wrappedValue.clean)
        self._freeQuantityText = State(initialValue: freeQuantity.wrappedValue.clean)
        
        self.maxQuantity = maxQuantity
        self.quantitiesCorrect = quantitiesCorrect
    }
    
    private func handleQuantityChange(newValue: String,
                                      quantity: Binding<Double>,
                                      quantityText: inout String) {
        quantityUpdated = false
        
        guard newValue.filter({ $0 == "." || $0 == "," }).count <= 1 else { return }
        
        quantityText = newValue
        
        if let number = handleInputString(newValue) {
            quantityUpdated = true
            quantity.wrappedValue = number
        }
    }
    
    private func handleInputString(_ input: String) -> Double? {
        let replaceDecimalPointString = input.replacingOccurrences(of: ",", with: ".")
        
        guard replaceDecimalPointString.last != ".",
            let number = Double(replaceDecimalPointString) else { return nil }
        
        return number
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
    
    private func validateQuantity() {
        quantitiesCorrect.wrappedValue =
            (quantity.wrappedValue + freeQuantity.wrappedValue).truncatingRemainder(dividingBy: 1) == 0
    }
}
