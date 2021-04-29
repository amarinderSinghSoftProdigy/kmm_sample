//
//  NotificationsScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 2.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct NotificationsScreen: View {
    let scope: NotificationScope.All
    
    @ObservedObject var notifications: SwiftDataSource<NSArray>
    @ObservedObject var notificationsSearch: SwiftDataSource<NSString>
    
    var body: some View {
        VStack(spacing: 16) {
            SearchBar(placeholderLocalizationKey: "notifications",
                      searchText: notificationsSearch.value,
                      leadingButton: .init(emptyTextButton: .custom(AnyView(self.searchBarLeadingButton)),
                                           enteredTextButton: .smallMagnifyingGlass),
                      trailingButton: .init(emptyTextButton: .magnifyingGlass,
                                            enteredTextButton: .clear)) { value, _ in
                scope.search(value: value)
            }
            
            TransparentList(data: notifications,
                            dataType: DataNotificationData.self,
                            listName: .notifications,
                            pagination: scope.pagination,
                            onTapGesture: { _ in },
                            loadItems: { scope.loadItems() } ) { _, element in
                NotificationView(data: element) {
                    scope.selectItem(item: element)
                }
            }
        }
        .hideKeyboardOnTap()
        .padding(.horizontal, 16)
        .padding(.top, 32)
        .screenLogger(withScreenName: "NotificationsScreen",
                      withScreenClass: NotificationsScreen.self)
    }
    
    private var searchBarLeadingButton: some View {
        ZStack(alignment: .topTrailing) {
            Image("Bell")
                .resizable()
                .padding(2)
            
            Circle()
                .stroke(AppColor.white.color, lineWidth: 1.2)
                .background(Circle().fill(appColor: .red))
                .frame(width: 6, height: 6)
                .padding(.top, 5)
                .padding(.trailing, 3)
        }
    }
    
    init(scope: NotificationScope.All) {
        self.scope = scope
        
        self.notifications = SwiftDataSource(dataSource: scope.items)
        self.notificationsSearch = SwiftDataSource(dataSource: scope.searchText)
    }
    
    private struct NotificationView: View {
        let data: DataNotificationData
        let onButtonTap: () -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 13) {
                VStack(alignment: .leading, spacing: 3) {
                    HStack {
                        Text(data.title)
                            .medicoText(textWeight: .semiBold,
                                        color: .lightBlue,
                                        multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        LocalizedText(localizedStringKey: getFormattedTimeLocalizedStringKey(forSentAt: data.sentAt),
                                      testingIdentifier: "hours",
                                      color: .grey3,
                                      multilineTextAlignment: .leading)
                    }
                    
                    Text(data.body)
                        .medicoText(color: .grey3,
                                    multilineTextAlignment: .leading)
                }
                
                HStack {
                    LocalizedText(localizationKey: data.status.stringId,
                                  textWeight: .semiBold,
                                  fontSize: 16,
                                  color: .darkBlue,
                                  multilineTextAlignment: .leading)
                        .opacity(data.status == .unread ? 1 : 0.65)
                    
                    Spacer()
                    
                    let buttonTextKey = data.selectedAction?.completedActionStringId ?? data.type.buttonStringId
                    
                    if let selectedAction = data.selectedAction {
                        LocalizedText(localizationKey: buttonTextKey,
                                      textWeight: .semiBold,
                                      fontSize: 12,
                                      color: selectedAction.statusColor,
                                      multilineTextAlignment: .leading)
                    }
                    else {
                        MedicoButton(localizedStringKey: buttonTextKey,
                                     width: 150,
                                     height: 30,
                                     fontSize: 12,
                                     fontWeight: .bold,
                                     fontColor: data.type.statusButtonTextColor,
                                     buttonColor: data.type.statusButtonColor) {
                            onButtonTap()
                        }
                        .buttonStyle(BorderlessButtonStyle())
                    }
                }
            }
            .padding(11)
            .background(AppColor.white.color.cornerRadius(5))
        }
        
        private func getFormattedTimeLocalizedStringKey(forSentAt sentAt: Int64) -> LocalizedStringKey {
            let sentAtDate = Date(timeIntervalSince1970: (Double(sentAt) / 1000.0))
            let currrentDate = Date()
            
            let components = Calendar.current.dateComponents([.minute, .hour, .day, .month, .year],
                                                             from: sentAtDate,
                                                             to: currrentDate)
            
            if let years = components.year, years > 0 {
                return LocalizedStringKey("years \(years)")
            }
            
            if let months = components.month, months > 0 {
                return LocalizedStringKey("months \(months)")
            }
            
            if let days = components.day, days > 0 {
                if days >= 7 {
                    return LocalizedStringKey("weeks \(days / 7)")
                }
                    
                return LocalizedStringKey("days \(days)")
            }
            
            if let hours = components.hour, hours > 0 {
                return LocalizedStringKey("hours \(hours)")
            }
            
            return LocalizedStringKey("minutes \(components.minute ?? 0)")
        }
    }
}
