//
//  SelectUserTypeScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 1.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SelectUserTypeScreen: View {
    let scope: SignUpScope.SelectUserType
    
    @ObservedObject var selectedUserType: SwiftDataSource<DataUserType>
    
    private let userTypesOrder = [
        [DataUserType.stockist, DataUserType.retailer],
        [DataUserType.hospital/*, DataUserType.seasonBoy*/]
    ]
    
    var body: some View {
        VStack(spacing: 32) {
            LocalizedText(localizationKey: "who_are_you",
                          textWeight: .bold,
                          fontSize: 24)
            
            let spacing: CGFloat = 20
            VStack(spacing: spacing) {
                ForEach((0..<userTypesOrder.count)) { rowIndex in
                    let row = userTypesOrder[rowIndex]
                    
                    HStack(spacing: spacing) {
                        ForEach ((0..<row.count)) { elementIndex in
                            let userType = row[elementIndex]
                            UserTypeView(userType: userType, isSelected: self.selectedUserType.value == userType)
                                .onTapGesture {
                                    self.scope.chooseUserType(userType: userType)
                                }
                        }
                    }
                }
            }
        }
        .modifier(SignUpButton(isEnabled: selectedUserType.value != nil, action: goToPersonalData))
        .screenLogger(withScreenName: "SelectUserTypeScreen",
                      withScreenClass: SelectUserTypeScreen.self)
    }
    
    init(scope: SignUpScope.SelectUserType) {
        self.scope = scope
        
        selectedUserType = SwiftDataSource(dataSource: scope.userType)
    }
    
    private func goToPersonalData() {
        guard self.selectedUserType.value != nil else { return }
        
        scope.goToPersonalData()
    }
}

fileprivate struct UserTypeView: View {
    let userType: DataUserType
    let isSelected: Bool
    
    var body: some View {
        ZStack(alignment: .bottom) {
            let strokeColor: Color = isSelected ? AppColor.yellow.color : AppColor.white.color
            let corderRadius: CGFloat = 8
            
            AppColor.white.color
                .cornerRadius(corderRadius)
                .overlay(RoundedRectangle(cornerRadius: corderRadius)
                            .stroke(strokeColor, style: StrokeStyle(lineWidth: 2)))
            
            Group {
                if let imageName = self.userType.imageName {
                    Image(imageName)
                }
            }.frame(maxHeight: .infinity)
            
            LocalizedText(localizationKey: userType.localizedName,
                          textWeight: .bold)
                .padding(.bottom, 20)
        }
        .aspectRatio(1, contentMode: .fit)
        .frame(width: 160, height: 160)
    }
}
