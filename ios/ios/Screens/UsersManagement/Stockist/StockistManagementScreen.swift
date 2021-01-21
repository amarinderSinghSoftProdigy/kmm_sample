//
//  StockistManagementScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct StockistManagementScreen: View {
    @State private var stockistText: NSString = ""
    
    @State private var selectedOption = 0
    let options = ["your_stockists", "all_stockists"]

    var body: some View {
        let stockistImage = Image("Stockist").resizable()

        ZStack(alignment: .topLeading) {
            AppColor.primary.color
                .hideKeyboardOnTap()
            
            VStack(spacing: 16) {
                SearchBar(placeholderLocalizationKey: "stockists",
                          searchText: stockistText,
                          leadingButton: SearchBar.SearchBarButton(emptyTextButton: .custom(AnyView(stockistImage)),
                                                                   enteredTextButton: .smallMagnifyingGlass),
                          trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                    enteredTextButton: .clear),
                          onTextChange: { newValue in stockistText = newValue as NSString })

                self.stockistsOptionsPicker
            }
            .keyboardResponder()
            .padding(.horizontal, 16)
            .padding(.vertical, 32)
        }
        .screenLogger(withScreenName: "StockistManagement",
                      withScreenClass: StockistManagementScreen.self)
    }
    
    private var stockistsOptionsPicker: some View {
        Picker(selection: $selectedOption, label: Text("")) {
            ForEach(0 ..< options.count) { index in
                Text(LocalizedStringKey(options[index]))
            }
        }
        .pickerStyle(SegmentedPickerStyle())
        .onAppear {
            let semtentedControlAppearance = UISegmentedControl.appearance()

            semtentedControlAppearance.selectedSegmentTintColor = .white

            semtentedControlAppearance.tintColor = UIColor(named: "NavigationBar")
            semtentedControlAppearance.backgroundColor = UIColor(named: "NavigationBar")

            let textColor = UIColor(named: "DarkBlue") ?? .darkGray
            let selectedStateFont = UIFont(name: "Barlow-SemiBold", size: 14) ?? .boldSystemFont(ofSize: 14)
            let normalStateFont = UIFont(name: "Barlow-Medium", size: 14) ?? .systemFont(ofSize: 14)

            semtentedControlAppearance.setTitleTextAttributes([.foregroundColor: textColor,
                                                               .font: selectedStateFont],
                                                              for: .selected)

            semtentedControlAppearance.setTitleTextAttributes([.foregroundColor: textColor,
                                                               .font: normalStateFont],
                                                              for: .normal)
        }
    }
}
