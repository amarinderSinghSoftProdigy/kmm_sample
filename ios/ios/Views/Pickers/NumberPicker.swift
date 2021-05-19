//
//  NumberPicker.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct NumberPicker: View {
    @State private var longPressTimer: Timer?
    
    let quantity: Int
    
    let onQuantityIncrease: () -> ()
    let onQuantityDecrease: () -> ()
    
    let longPressEnabled: Bool
    
    var body: some View {
        HStack(spacing: 15) {
            self.getActionButton(withImageName: "minus",
                                 canBeDisabled: true,
                                 for: self.onQuantityDecrease)
                .disabled(quantity <= 0)
            
            Text(String(quantity))
                .medicoText(textWeight: .bold,
                            fontSize: 22)
                .frame(width: 40)
            
            self.getActionButton(withImageName: "plus",
                                 for: self.onQuantityIncrease)
        }
    }
    
    init(quantity: Int,
         onQuantityIncrease: @escaping () -> (),
         onQuantityDecrease: @escaping () -> (),
         longPressEnabled: Bool = false
    ) {
        self.quantity = quantity
        
        self.onQuantityIncrease = onQuantityIncrease
        self.onQuantityDecrease = onQuantityDecrease
        
        self.longPressEnabled = longPressEnabled
    }
    
    private func getActionButton(withImageName imageName: String,
                                 canBeDisabled: Bool = false,
                                 for action: @escaping () -> ()) -> some View {
        Button(action: { }) {
            Image(systemName: imageName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(appColor: !canBeDisabled || quantity > 0 ? .lightBlue : .grey3)
                .font(Font.title.weight(.bold))
                .frame(width: 14, height: 14)
                .background(appColor: .white)
                .onTapGesture {
                    action()
                }
                .onLongPressGesture(minimumDuration: 30, pressing: { inProgress in
                    if inProgress && longPressEnabled {
                        self.longPressTimer = getLongPressTimer(handleTimeElapse: action)
                    }
                    else {
                        self.longPressTimer?.invalidate()
                    }
                }) {
                    self.longPressTimer?.invalidate()
                }
        }
    }
    
    private func getLongPressTimer(handleTimeElapse: @escaping () -> ()) -> Timer {
        Timer.scheduledTimer(withTimeInterval: 0.2, repeats: true) { _ in
            handleTimeElapse()
        }
    }
}
