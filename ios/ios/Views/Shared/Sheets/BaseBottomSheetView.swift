//
//  BaseBottomSheetView.swift
//  Medico
//
//  Created by Dasha Gurinovich on 21.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct BaseBottomSheetView<Content: View>: View {
    private let minHeightRatio: CGFloat = 0.3
    
    let maxHeight: CGFloat
    let minHeight: CGFloat
    let content: Content
    
    @Binding var isOpened: Bool

    @GestureState private var translation: CGFloat = 0

    private var indicator: some View {
        AppColor.placeholderGrey.color
            .cornerRadius(2.5)
            .frame(width: 40, height: 3)
            .onTapGesture {
                self.isOpened = false
            }
    }

    init(isOpened: Binding<Bool>, maxHeight: CGFloat, @ViewBuilder content: () -> Content) {
        self.minHeight = maxHeight * minHeightRatio
        self.maxHeight = maxHeight
        
        self.content = content()
        
        self._isOpened = isOpened
    }

    var body: some View {
        Group {
            if isOpened {
                BlurEffectView()
                    .edgesIgnoringSafeArea(.all)
                    .onTapGesture {
                        self.isOpened = false
                    }
                    .transition(.opacity)
                
                GeometryReader { geometry in
                    VStack(spacing: 10) {
                        self.indicator
                            .padding(.top, 10)
                        
                        self.content
                    }
                    .frame(width: geometry.size.width, height: self.maxHeight, alignment: .top)
                    .background(appColor: .white)
                    .cornerRadius(13, corners: [.topLeft, .topRight])
                    .frame(height: geometry.size.height, alignment: .bottom)
                    .offset(y: max(self.translation, 0))
                    .gesture(
                        DragGesture().updating(self.$translation) { value, state, _ in
                            state = value.translation.height
                        }.onEnded { value in
                            let snapRatio: CGFloat = 0.35
                            let snapDistance = self.maxHeight * snapRatio
                            
                            guard abs(value.translation.height) > snapDistance else {
                                return
                            }
                            
                            self.isOpened = value.translation.height < 0
                        }
                    )
                }
                .transition(.move(edge: .bottom))
            }
        }
    }
}
