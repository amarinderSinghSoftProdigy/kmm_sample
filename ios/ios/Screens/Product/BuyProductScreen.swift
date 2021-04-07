//
//  BuyProductScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 5.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct BuyProductScreen: View {
    let scope: BuyProductScope
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16)  {
            ProductInfo(product: scope.product)
            
            ChooseSellerView(product: scope.product,
                             sellersInfo: scope.sellersInfo,
                             quantities: SwiftDataSource(dataSource: scope.quantities),
                             onQuantityIncrease: scope.inc,
                             onQuantityDecrease: scope.dec,
                             onSellerInfoSelect: scope.addToCart)
        }
        .screenLogger(withScreenName: "BuyProduct",
                      withScreenClass: BuyProductScreen.self)
    }
    
    private struct ProductInfo: View {
        let product: DataProductSearch
        
        var body: some View {
            HStack(spacing: 16) {
                ProductImage(medicineId: product.code,
                             size: .px123)
                    .frame(width: 71, height: 71)
                
                VStack(alignment: .leading, spacing: 7) {
                    Text(product.name)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                    
                    HStack(spacing: 8) {
                        Text(product.code)
                            .medicoText(color: .grey3,
                                        multilineTextAlignment: .leading)
                        
                        if let units = product.standardUnit {
                            Divider()
                                .frame(height: 13)
                            
                            HStack(spacing: 4) {
                                LocalizedText(localizationKey: "units:",
                                              multilineTextAlignment: .leading)
                                
                                Text(units)
                                    .medicoText(textWeight: .bold,
                                                color: .lightBlue,
                                                multilineTextAlignment: .leading)
                            }
                        }
                    }
                    
                    Text(product.uomName)
                        .medicoText(color: .lightBlue,
                                    multilineTextAlignment: .leading)
                }
            }
            .padding(16)
            .frame(maxWidth: .infinity)
            .background(appColor: .white)
        }
    }
    
    private struct ChooseSellerView: View {
        let product: DataProductSearch
        let sellersInfo: [DataSellerInfo]
        
        @ObservedObject var quantities: SwiftDataSource<NSDictionary>
        
        let onQuantityIncrease: (DataSellerInfo) -> ()
        let onQuantityDecrease: (DataSellerInfo) -> ()
        
        let onSellerInfoSelect: (DataSellerInfo) -> ()
        
        var body: some View {
            VStack(alignment: .leading, spacing: 12) {
                LocalizedText(localizationKey: "choose_seller",
                              textWeight: .medium,
                              fontSize: 16,
                              multilineTextAlignment: .leading)
                
                Divider()
                
                let quantities = self.quantities.value as? [DataSellerInfo: Int]
                
                VStack(spacing: 12)  {
                    ForEach(sellersInfo, id: \.self) {
                        SellerView(product: product,
                                   info: $0,
                                   quantity: quantities?[$0] ?? 0,
                                   onQuantityIncrease: onQuantityIncrease,
                                   onQuantityDecrease: onQuantityDecrease,
                                   onSellerInfoSelect: onSellerInfoSelect)
                    }
                }
                .scrollView()
            }
            .padding(.horizontal, 16)
        }
        
        private struct SellerView: View {
            let product: DataProductSearch
            let info: DataSellerInfo
            
            let quantity: Int
            
            let onQuantityIncrease: (DataSellerInfo) -> ()
            let onQuantityDecrease: (DataSellerInfo) -> ()
            
            let onSellerInfoSelect: (DataSellerInfo) -> ()
            
            var body: some View {
                ZStack(alignment: .leading) {
                    VStack(alignment: .leading, spacing: 0) {
                        HStack(spacing: 17) {
                            UserNameImage(username: info.tradeName)
                                .frame(width: 65, height: 65)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(info.tradeName)
                                    .medicoText(textWeight: .semiBold,
                                                fontSize: 16,
                                                multilineTextAlignment: .leading)
                                
                                HStack {
                                    Text(info.priceInfo.price.formattedPrice)
                                        .medicoText(textWeight: .bold,
                                                    fontSize: 18,
                                                    multilineTextAlignment: .leading)
                                    
                                    Spacer()
                                    
                                    getDetailView(withTitleLocalizationKey: "mrp:",
                                                  withBody: info.priceInfo.mrp.formattedPrice)
                                }
                                
                                HStack {
                                    Text(product.code)
                                        .medicoText(color: .grey3,
                                                    multilineTextAlignment: .leading)
                                    
                                    Spacer()
                                    
                                    getDetailView(withTitleLocalizationKey: "margin:",
                                                  withBody: info.priceInfo.marginPercent)
                                }
                                
                                HStack {
                                    getDetailView(withTitleLocalizationKey: "expiry:",
                                                  withBody: info.stockInfo.expireDate,
                                                  withBodyColor: .orange)
                                    
                                    Spacer()
                                    
                                    getDetailView(withTitleLocalizationKey: "stocks:",
                                                  withBody: String(info.stockInfo.availableQty))
                                }
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.vertical, 12)
                        .fixedSize(horizontal: false, vertical: true)
                        
                        AppColor.darkBlue.color
                            .opacity(0.12)
                            .frame(height: 1)
                        
                        HStack {
                            NumberPicker(quantity: quantity,
                                         onQuantityIncrease: { onQuantityIncrease(self.info) },
                                         onQuantityDecrease: { onQuantityDecrease(self.info) })
                            
                            Spacer()
                            
                            MedicoButton(localizedStringKey: "add_to_cart",
                                         width: 120,
                                         height: 32,
                                         fontSize: 14,
                                         fontWeight: .bold) {
                                onSellerInfoSelect(self.info)
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.vertical, 8)
                    }
                    
                    info.stockInfo.statusColor.color
                        .cornerRadius(5, corners: [.topLeft, .bottomLeft])
                        .frame(width: 5)
                }
                .background(AppColor.white.color.cornerRadius(5))
            }
            
            private func getDetailView(withTitleLocalizationKey titleLocalizationKey: String,
                                       withBody body: String,
                                       withBodyColor bodyColor: AppColor = .lightBlue) -> some View {
                return AnyView(
                    HStack(spacing: 4) {
                        LocalizedText(localizationKey: titleLocalizationKey,
                                      color: .grey3,
                                      multilineTextAlignment: .leading)
                        
                        Text(body)
                            .medicoText(textWeight: .bold,
                                        color: bodyColor,
                                        multilineTextAlignment: .leading)
                    }
                )
            }
        }
    }
}
