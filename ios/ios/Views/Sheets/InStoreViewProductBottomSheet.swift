//
//  InstoreAddProductBottomSheet.swift
//  Medico
//
//  Created by user on 04/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct InStoreViewProductBottomSheet: ViewModifier {
    
    let bottomSheet: BottomSheet.InStoreViewProduct
    let onBottomSheetDismiss: () -> ()
    
    @State private var quantity: Double
    @State private var freeQuantity: Double
    
    var product: DataInStoreProduct {
        return bottomSheet.product
    }
    
    func body(content: Content) -> some View {
        
        let bottomSheetOpened = Binding(get: { true },
                                        set: { newValue in if !newValue { onBottomSheetDismiss() } })
                
        return AnyView(
            BaseBottomSheetView(isOpened: bottomSheetOpened,
                                maxHeight: 420) {
                
                VStack(alignment: .leading, spacing: 15) {
                    EmptyView()
                        .modifier(BaseSellerView(initialMode: nil,
                                                 header: inStoreProductDetailTopView,
                                                 horizontalPadding: 0,
                                                 verticalPadding: 0,
                                                 showsDivider: false,
                                                 isReadonly: false,
                                                 isQuoted: false,
                                                 initialQuantity: quantity,
                                                 initialFreeQuantity: freeQuantity,
                                                 maxQuantity: .infinity,
                                                 onQuantitySelect: {
                                                    updateItemQuantity(quantity: $0, freeQuantity: $1)
                                                 }))
                    
                    if let promotion = product.promotionData, product.isPromotionActive {
                        InStoreProductOfferView(promotion:promotion, price: product.priceInfo.price.formattedPrice)
                    } else {
                        InStoreNoOfferView()
                    }
                    
                    inStoreProductDetailBottomView
                }
                .padding(.horizontal, 20)
                .padding(.vertical, 10)
                .scrollView()
            }
            .textFieldsModifiers()
            .edgesIgnoringSafeArea(.bottom)
            
        )
    }
    
    private func updateItemQuantity(quantity: Double?,
                                    freeQuantity: Double?) {
        guard let quantity = quantity,
              let freeQuantity = freeQuantity else { return }
        self.bottomSheet.addToCart(quantity: quantity, freeQuantity: freeQuantity)
        self.quantity = quantity
        self.freeQuantity = freeQuantity
    }
    
    init(bottomSheet: BottomSheet.InStoreViewProduct,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        self.onBottomSheetDismiss = onBottomSheetDismiss
        self._quantity = State(initialValue: Double(truncating: bottomSheet.product.order?.quantity.value ?? 0))
        self._freeQuantity = State(initialValue:Double(truncating: bottomSheet.product.order?.freeQty.value ?? 0))
    }
    
    var inStoreProductDetailTopView: some View {
        
        return HStack(spacing: 10) {
            
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
            
            HStack {
                VStack(alignment: .leading, spacing: 8) {
                    
                    Text(promotion.displayLabel)
                        .medicoText(textWeight: .bold,
                                    fontSize: 16,
                                    color: AppColor.red)
                    
                    HStack(spacing: 8) {
                        Text(promotion.offerPrice.formatted)
                            .medicoText(textWeight: .bold,
                                        fontSize: 16)
                        Text(promotion.offerPrice.formatted)
                            .strikethrough(true, color: AppColor.textGrey.color)
                            .medicoText(textWeight: .medium,
                                        fontSize: 12,
                                        color: AppColor.textGrey)
                        
                    }
                    if let validity = promotion.validity, !validity.isEmpty {
                        Text(validity)
                            .medicoText(textWeight: .regular,
                                        fontSize: 8,
                                        color: AppColor.textGrey)
                    }
                }
                Spacer()
            }
            .padding(.vertical, 12)
            .padding(.horizontal, 16)
            .background(
                RoundedRectangle(cornerRadius: 4)
                    .stroke(AppColor.red.color, lineWidth: 1)
            )
        }
    }
    
    private struct InStoreNoOfferView: View {
        
        var body: some View {
            HStack(alignment: .center, spacing: 10) {
                Spacer()
                Image("NoAvailableOffer")
                    .fixedSize()
                    .frame(width: 25, height: 25)
                LocalizedText(localizationKey: "no_available_offer", textWeight: .bold, fontSize: 12, color: .white)
                    .frame(height: 40)
                Spacer()
            }
            .background(appColor: .red)
            .cornerRadius(3)
            .frame(maxWidth: .infinity)
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
