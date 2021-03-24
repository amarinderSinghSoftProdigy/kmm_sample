//
//  ProductDetails.swift
//  Medico
//
//  Created by Dasha Gurinovich on 6.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct ProductDetails: View {
    let scope: ProductInfoScope
    
    var body: some View {
        VStack(spacing: 32) {
            self.infoView
            
            DetailsView(isOpened: scope.isDetailsOpened,
                        manufacturer: scope.product.manufacturer,
                        compositions: scope.compositionsString,
                        storage: "storage_description") {
                scope.toggleDetails()
            }
            
            AlternativeBrandsView(alternativeBrands: scope.alternativeBrands) {
                scope.selectAlternativeProduct(product: $0)
            }
        }
        .padding(.vertical, 33)
        .padding(.horizontal, 16)
        .scrollView()
        .screenLogger(withScreenName: "ProductDetails",
                      withScreenClass: ProductDetails.self)
    }
    
    private var infoView: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .top, spacing: 16) {
                ProductImage(medicineId: scope.product.code,
                             size: .px123)
                    .frame(width: 123, height: 123)
                
                VStack(alignment: .leading, spacing: 6) {
                    Text(scope.product.name)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                    
                    LocalizedText(localizedStringKey: LocalizedStringKey("code \(scope.product.code)"),
                                  testingIdentifier: "code",
                                  color: .grey3,
                                  multilineTextAlignment: .leading)
                    
                    if let price = scope.product.formattedPrice {
                        Text(price)
                            .medicoText(textWeight: .bold,
                                        fontSize: 20,
                                        multilineTextAlignment: .leading)
                    }
                }
                .fixedSize(horizontal: false, vertical: true)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                LocalizedText(localizedStringKey: LocalizedStringKey("mrp \(scope.product.formattedMrp)"),
                              testingIdentifier: "mrp",
                              color: .grey3,
                              multilineTextAlignment: .leading)
                
                if let margin = scope.product.marginPercent {
                    LocalizedText(localizedStringKey: LocalizedStringKey("margin \(margin)"),
                                  testingIdentifier: "margin",
                                  color: .grey3,
                                  multilineTextAlignment: .leading)
                }
                
                HStack(alignment: .top, spacing: 10) {
                    LocalizedText(localizedStringKey: LocalizedStringKey("description \(scope.product.uomName)"),
                                  testingIdentifier: "description",
                                  fontSize: 16,
                                  color: .lightBlue,
                                  multilineTextAlignment: .leading)
                    
                    Spacer()
                    
                    if let stockInfo = self.scope.product.stockInfo {
                        LocalizedText(localizationKey: stockInfo.formattedStatus,
                                      textWeight: .bold,
                                      fontSize: 12,
                                      color: stockInfo.statusColor,
                                      multilineTextAlignment: .leading)
                            .padding(.top, 2)
                    }
                }
            }
            
            switch self.scope.product.buyingOption {
            case .buy:
                MedicoButton(localizedStringKey: "add_to_cart") {
                    scope.addToCart()
                }
            case .quote:
                MedicoButton(localizedStringKey: "get_quote",
                             buttonColor: .clear) {
                    
                }
                .background(RoundedRectangle(cornerRadius: 8)
                                .stroke(AppColor.yellow.color, lineWidth: 2))
            default:
                EmptyView()
            }
        }
    }
    
    private struct DetailsView: View {
        @ObservedObject var isOpened: SwiftDataSource<KotlinBoolean>
        
        let manufacturer: String
        let compositions: String
        let storage: String
        
        let onToggle: () -> ()
        
        var body: some View {
            let isOpened = self.isOpened.value == true
            
            return AnyView(
                VStack(alignment: .leading, spacing: 16) {
                    HStack {
                        LocalizedText(localizationKey: "details",
                                      textWeight: .bold,
                                      fontSize: 16,
                                      multilineTextAlignment: .leading)
                        
                        Spacer()
                        
                        Button(action: {
                            self.onToggle()
                        }) {
                            Image(systemName: "chevron.right")
                                .foregroundColor(appColor: .grey3)
                                .rotationEffect(.degrees(isOpened ? 90 : 0))
                        }
                    }
                    
                    if isOpened {
                        getDetailView(withTitleLocalizationKey: "manufacturer",
                                      withBody: manufacturer)
                        
                        getDetailView(withTitleLocalizationKey: "compositions",
                                      withBody: compositions)
                    
                        getDetailView(withTitleLocalizationKey: "storage",
                                      withBody: storage)
                            .transition(AnyTransition.move(edge: .top).combined(with: .opacity))
                    }
                }
                .animation(.linear(duration: 0.2))
            )
        }
        
        init(isOpened: DataSource<KotlinBoolean>,
             manufacturer: String,
             compositions: String,
             storage: String,
             onToggle: @escaping () -> ()) {
            self.isOpened = SwiftDataSource(dataSource: isOpened)
            
            self.manufacturer = manufacturer
            self.compositions = compositions
            self.storage = storage
            
            self.onToggle = onToggle
        }
        
        private func getDetailView(withTitleLocalizationKey titleLocalizationKey: String,
                                   withBody body: String) -> some View {
            return AnyView(
                VStack(alignment: .leading, spacing: 6) {
                    LocalizedText(localizationKey: titleLocalizationKey,
                                  textWeight: .semiBold,
                                  fontSize: 15,
                                  multilineTextAlignment: .leading)
                    
                    LocalizedText(localizationKey: body,
                                  multilineTextAlignment: .leading)
                }
            )
        }
    }
    
    private struct AlternativeBrandsView: View {
        let alternativeBrands: [DataAlternateProductData]
        let onBrandTap: (DataAlternateProductData) -> ()
        
        var body: some View {
            guard !alternativeBrands.isEmpty else { return AnyView(EmptyView()) }
            
            return AnyView(
                VStack(alignment: .leading, spacing: 15) {
                    LocalizedText(localizationKey: "alternative_brands",
                                  textWeight: .medium,
                                  fontSize: 16,
                                  multilineTextAlignment: .leading)
                    
                    VStack(spacing: 10) {
                        ForEach(alternativeBrands, id: \.self) { alternativeBrand in
                            AlternativeBrandView(alternativeBrand: alternativeBrand)
                                .onTapGesture {
                                    onBrandTap(alternativeBrand)
                                }
                        }
                    }
                })
        }
        
        private struct AlternativeBrandView: View {
            let alternativeBrand: DataAlternateProductData
            
            var body: some View {
                VStack(alignment: .leading, spacing: 20) {
                    VStack(alignment: .leading, spacing: 7) {
                        HStack(alignment: .top, spacing: 10) {
                            Text(alternativeBrand.name)
                                .medicoText(textWeight: .semiBold,
                                            fontSize: 16,
                                            multilineTextAlignment: .leading)
                            
                            Spacer()
                            
                            LocalizedText(localizationKey: alternativeBrand.availableVariants,
                                          fontSize: 12,
                                          color: .grey3,
                                          multilineTextAlignment: .leading)
                        }
                        
                        Text(alternativeBrand.manufacturerName)
                            .medicoText(fontSize: 16,
                                        multilineTextAlignment: .leading)
                    }
                    
                    Text(alternativeBrand.priceRange)
                        .medicoText(textWeight: .bold,
                                    color: .lightBlue,
                                    multilineTextAlignment: .leading)
                    
                }
                .padding(12)
                .background(AppColor.white.color.cornerRadius(5))
            }
        }
    }
}


struct ProductImage: View {
    private let urlProvider = CdnUrlProvider()
    
    let medicineId: String
    let size: CdnUrlProvider.Size
    
    var body: some View {
        URLImage(withURL: urlProvider.urlFor(medicineId: medicineId,
                                             size: size),
                 withDefaultImageName: "DefaultProduct")
    }
}
