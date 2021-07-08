//
//  NumberPicker.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct NumberPicker: View {
    @State private var longPressTimer: Timer?
    
    let quantity: Int
    let maxQuantity: Int
    
    let onQuantityIncrease: (_ tapMode: DataTapMode) -> ()
    let onQuantityDecrease: (_ tapMode: DataTapMode) -> ()
    
    let longPressEnabled: Bool
    
    var body: some View {
        HStack(spacing: 15) {
            self.getActionButton(withImageName: "minus",
                                 disabled: quantity <= 0,
                                 for: self.onQuantityDecrease)
            
            Text(String(quantity))
                .medicoText(textWeight: .bold,
                            fontSize: 22)
                .frame(width: 40)
            
            self.getActionButton(withImageName: "plus",
                                 disabled: quantity > maxQuantity,
                                 for: self.onQuantityIncrease)
        }
    }
    
    init(quantity: Int,
         maxQuantity: Int = .max,
         onQuantityIncrease: @escaping (_ tapMode: DataTapMode) -> (),
         onQuantityDecrease: @escaping (_ tapMode: DataTapMode) -> (),
         longPressEnabled: Bool = false
    ) {
        self.quantity = quantity
        self.maxQuantity = maxQuantity
        
        self.onQuantityIncrease = onQuantityIncrease
        self.onQuantityDecrease = onQuantityDecrease
        
        self.longPressEnabled = longPressEnabled
    }
    
    private func getActionButton(withImageName imageName: String,
                                 disabled: Bool = false,
                                 for action: @escaping (_ tapMode: DataTapMode) -> ()) -> some View {
        Button(action: { }) {
            Image(systemName: imageName)
                .resizable()
                .aspectRatio(contentMode: .fit)
                .foregroundColor(appColor: !disabled ? .lightBlue : .grey3)
                .font(Font.title.weight(.bold))
                .frame(width: 14, height: 14)
                .background(appColor: .white)
                .onTapGesture {
                    action(.click)
                }
                .onLongPressGesture(minimumDuration: 30, pressing: { inProgress in
                    if inProgress && longPressEnabled {
                        action(.longPress)
//                        self.longPressTimer = getLongPressTimer(handleTimeElapse: { action(false) })
                    }
                    else {
                        action(.release_)
//                        self.longPressTimer?.invalidate()
                    }
                }) {
                    action(.release_)
//                    self.longPressTimer?.invalidate()
                }
        }
        .disabled(disabled)
    }
    
    private func getLongPressTimer(handleTimeElapse: @escaping () -> ()) -> Timer {
        Timer.scheduledTimer(withTimeInterval: 0.2, repeats: true) { _ in
            handleTimeElapse()
        }
    }
}
