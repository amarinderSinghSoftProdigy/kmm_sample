//
//  PopSheet.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.03.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

public extension View {
    /// Creates an `ActionSheet` on an iPhone or the equivalent `popover` on an iPad, in order to work around `.actionSheet` crashing on iPad (`FB7397761`).
    ///
    /// - Parameters:
    ///     - isPresented: A `Binding` to whether the action sheet should be shown.
    ///     - content: A closure returning the `PopSheet` to present.
    func popSheet(isPresented: Binding<Bool>, arrowEdge: Edge = .bottom, content: @escaping () -> PopSheet) -> some View {
        Group {
            if UIDevice.current.userInterfaceIdiom == .pad {
                popover(isPresented: isPresented,
                        attachmentAnchor: .point(.bottom),
                        arrowEdge: arrowEdge,
                        content: { content().popover(isPresented: isPresented) })
            } else {
                actionSheet(isPresented: isPresented,
                            content: { content().actionSheet() })
            }
        }
    }
}

/// A `Popover` on iPad and an `ActionSheet` on iPhone.
public struct PopSheet {
    let title: Text
    let message: Text?
    let buttons: [PopSheet.Button]
    
    let onDisappear: () -> ()

    /// Creates an action sheet with the provided buttons.
    public init(title: Text,
                message: Text? = nil,
                buttons: [PopSheet.Button] = [.cancel()],
                onDisappear: @escaping () -> ()) {
        self.title = title
        self.message = message
        self.buttons = buttons
        
        self.onDisappear = onDisappear
    }

    /// Creates an `ActionSheet` for use on an iPhone device
    func actionSheet() -> ActionSheet {
        ActionSheet(title: title, message: message, buttons: buttons.map({ popButton in
            // convert from PopSheet.Button to ActionSheet.Button (i.e., Alert.Button)
            switch popButton.kind {
            case .default: return .default(popButton.label, action: popButton.action)
            case .cancel: return .cancel(popButton.label, action: popButton.action)
            case .destructive: return .destructive(popButton.label, action: popButton.action)
            }
        }))
    }

    /// Creates a `.popover` for use on an iPad device
    func popover(isPresented: Binding<Bool>) -> some View {
        VStack(spacing: 0) {
            ForEach(Array(buttons.enumerated()), id: \.offset) { (offset, button) in
                Group {
                    SwiftUI.Button(action: {
                        // hide the popover whenever an action is performed
                        isPresented.wrappedValue = false
                        // another bug: if the action shows a sheet or popover, it will fail unless this one has already been dismissed
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1, execute: { button.action?() })
                    }) {
                        button.label.font(.title)
                            .padding()
                    }

                    Divider()
                }
            }
        }
        .onDisappear {
            self.onDisappear()
        }
    }

    /// A button representing an operation of an action sheet or popover presentation.
    ///
    /// Basically duplicates `ActionSheet.Button` (i.e., `Alert.Button`).
    public struct Button {
        let kind: Kind
        let label: Text
        let action: (() -> Void)?
        enum Kind { case `default`, cancel, destructive }

        /// Creates a `Button` with the default style.
        public static func `default`(_ label: Text, action: (() -> Void)? = {}) -> Self {
            Self(kind: .default, label: label, action: action)
        }

        /// Creates a `Button` that indicates cancellation of some operation.
        public static func cancel(_ label: Text, action: (() -> Void)? = {}) -> Self {
            Self(kind: .cancel, label: label, action: action)
        }

        /// Creates an `Alert.Button` that indicates cancellation of some operation.
        public static func cancel(_ action: (() -> Void)? = {}) -> Self {
            Self(kind: .cancel, label: Text("Cancel"), action: action)
        }

        /// Creates an `Alert.Button` with a style indicating destruction of some data.
        public static func destructive(_ label: Text, action: (() -> Void)? = {}) -> Self {
            Self(kind: .destructive, label: label, action: action)
        }
    }
}
