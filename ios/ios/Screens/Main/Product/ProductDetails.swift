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
    let scope: MainScope.ProductInfo
    
    var body: some View {
        VStack(spacing: 32) {
            self.infoView
            
            DetailsView(isOpened: scope.isDetailsOpened,
                        manufacturer: scope.product.manufacturer.name,
                        compositions: scope.compositionsString,
                        storage: "storage_description") {
                scope.toggleDetails()
            }
        }
        .padding(.vertical, 33)
        .padding(.horizontal, 16)
        .scrollView()
    }
    
    private var infoView: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(spacing: 16) {
                ProductImage(medicineId: scope.product.medicineId,
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
                    
                    Spacer()
                    
                    Text(scope.product.formattedPrice)
                        .medicoText(textWeight: .bold,
                                    fontSize: 20,
                                    multilineTextAlignment: .leading)
                }
            }
            
            VStack(alignment: .leading, spacing: 4) {
                LocalizedText(localizedStringKey: LocalizedStringKey("mrp \(String(format: "%.2f", scope.product.mrp))"),
                              testingIdentifier: "mrp",
                              color: .grey3,
                              multilineTextAlignment: .leading)
                
                LocalizedText(localizedStringKey: LocalizedStringKey("ptr \(scope.product.ptr)"),
                              testingIdentifier: "ptr",
                              color: .grey3,
                              multilineTextAlignment: .leading)
                
                LocalizedText(localizedStringKey: LocalizedStringKey("description \(scope.product.unitOfMeasureData.name)"),
                              testingIdentifier: "description",
                              fontSize: 16,
                              color: .lightBlue,
                              multilineTextAlignment: .leading)
            }
            
            MedicoButton(localizedStringKey: "add_to_cart") {
                scope.addToCart()
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
