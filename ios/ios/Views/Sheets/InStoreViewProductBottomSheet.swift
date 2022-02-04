//
//  InstoreAddProductBottomSheet.swift
//  Medico
//
//  Created by user on 04/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

/*val type: String,
 val code: String,
 val buy: FormattedData<Double>,
 val free: FormattedData<Double>,
 val productDiscount: FormattedData<Double>,
 val displayLabel: String,
 val offerPrice: FormattedData<Double>,
 val validity: String?,
 */

struct InStoreViewProductBottomSheet: ViewModifier {
    
    let bottomSheet: BottomSheet.InStoreViewProduct
    let onBottomSheetDismiss: () -> ()
    
    var product: DataInStoreProduct {
        return bottomSheet.product
    }
    
    func body(content: Content) -> some View {
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
        
        let product = bottomSheet.product
        
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 430) {
                
                VStack(alignment: .leading, spacing: 15) {
                    
                    inStoreProductDetailTopView
                    
                    InStoreAddToCartButtonView {
                        print("Click on Add to cart")
                    }
                    
                    if let promotion = product.promotionData, product.isPromotionActive {
                        InStoreProductOfferView(promotion:promotion, price: product.priceInfo.price.formattedPrice)
                    } else {
                        InStoreNoOfferView()
                    }
                 
                    inStoreProductDetailBottomView
                }
                .padding(15)
                .scrollView()
            }
            .edgesIgnoringSafeArea(.bottom)
        )
    }
    
    init(bottomSheet: BottomSheet.InStoreViewProduct,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
    }
    
    var inStoreProductDetailTopView: some View {
        
        HStack(spacing: 10) {
            
            ProductImage(medicineId: product.id,
                         size: .px123)
                .frame(width: 60.0, height: 60.0)
            
            VStack(alignment: .leading, spacing: 5) {
                
                Text(product.name)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 16,
                                multilineTextAlignment: .leading)
                    .lineLimit(2)
                
                HStack {
                    
                    Text(product.code)
                        .medicoText(textWeight: .regular,
                                    fontSize: 13,
                                    color: .black)
                    
                    AppColor.grey1.color
                        .opacity(0.5)
                        .frame(width: 1, height: 20)
                    
                    HStack(spacing: 3) {
                        
                        LocalizedText(localizationKey: "units:",
                                      textWeight: .regular,
                                      fontSize: 13)
                        
                        Text(product.standardUnit)
                            .medicoText(textWeight: .bold,
                                        fontSize: 14,
                                        color: .blue)
                    }
                    
                }
            }
            
            Spacer()
        }
    }
    
    
    private struct InStoreProductOfferView: View {
        
        let promotion: DataInStorePromotionData
        let price: String
        
        var body: some View {
            
            
            VStack(alignment: .leading, spacing: 0) {
                
                GeometryReader { geometry in
                    AppColor.red.color
                        .opacity(0)
                        .frame(width: 8, height: geometry.size.height)
                }
                
                VStack(alignment: .leading, spacing: 12) {
                    
                    Text("\(promotion.displayLabel)")
                        .medicoText(textWeight: .bold,
                                    fontSize: 16,
                                    color: .red)
                        .lineLimit(1)
                    
                    HStack {
                        Text("\(promotion.offerPrice.formatted)")
                            .medicoText(textWeight: .bold,
                                        fontSize: 16,
                                        color: .darkBlue)
                            .lineLimit(1)
                        Text("\(price)")
                            .strikethrough()
                            .medicoText(textWeight: .medium,
                                        fontSize: 12,
                                        color: .grey3)
                            .lineLimit(1)
                            
                    }
                    
                    let offerValidity = "(" + "\(String(describing: promotion.validity))" + ")"
                    Text(offerValidity)
                        .medicoText(textWeight: .regular,
                                    fontSize: 10,
                                    color: .grey3)
                        .lineLimit(1)
                }
                .padding(12)
            }
            .strokeBorder(.red,
                          borderOpacity: 0.5,
                          fill: .white,
                          cornerRadius: 8)
        }
    }
    
    private struct InStoreNoOfferView: View {
        
        var body: some View {
            LocalizedText(localizationKey: "no_available_offer", textWeight: .bold, fontSize: 12, color: .white)
             .frame(height: 40)
             .frame(maxWidth: .infinity)
             .background(appColor: .red)
             .cornerRadius(3)
        }
    }
    
    private struct InStoreAddToCartButtonView: View {
        
        let onSelect: ()->Void
        var body: some View {
            GeometryReader { geometry in
            
                MedicoButton(localizedStringKey: "add_to_cart",
                             isEnabled: true,
                             width: geometry.size.width,
                             height: 40,
                             cornerRadius: 24,
                             fontSize: 15,
                             fontWeight: .bold,
                             fontColor: .darkBlue) {
                   //self.onSelectItem()
                }
                .buttonStyle(BorderlessButtonStyle())
            }
            .frame(height: 40)
        }
    }
    
    var inStoreProductDetailBottomView:some View {
        
        VStack(alignment: .leading, spacing: 0) {
            
            VStack(alignment: .leading, spacing: 12) {
                
                HStack(spacing: 2) {
                    Text(product.priceInfo.price.formattedPrice)
                        .medicoText(textWeight: .semiBold,
                                    fontSize: 16,
                                    color: .darkBlue)
                        .lineLimit(1)
                    Spacer()
                    InLineInfoView(localizationKey: "stocks:",
                                   value: "\(product.stockInfo.availableQty)")
                }
                
                HStack {
                    ExpiryView(date: product.stockInfo.expiry.formattedDate,
                               color: .hex(product.stockInfo.expiry.color))
                    Spacer()
                }
                
                HStack {
                    InLineInfoView(localizationKey: "mrp:", value: product.priceInfo.mrp.formattedPrice)
                    Spacer()
                    InLineInfoView(localizationKey: "margin:", value: product.priceInfo.marginPercent)
                }
                
                InLineInfoView(localizationKey: "batch_no:", value: "N/A")
                
            }
            .padding(12)
        }
        .strokeBorder(.darkBlue,
                      borderOpacity: 0.12,
                      fill: .white,
                      cornerRadius: 5)
    }
    
    private struct InLineInfoView: View {
        
        let localizationKey: String
        let value: String
        var body: some View {
            HStack(spacing: 3) {
                LocalizedText(localizationKey: localizationKey,
                              textWeight: .regular,
                              fontSize: 13)
                Text(value)
                    .medicoText(textWeight: .semiBold,
                                fontSize: 13,
                                color: .grey3)
            }
        }
    }
    
    private struct ExpiryView: View {
        let date: String
        let color: AppColor
        var body: some View {
            HStack {
                HStack(spacing: 2) {
                    LocalizedText(localizationKey: "expiry:", textWeight: .regular, fontSize: 12, color: .grey1)
                    Text(date)
                        .medicoText(textWeight: .bold, fontSize: 12, color: color)
                        .autocapitalization(.words)
                }.padding(6)
            }
            .background(RoundedRectangle(cornerRadius: 6)
                            .fill(appColor: color)
                            .opacity(0.3))
        }
    }
}

