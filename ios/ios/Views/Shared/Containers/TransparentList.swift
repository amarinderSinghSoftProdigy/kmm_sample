//
//  TransparentList.swift
//  Medico
//
//  Created by Dasha Gurinovich on 21.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct TransparentList<Content: View, T: Hashable>: View {
    let data: [T]
    let elementsSpacing: CGFloat
    
    let getCellView: (Int, T) -> Content
    
    var body: some View {
        List {
            ForEach(Array(data.enumerated()), id: \.element) { index, element in
                self.getCellView(index, element)
                    .padding(.bottom, elementsSpacing)
                    .listRowInsets(.init())
                    .listRowBackground(Color.clear)
                    .background(appColor: .primary)
            }
        }
        .onAppear {
            UITableView.appearance().showsVerticalScrollIndicator = false
            
            UITableView.appearance().backgroundColor = UIColor.clear
            UITableViewCell.appearance().backgroundColor = UIColor.clear
        }
    }
    
    init(data: [T],
         elementsSpacing: CGFloat = 16,
         getCellView: @escaping (Int, T) -> Content) {
        self.data = data
        self.elementsSpacing = elementsSpacing
        
        self.getCellView = getCellView
    }
}
