//
//  KeyboardResponder.swift
//  ios
//
//  Created by Dasha Gurinovich on 2.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import Foundation
import SwiftUI
import Combine

final class KeyboardResponder: ObservableObject {
    private let extraPadding: CGFloat = 20
    
    //2. Keeping track off the keyboard's current height
    @Published private(set) var currentBottomPadding: CGFloat = 0
    
    private static var padding: CGFloat = .zero
    
    //3. We use the NotificationCenter to listen to system notifications
    private var _center: NotificationCenter
    
    init(center: NotificationCenter = .default) {
        _center = center
        
        _center.addObserver(self,
                            selector: #selector(keyBoardWillShow(notification:)),
                            name: UIResponder.keyboardWillShowNotification,
                            object: nil)
        
        _center.addObserver(self,
                            selector: #selector(keyBoardWillHide(notification:)),
                            name: UIResponder.keyboardWillHideNotification,
                            object: nil)
        
        if UIResponder.currentFirstResponder != nil {
            currentBottomPadding = KeyboardResponder.padding
        }
    }

    deinit {
        _center.removeObserver(self)
    }
    
    //5.1. Update the currentHeight variable when the keyboards gets toggled
    @objc func keyBoardWillShow(notification: Notification) {
        guard let keyboardSize = (notification.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue)?.cgRectValue else { return }
        
        calculatePaddingValue(for: UIResponder.currentFirstResponder, withKeyboardSize: keyboardSize)
    }

    //5.2 Update the currentHeight variable when the keyboards collapses
    @objc func keyBoardWillHide(notification: Notification) {
        currentBottomPadding = 0
        KeyboardResponder.padding = .zero
    }
    
    private func calculatePaddingValue(for responder: UIResponder?, withKeyboardSize keyboardSize: CGRect) {
        let keyboardTop = UIScreen.main.bounds.height - keyboardSize.height
        let focusedTextInputBottom = responder?.globalFrame?.maxY ?? 0
        
        let padding = max(0, focusedTextInputBottom - keyboardTop + extraPadding)
        
        self.currentBottomPadding = padding
        KeyboardResponder.padding = padding
    }
}

extension UIResponder {
    static var currentFirstResponder: UIResponder? {
        _currentFirstResponder = nil
        UIApplication.shared.sendAction(#selector(UIResponder.findFirstResponder(_:)), to: nil, from: nil, for: nil)
        return _currentFirstResponder
    }

    private static weak var _currentFirstResponder: UIResponder?

    @objc private func findFirstResponder(_ sender: Any) {
        UIResponder._currentFirstResponder = self
    }

    var globalFrame: CGRect? {
        guard let view = self as? UIView else { return nil }
        return view.superview?.convert(view.frame, to: nil)
    }
}

struct KeyboardResponderModifier: ViewModifier {
    @ObservedObject private var keyboard = KeyboardResponder()
    
    func body(content: Content) -> some View {
        let modifiedView = getModifiededView(content: content)
        
        if #available(iOS 14.0, *) {
            return AnyView(modifiedView
                .ignoresSafeArea(.keyboard, edges: .bottom))
        } else {
            return AnyView(modifiedView)
        }
    }
    
    private func getModifiededView(content: Content) -> some View {
        return GeometryReader { _ in
            content
        }
        .offset(y: -keyboard.currentBottomPadding)
        .animation(.easeOut(duration: 0.16))
    }
}

extension View {
    func keyboardResponder() -> some View {
        self.modifier(KeyboardResponderModifier())
    }
}
