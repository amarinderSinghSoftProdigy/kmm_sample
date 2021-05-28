//
//  UserManagementScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct UserManagementScreen: View {
    let scope: ManagementScope.User
    
    @ObservedObject var userSearchText: SwiftDataSource<NSString>
    @ObservedObject var activeTab: SwiftDataSource<ManagementScope.Tab>
    
    @ObservedObject var totalUsersNumber: SwiftDataSource<KotlinInt>
    @ObservedObject var users: SwiftDataSource<NSArray>

    var body: some View {
        let imageName: String
        let searchBarPlaceholderKey: String
        let screenName: String
        
        switch self.scope {
        
        case is ManagementScope.UserStockist:
            imageName = "Stockist"
            searchBarPlaceholderKey = "stockists"
            screenName = "Stockist"
        
        case is ManagementScope.UserRetailer:
            imageName = "Retailer"
            searchBarPlaceholderKey = "retailers"
            screenName = "Retailer"
            
        case is ManagementScope.UserHospital:
            imageName = "Hospital"
            searchBarPlaceholderKey = "hospitals"
            screenName = "Hospital"
            
        case is ManagementScope.UserSeasonBoy:
            imageName = "SeasonBoy"
            searchBarPlaceholderKey = "seasonBoys"
            screenName = "SeasonBoy"
            
        default:
            return AnyView(EmptyView())
        }
        
        let view = AnyView(
            getGeneralScreen(withSearchImageName: imageName,
                             withSearchBarPlaceholderKey: searchBarPlaceholderKey)
                .screenLogger(withScreenName: "ManagementScopeUser.\(screenName)",
                              withScreenClass: UserManagementScreen.self)
        )
        
        if let notificationsScope = self.scope as? CommonScopeWithNotifications {
            return AnyView(
                view
                    .notificationAlertSender(withHandler: notificationsScope)
            )
        }
        
        return view
    }
    
    init(scope: ManagementScope.User) {
        self.scope = scope
        
        self.userSearchText = SwiftDataSource(dataSource: scope.searchText)
        self.activeTab = SwiftDataSource(dataSource: scope.activeTab)
        
        self.totalUsersNumber = SwiftDataSource(dataSource: scope.totalItems)
        self.users = SwiftDataSource(dataSource: scope.items)
    }
    
    private func getGeneralScreen(withSearchImageName imageName: String,
                                  withSearchBarPlaceholderKey searchBarPlaceholderKey: String) -> some View {
        let selectedOption = Binding(get: {
            guard let activeTab = self.activeTab.value else { return 0 }

            return scope.tabs.firstIndex(of: activeTab) ?? 0
        }, set: { newValue in
            scope.selectTab(tab: scope.tabs[newValue])
        })
        
        let image = Image(imageName).resizable()
        let isSeasonBoy = self.scope is ManagementScope.UserSeasonBoy

        return AnyView(
            ZStack(alignment: .bottomTrailing) {
                VStack(spacing: 16) {
                    SearchBar(placeholderLocalizationKey: searchBarPlaceholderKey,
                              searchText: userSearchText.value,
                              leadingButton: SearchBar.SearchBarButton(emptyTextButton: .custom(AnyView(image)),
                                                                       enteredTextButton: .smallMagnifyingGlass),
                              trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                        enteredTextButton: .clear),
                              onTextChange: { newValue, _ in scope.search(value: newValue) })
                    
                    if scope.tabs.count == 1 {
                        self.singleTabView
                    }
                    else {
                        self.getOptionsPicker(withSelectedOption: selectedOption)
                    }
                    
                    TransparentList(data: users,
                                    dataType: DataEntityInfo.self,
                                    listName: self.activeTab.value?.listName,
                                    pagination: scope.pagination,
                                    onTapGesture: { scope.selectItem(item: $0) },
                                    loadItems: { scope.loadItems() }) { _, element in
                        Group {
                            if isSeasonBoy {
                                SeasonBoyView(seasonBoy: element)
                            }
                            else {
                                NonSeasonBoyView(user: element)
                            }
                        }
                    }
                    .hideKeyboardOnTap()
                }
                .keyboardResponder()
                .padding(.horizontal, 16)
                .padding(.vertical, 32)
                
                if let retailersManagementScope = self.scope as? ManagementScope.UserRetailer,
                   retailersManagementScope.canAdd {
                    Button(action: { retailersManagementScope.requestCreateRetailer() }) {
                        Image(systemName: "plus")
                            .foregroundColor(appColor: .darkBlue)
                            .padding(20)
                            .background(Circle().fill(appColor: .yellow))
                    }
                    .padding(30)
                }
            }
        )
    }
    
    private var singleTabView: some View {
        guard let activeTab = self.activeTab.value else { return AnyView(EmptyView()) }
        
        return AnyView(
            TabOptionView(localizationKey: activeTab.stringId,
                          isSelected: true,
                          itemsNumber: (self.totalUsersNumber.value as? Int) ?? 0)
        )
    }
    
    private func getOptionsPicker(withSelectedOption selectedOption: Binding<Int>) -> some View {
        HStack {
            ForEach(0..<scope.tabs.count) { index in
                TabOptionView(localizationKey: scope.tabs[index].stringId,
                              isSelected: selectedOption.wrappedValue == index,
                              itemsNumber: (self.totalUsersNumber.value as? Int) ?? 0)
                    .onTapGesture {
                        selectedOption.wrappedValue = index
                    }
            }
        }
        .padding(.vertical, 2)
        .padding(.horizontal, 3)
        .background(AppColor.navigationBar.color.cornerRadius(8))
    }
    
    private struct NonSeasonBoyView: View {
        let user: DataEntityInfo
        
        var body: some View {
            ZStack {
                AppColor.white.color
                    .cornerRadius(5)
                
                VStack(spacing: 5) {
                    VStack(alignment: .leading, spacing: 2) {
                        HStack(spacing: 10) {
                            HStack(alignment: .top, spacing: 4) {
                                Text(user.tradeName)
                                    .medicoText(textWeight: .semiBold,
                                                fontSize: 16,
                                                multilineTextAlignment: .leading)
                                
                                if user.isVerified == true {
                                    Image("VerifyMark")
                                        .padding(.top, 2)
                                }
                            }
                            
                            Spacer()
                            
                            if let status = user.subscriptionData?.status {
                                LocalizedText(localizationKey: status.serverValue,
                                              textWeight: .medium,
                                              fontSize: 15,
                                              color: status.statusColor)
                            }
                        }
                        
                        Text(user.geoData.landmark)
                            .medicoText(textWeight: .medium,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                    
                    HStack(spacing: 10) {
                        SmallAddressView(location: user.geoData.fullAddress())
                        
                        Spacer()
                        
                        Text(user.geoData.formattedDistance)
                            .medicoText(fontSize: 12,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                    }
                }
                .padding(.horizontal, 10)
                .padding(.vertical, 7)
            }
        }
    }
    
    private struct SeasonBoyView: View {
        let seasonBoy: DataEntityInfo
        
        var body: some View {
            ZStack {
                AppColor.white.color
                    .cornerRadius(5)
                
                VStack(spacing: 6) {
                    HStack {
                        Text(seasonBoy.tradeName)
                            .medicoText(textWeight: .bold,
                                        fontSize: 15,
                                        multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        if let status = seasonBoy.subscriptionData?.status {
                            LocalizedText(localizationKey: status.serverValue,
                                          textWeight: .medium,
                                          fontSize: 15,
                                          color: status.statusColor)
                        }
                    }
                    
                    HStack {
                        Text(seasonBoy.geoData.fullAddress())
                            .medicoText(textWeight: .medium,
                                        color: .grey3,
                                        multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        if let phoneNumber = seasonBoy.phoneNumber {
                            let formattedphoneNumber = PhoneNumberUtil.shared.getFormattedPhoneNumber(phoneNumber)
                            Text(formattedphoneNumber)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 15,
                                            color: .lightBlue,
                                            multilineTextAlignment: .leading)
                        }
                    }
                }
                .padding(12)
            }
        }
    }
}

struct SmallAddressView: View {
    let location: String
    
    let fontWeight: TextWeight
    let fontSize: CGFloat
    let color: AppColor
    
    var body: some View {
        HStack(spacing: 5) {
            Image("SmallAddress")
                .renderingMode(.template)
                .foregroundColor(appColor: color)
            
            Text(location)
                .medicoText(textWeight: fontWeight,
                            fontSize: fontSize,
                            color: color,
                            multilineTextAlignment: .leading)
        }
    }
    
    init(location: String,
         fontWeight: TextWeight = .bold,
         fontSize: CGFloat = 14,
         color: AppColor = .grey3) {
        self.location = location
        
        self.fontWeight = fontWeight
        self.fontSize = fontSize
        self.color = color
    }
}

extension ManagementScope.Tab {
    var listName: ListScrollData.Name? {
        switch self {
        
        case .allStockists:
            return .allStockists
            
        case .yourStockists:
            return .yourStockists
            
        case .yourRetailers:
            return .yourRetailers
            
        case .yourHospitals:
            return .yourHospitals
            
        case .yourSeasonBoys:
            return .yourSeasonBoys
            
        default:
            return nil
        }
    }
}
