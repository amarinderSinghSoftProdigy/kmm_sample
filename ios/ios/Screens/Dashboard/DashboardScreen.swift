//
//  DashboardScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 17.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct DashboardScreen: View {
    let scope: DashboardScope
    
    @ObservedObject var unreadNotifications: SwiftDataSource<KotlinInt>
    @ObservedObject var dashboard: SwiftDataSource<DataDashboardData>
    
    var body: some View {
        VStack(spacing: 16) {
            HStack(spacing: 17) {
                BigButton(imageName: "Bell",
                          textKey: "notifications",
                          counter: unreadNotifications.value as? Int)
                    .onTapGesture {
                        scope.goToNotifications()
                    }
                
                BigButton(imageName: "PurchaseOrders",
                          textKey: "purchase_orders",
                          counter: Int(dashboard.value?.ordersCount ?? 0))
                    .onTapGesture {
                        scope.goToOrders()
                    }
            }
            
            if let dashboard = self.dashboard.value {
                let viewSections = scope.sections.sections(number: 2)
                
                ForEach(0..<viewSections.count) {
                    let sectionsRow = viewSections[$0]
                    
                    HStack(spacing: 17) {
                        ForEach(0..<sectionsRow.count) {
                            let section = sectionsRow[$0]
                            
                            SectionView(sectionNameLocalizationKey: section.stringId,
                                        iconName: section.getIconName(),
                                        count: section.getCount(dashboardData: dashboard),
                                        onClick: section.isClickable ? { scope.selectSection(section: section) } : nil)
                        }
                    }
                }
                
                if scope.userType == .stockist {
                    HStack(spacing: 0) {
                        VStack(alignment: .leading) {
                            LocalizedText(localizationKey: "in_stock",
                                          textWeight: .semiBold,
                                          fontSize: 12)
                                .opacity(0.6)
                            
                            Text("\(dashboard.stockStatusData?.inStock ?? 0)")
                                .medicoText(textWeight: .bold,
                                            fontSize: 24)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.vertical, 19)
                        .padding(.horizontal, 33)
                        .strokeBorder(.lightGrey,
                                      fill: .lightGreen,
                                      corners: [.topLeft, .bottomLeft])
                        
                        VStack(alignment: .trailing) {
                            LocalizedText(localizationKey: "out_of_stock",
                                          textWeight: .semiBold,
                                          fontSize: 12)
                                .opacity(0.6)
                            
                            Text("\(dashboard.stockStatusData?.outOfStock ?? 0)")
                                .medicoText(textWeight: .bold,
                                            fontSize: 24)
                        }
                        .frame(maxWidth: .infinity, alignment: .trailing)
                        .padding(.vertical, 19)
                        .padding(.horizontal, 33)
                        .strokeBorder(.lightGrey,
                                      fill: .lightRed,
                                      corners: [.topRight, .bottomRight])
                    }
                    
                    ProductsList(titleLocalizationKey: "today_sold_products",
                                 products: dashboard.productInfo?.mostSold ?? .init())
                    
                    ProductsList(titleLocalizationKey: "most_searched_products",
                                 products: dashboard.productInfo?.mostSearched ?? .init())
                }
            }
        }
        .padding(.vertical, 32)
        .padding(.horizontal, 16)
        .scrollView()
    }
    
    init(scope: DashboardScope) {
        self.scope = scope
        
        self.unreadNotifications = SwiftDataSource(dataSource: scope.unreadNotifications)
        self.dashboard = SwiftDataSource(dataSource: scope.dashboard)
    }
    
    private struct BigButton: View {
        let imageName: String
        let textKey: String
        
        let counter: Int?
        
        var body: some View {
            VStack(spacing: 12) {
                ZStack(alignment: .topTrailing) {
                    Image(imageName)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 35, height: 35)
                        .padding(.top, 10)
                        .padding(.horizontal, 18)
                    
                    if let counter = self.counter,
                       counter > 0 {
                        ZStack {
                            Circle()
                                .fill(appColor: .red)
                                .frame(width: 30, height: 30)

                            Text(String(counter))
                                .medicoText(textWeight: .bold,
                                            fontSize: 16,
                                            color: AppColor.white)
                        }
                    }
                }
                
                LocalizedText(localizationKey: textKey,
                              textWeight: .semiBold,
                              fontSize: 12,
                              color: .lightGrey)
                    .lineLimit(1)
                    .minimumScaleFactor(0.5)
            }
            .modifier(DashboardItemModifier(isClickable: true))
        }
        
        init(imageName: String,
             textKey: String,
             counter: Int?) {
            self.imageName = imageName
            self.textKey = textKey
            
            self.counter = counter
        }
    }
    
    private struct SectionView: View {
        let sectionNameLocalizationKey: String
        let iconName: String?
        
        let count: Int32?
        
        let onClick: (() -> Void)?
        
        var body: some View {
            Group {
                if let count = self.count {
                    HStack {
                        VStack(alignment: .leading) {
                            LocalizedText(localizationKey: sectionNameLocalizationKey,
                                          textWeight: .semiBold,
                                          fontSize: 12)
                                .lineLimit(1)
                                .minimumScaleFactor(0.5)
                            
                            Text("\(count)")
                                .medicoText(textWeight: .bold,
                                            fontSize: 28,
                                            color: .lightBlue)
                        }
                        
                        Spacer()
                        
                        if let iconName = self.iconName {
                            Image(iconName)
                                .renderingMode(.template)
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .foregroundColor(appColor: .lightGrey)
                                .frame(width: 36, height: 36)
                        }
                    }
                }
                else {
                    VStack(spacing: 12) {
                        if let iconName = self.iconName {
                            Image(iconName)
                                .renderingMode(.template)
                                .resizable()
                                .aspectRatio(contentMode: .fit)
                                .foregroundColor(appColor: .darkBlue)
                                .frame(width: 38, height: 38)
                        }
                        
                        LocalizedText(localizationKey: sectionNameLocalizationKey,
                                      textWeight: .semiBold,
                                      fontSize: 12,
                                      color: .lightGrey)
                    }
                }
            }
            .padding(24)
            .modifier(DashboardItemModifier(isClickable: onClick != nil))
            .onTapGesture {
                onClick?()
            }
        }
    }
    
    private struct ProductsList: View {
        let titleLocalizationKey: String
        let products: [DataProductSold]
        
        @State private var expanded = false
        
        var body: some View {
            VStack(spacing: 0) {
                HStack(spacing: 16) {
                    LocalizedText(localizationKey: titleLocalizationKey,
                                  textWeight: .semiBold,
                                  fontSize: 12)
                    
                    Spacer()
                    
                    Image(systemName: "chevron.right")
                        .foregroundColor(appColor: .darkBlue)
                        .opacity(0.54)
                        .rotationEffect(.degrees(expanded ? -90 : 90))
                        .animation(.linear(duration: 0.2))
                        .padding(.trailing, 18)
                }
                .padding(.vertical, 20)
                .padding(.horizontal, 24)
                .strokeBorder(.lightGrey,
                              fill: .white,
                              cornerRadius: 14)
                .onTapGesture {
                    expanded.toggle()
                }
                
                if expanded {
                    VStack(spacing: 0) {
                        ForEach(0..<products.count, id: \.self) {
                            let item = products[$0]

                            HStack {
                                Text(item.productName)
                                    .medicoText(textWeight: .medium,
                                                fontSize: 12)

                                Spacer()

                                Text("\(item.count)")
                                    .medicoText(textWeight: .medium,
                                                fontSize: 12)
                                    .padding(.vertical, 4)
                                    .padding(.horizontal, 8)
                                    .background(
                                        RoundedRectangle(cornerRadius: 6)
                                            .fill(appColor: .darkBlue)
                                            .opacity(0.08)
                                    )
                            }
                            .padding(.vertical, 8)
                            .padding(.horizontal, 16)
                            .background(appColor: $0 % 2 == 0 ? .white : .lightGrey)
                        }
                    }
                    .padding(.bottom, 10)
                }
            }
            .background(
                RoundedRectangle(cornerRadius: 16)
                    .fill(appColor: .white)
            )
        }
    }
    
    private struct DashboardItemModifier: ViewModifier {
        let isClickable: Bool
        
        func body(content: Content) -> some View {
            ZStack(alignment: .topTrailing) {
                content
                    .frame(maxWidth: .infinity)
                    .frame(height: 120)
                
                if isClickable {
                    Image(systemName: "chevron.right")
                        .foregroundColor(appColor: .darkBlue)
                        .font(.system(size: 12, weight: .semibold))
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 10, height: 10)
                        .padding(17)
                }
            }
            .frame(maxWidth: 250)
            .background(AppColor.white.color.cornerRadius(24))
        }
    }
}

extension DashboardScope.Section {
    func getIconName() -> String? {
        switch self {
        case .stockistCount, .stockistAdd:
            return "Stockist"
            
        case .retailerCount, .retailerAdd:
            return "Retailer"
            
        case .seasonBoyCount:
            return "SeasonBoy"
            
        case .hospitalCount:
            return "Hospital"
            
        case .stockistConnect:
            return "ConnectStockist"
            
        default:
            return nil
        }
    }
    
    func getCount(dashboardData: DataDashboardData) -> Int32? {
        switch self {
        case .stockistCount:
            return dashboardData.userData.stockist.connected
            
        case .retailerCount:
            return dashboardData.userData.retailer?.connected
            
        case .seasonBoyCount:
            return dashboardData.userData.seasonBoy?.connected
            
        case .hospitalCount:
            return dashboardData.userData.hospital?.connected
            
        default:
            return nil
        }
    }
}
