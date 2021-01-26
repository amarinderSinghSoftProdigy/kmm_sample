//
//  StockistManagementScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct StockistManagementScreen: View {
    let scope: ManagementScopeStockist
    
    @ObservedObject var stockistSearchText: SwiftDataSource<NSString>
    @ObservedObject var activeTab: SwiftDataSource<ManagementScopeTab>
    @ObservedObject var stockists: SwiftDataSource<NSArray>

    var body: some View {
        let selectedOption = Binding(get: {
            guard let activeTab = self.activeTab.value else { return 0 }

            return scope.tabs.firstIndex(of: activeTab) ?? 0
        }, set: { newValue in
            scope.selectTab(tab: scope.tabs[newValue])
        })
        
        let stockistImage = Image("Stockist").resizable()

        ZStack(alignment: .topLeading) {
            AppColor.primary.color
                .hideKeyboardOnTap()
            
            VStack(spacing: 16) {
                SearchBar(placeholderLocalizationKey: "stockists",
                          searchText: stockistSearchText.value,
                          leadingButton: SearchBar.SearchBarButton(emptyTextButton: .custom(AnyView(stockistImage)),
                                                                   enteredTextButton: .smallMagnifyingGlass),
                          trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                    enteredTextButton: .clear),
                          onTextChange: { newValue in scope.search(value: newValue) })
                
                self.getStockistsOptionsPicker(withSelectedOption: selectedOption)
                
                let listName: ListScrollData.Name =
                    self.activeTab.value == .allStockists ? .allStockists : .yourStockists
                TransparentList(data: stockists,
                                dataType: DataEntityInfo.self,
                                listName: listName,
                                pagination: scope.pagination,
                                onTapGesture: { scope.selectItem(item: $0) },
                                loadItems: { scope.loadItems() }) { _, element in
                    StockistView(stockist: element)
                }
            }
            .keyboardResponder()
            .padding(.horizontal, 16)
            .padding(.vertical, 32)
        }
        .notificationAlert(withHandler: scope)
        .screenLogger(withScreenName: "StockistManagement",
                      withScreenClass: StockistManagementScreen.self)
    }
    
    init(scope: ManagementScopeStockist) {
        self.scope = scope
        
        self.stockistSearchText = SwiftDataSource(dataSource: scope.searchText)
        self.activeTab = SwiftDataSource(dataSource: scope.activeTab)
        self.stockists = SwiftDataSource(dataSource: scope.items)
    }
    
    private func getStockistsOptionsPicker(withSelectedOption selectedOption: Binding<Int>) -> some View {
        Picker(selection: selectedOption, label: Text("")) {
            ForEach(0..<scope.tabs.count) { index in
                LocalizedText(localizationKey: scope.tabs[index].stringId)
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
        let stockist: DataEntityInfo
        
        var body: some View {
            ZStack {
                AppColor.white.color
                    .cornerRadius(5)
                
                HStack(alignment: .top) {
                    VStack(alignment: .leading) {
                        Text(stockist.traderName)
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 16,
                                        multilineTextAlignment: .leading)
                        
                        SmallAddresView(location: stockist.location, pincode: stockist.pincode)
                    }
                    
                    Spacer()
                    
                    let status = stockist.getSubscriptionStatus()
                    LocalizedText(localizationKey: status?.serverValue ?? "",
                                  textWeight: .medium,
                                  fontSize: 15,
                                  color: status == .subscribed ? .lightBlue : .yellow)
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 7)
            }
        }
    }
}

struct SmallAddresView: View {
    let location: String
    let pincode: String
    
    var body: some View {
        HStack(spacing: 5) {
            Image("SmallAddress")
            
            Text("\(location) \(pincode)")
                .medicoText(textWeight: .bold,
                            color: .grey3,
                            multilineTextAlignment: .leading)
        }
    }
}
