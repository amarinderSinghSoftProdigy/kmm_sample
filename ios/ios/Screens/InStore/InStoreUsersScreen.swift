//
//  InStoreUsers.swift
//  Medico
//
//  Created by user on 01/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

//MARK: Main Screen
struct InStoreUsersScreen: View {
    
    let scope: InStoreUsersScope
    
    @State private var expandedItems = [String: Bool]()
    @State private var selectedItem = String()
        
    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var totalItems: SwiftDataSource<KotlinInt>
    @ObservedObject var searchText: SwiftDataSource<NSString>
    
    var body: some View {
        
        VStack {
            
            SearchBar(placeholderLocalizationKey: "search_tradename_mobile_no",
                      searchText: searchText.value,
                      style: .custom(fontWeight: .medium, placeholderOpacity: 0.5),
                      backgroundColor: .blueWhite,
                      leadingButton: nil,
                      trailingButton: SearchBar.SearchBarButton(emptyTextButton: .magnifyingGlass,
                                                                enteredTextButton: .clear),
                      onTextChange: { newValue, _ in scope.search(value: newValue) })
                .padding(20)
                .background(appColor: .white)
            
            let isUserAvailable = (items.value?.count ?? 0) > 0
            
            if isUserAvailable {
                
                TransparentList(data: items,
                                dataType: DataInStoreUser.self,
                                listName: .instoreOrders,
                                pagination: scope.pagination,
                                elementsSpacing: 0,
                                onTapGesture: { _ in  },
                                loadItems: { scope.loadItems() }) { _, item in
                    
                    ExpandableUserItem(expandedItems: $expandedItems,
                                       selectedItem: $selectedItem,
                                       seller: item)
                        .buttonStyle(PlainButtonStyle())
                }
                
            }
            else {
                InStoreEmptyListView(imageName: "EmptyInstoreOrders",
                                     titleLocalizationKey: "empty_instore_users")
            }
            
            InstoreUserActionView(enableContinue: !selectedItem.isEmpty, onClickAddUser: addUserAction, onClickContinue: continueAction)
        }
        .screenLogger(withScreenName: "InStoreUsersScreen",
                      withScreenClass: InStoreUsersScreen.self)
    }
    
    private func continueAction()  {
        if let users = self.items.value as? [DataInStoreUser] {
            if let selectedUser = users.filter({ $0.mobileNumber == selectedItem }).first {
                scope.selectItem(item: selectedUser)
            }
        }
    }
    
    private func addUserAction()  {
        scope.goToInStoreCreateUser()
    }
    
    init(scope: InStoreUsersScope) {
        self.scope = scope
        self.searchText = SwiftDataSource(dataSource: scope.searchText)
        self.items = SwiftDataSource(dataSource: scope.items)
        self.totalItems = SwiftDataSource(dataSource: scope.totalItems)
        self.totalItems = SwiftDataSource(dataSource: scope.totalItems)
    }
}

//MARK: Expandable User Item
private struct ExpandableUserItem: View {
    
    @Binding var expandedItems: [String: Bool]
    @Binding var selectedItem: String
    
    let seller: DataInStoreUser
    
    var body: some View {
        let expanded = Binding(get: { expandedItems[seller.mobileNumber] == true },
                               set: { expandedItems[seller.mobileNumber] = $0 })
        ExpandableUserItemChild(seller: seller, expanded: expanded, selectedItem: $selectedItem)
    }
}

//MARK: Expandable Item Child
private struct ExpandableUserItemChild: View {
    
    let seller: DataInStoreUser
    
    @Binding var expanded: Bool
    @Binding var selectedItem: String
    
    var body: some View {
        
        VStack(alignment: .leading) {
            VStack(alignment: .leading) {
                HStack(spacing: 10) {
                    
                    let isChecked = selectedItem == seller.mobileNumber
                    RadioButton(checked: isChecked, onTapHandler: onTapRadioButton)
                    
                    Text(seller.tradeName)
                        .medicoText(textWeight: expanded ? .semiBold : .regular, fontSize: 14, color: .darkBlue, multilineTextAlignment: .leading)
                    
                    Spacer()
                    
                    Image(systemName: "chevron.right")
                        .foregroundColor(appColor: .darkBlue)
                        .opacity(0.54)
                        .rotationEffect(.degrees(expanded ? -90 : 90))
                        .animation(.linear(duration: 0.2))
                        .padding(.trailing, 10)
                        .frame(width: 15, height: 15)
                }
                
                if expanded {
                    ExpandableView(seller: seller)
                        .padding(.leading, 25)
                }
                
            }
            .padding(20)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(appColor: expanded ? .white : .clear)
        .contentShape(Rectangle())
        .onTapGesture {
            expanded.toggle()
        }
    }
    
    private func onTapRadioButton() {
        selectedItem = seller.mobileNumber
    }
}

//MARK: Expandable View
private struct ExpandableView: View {
    
    let seller: DataInStoreUser
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Group {
                Text(seller.addressData.address)
                    .medicoText()
                Text(seller.gstin).medicoText(textWeight: .medium, color: .lightBlue)
                DrugLicenseNumberView(title: "DL1: 20B:", number: seller.drugLicenseNo1)
                DrugLicenseNumberView(title: "DL2: 21B:", number: seller.drugLicenseNo2)
                StatusView(status: seller.status)
            }
        }
    }
    
    private struct DrugLicenseNumberView: View {
        let title: String
        let number: String
        var body: some View {
            HStack {
                Text(title).medicoText(textWeight: .semiBold, fontSize: 12)
                Text(number).medicoText(textWeight: .medium, fontSize: 12)
            }
        }
    }
    
    private struct StatusView: View {
        let status: String
        var body: some View {
            HStack(spacing: 2) {
                HStack {
                    LocalizedText(localizationKey: "status", textWeight: .semiBold, fontSize: 12, color: .darkBlue)
                    Text(status).medicoText(textWeight: .semiBold)
                        .autocapitalization(.words)
                }.padding(6)
            }
            .background(RoundedRectangle(cornerRadius: 6)
                            .fill(appColor: .lightGreen)
                            .overlay(
                                RoundedRectangle(cornerRadius: 6)
                                    .stroke(AppColor.green.color, lineWidth: 2)
                            ))
            
        }
    }
}

//MARK: Instore User Action View
private struct InstoreUserActionView: View {
    
    var enableContinue: Bool
    var onClickAddUser: ()->Void
    var onClickContinue: ()->Void
    
    var body: some View {
        
        GeometryReader { geometry in
            HStack {
                MedicoImageButton(localizedStringKey: "new_customer",
                                  cornerRadius: 24,
                                  fontSize: 15,
                                  fontWeight: .bold,
                                  fontColor: .lightBlue,
                                  buttonColor: .clear,
                                  imageName: "Plus",
                                  imageColor: .lightBlue,
                                  action: onClickAddUser)
                    .strokeBorder(.lightBlue,
                                  borderOpacity: 0.5,
                                  fill: .clear,
                                  lineWidth: 2,
                                  cornerRadius: 24)
                    .frame(width: geometry.size.width * 0.55)
                
                Spacer()
                
                MedicoButton(localizedStringKey: "continue",
                             isEnabled: enableContinue,
                             cornerRadius: 24,
                             fontSize: 15,
                             fontWeight: .bold,
                             fontColor: .white,
                             buttonColor: .lightBlue,
                             action: onClickContinue)
                    .frame(width: geometry.size.width * 0.40)
            }
        }
        .frame( height: 50, alignment: .bottom)
        .padding([.leading,.trailing], 20)
    }
}

//MARK: Radio Button
private struct RadioButton: View {
    
    var checked: Bool
    
    var onTapHandler: ()->Void
    
    var body: some View {
        Group {
            ZStack {
                Circle()
                    .stroke(AppColor.lightBlue.color, lineWidth: 2)
                    .opacity(checked ? 1 : 0.5)
                    .frame(width:20, height: 20)
                    .background(appColor: .clear)
                if checked {
                    Circle()
                        .fill(Color.white)
                        .frame(width: 16, height: 16)
                    Circle()
                        .fill(AppColor.lightBlue.color)
                        .frame(width: 12, height: 12)
                }
            }
        }
        .onTapGesture { self.onTapHandler() }
    }
}
