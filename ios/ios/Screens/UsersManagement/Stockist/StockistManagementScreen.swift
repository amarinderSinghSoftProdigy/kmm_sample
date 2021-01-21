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
    
    @State private var chosenStockist: String?
    
    @State private var selectedOption = 0
    let options = ["your_stockists", "all_stockists"]
    
    let stockists = ["Pharmacy Doctors", "Pharmacy Doctors2"]

    var body: some View {
        let stockistImage = Image("Stockist").resizable()
    
        let bottomSheetOpened = Binding(get: { self.chosenStockist != nil },
                                        set: { newValue in if newValue == false { self.chosenStockist = nil }  })

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
                
                TransparentList(data: stockists) { _, element in
                    StockistView(stockist: element)
                        .onTapGesture {
                            self.chosenStockist = element
                        }
                }
            }
            .keyboardResponder()
            .padding(.horizontal, 16)
            .padding(.vertical, 32)
            
            if let chosenStockist = self.chosenStockist {
                BottomSheetView(isOpened: bottomSheetOpened, maxHeight: 350) {
                    StockistDetails(stockist: chosenStockist)
                }.edgesIgnoringSafeArea(.all)
            }
        }
        .screenLogger(withScreenName: "StockistManagement",
                      withScreenClass: StockistManagementScreen.self)
    }
    
    private var stockistsOptionsPicker: some View {
        Picker(selection: $selectedOption, label: Text("")) {
            ForEach(0 ..< options.count) { index in
                LocalizedText(localizationKey: options[index])
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
    
    private struct StockistView: View {
        let stockist: String
        
        var body: some View {
            ZStack {
                AppColor.white.color
                    .cornerRadius(5)
                
                HStack(alignment: .top) {
                    VStack(alignment: .leading) {
                        Text(stockist)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 16,
                                        multilineTextAlignment: .leading)
                        
                        HStack(spacing: 5) {
                            Image("SmallAddress")
                            
                            Text("Vijayawada 520001")
                                .medicoText(textWeight: .medium,
                                            color: .grey3,
                                            multilineTextAlignment: .leading)
                        }
                    }
                    
                    Spacer()
                    
                    let statusColor: AppColor = .lightBlue
                    LocalizedText(localizationKey: "subscribed",
                                  textWeight: .medium,
                                  fontSize: 15,
                                  color: statusColor)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 7)
            }
        }
    }
    
    private struct StockistDetails: View {
        let stockist: String
        
        var body: some View {
            VStack(alignment: .leading, spacing: 16) {
                HStack {
                    VStack(alignment: .leading, spacing: 5) {
                        Text(stockist)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 20,
                                        multilineTextAlignment: .leading)
                        
                        Text("Mumbai")
                            .medicoText(textWeight: .medium,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                    
                    Spacer()
                    
                    MedicoButton(localizedStringKey: "subscribe",
                                 width: 91,
                                 height: 31,
                                 cornerRadius: 5,
                                 fontSize: 14) {
                        
                    }
                }
                
                HStack {
                    HStack(spacing: 60) {
                        URLImage(withURL: "", withDefaultImageName: "DefaultProduct")
                            .frame(width: 96, height: 96)
                        
                        VStack(alignment: .leading, spacing: 13) {
                            HStack(spacing: 5) {
                                Image("SmallAddress")
                                
                                Text("Mumbai 200005")
                                    .medicoText(textWeight: .bold,
                                                color: .grey3,
                                                multilineTextAlignment: .leading)
                            }
                            
                            VStack(alignment: .leading,spacing: 5) {
                                Text("2 km from you")
                                    .medicoText(fontSize: 12,
                                                color: .grey3,
                                                multilineTextAlignment: .leading)
                                
                                LocalizedText(localizationKey: "see_on_the_map",
                                              textWeight: .bold,
                                              fontSize: 12,
                                              color: .lightBlue,
                                              multilineTextAlignment: .leading)
                            }
                        }
                    }
                    
                    Spacer()
                }
                
                VStack(alignment: .leading, spacing: 5) {
                    getInfoPanel(withTitleKey: "status", withValueKey: "subscribed")
                    getInfoPanel(withTitleKey: "gstin_number", withValueKey: "405569546")
                    getInfoPanel(withTitleKey: "payment_method", withValueKey: "Cash")
                    getInfoPanel(withTitleKey: "orders", withValueKey: "3")
                }
                
                Spacer()
            }
            .padding(.horizontal, 25)
            .padding(.vertical, 22)
        }
        
        private func getInfoPanel(withTitleKey titleKey: String,
                                  withValueKey valueKey: String) -> some View {
            HStack(spacing: 3) {
                LocalizedText(localizationKey: titleKey,
                              multilineTextAlignment: .leading)
                
                LocalizedText(localizationKey: valueKey,
                              textWeight: .medium,
                              multilineTextAlignment: .leading)
            }
        }
    }
}
