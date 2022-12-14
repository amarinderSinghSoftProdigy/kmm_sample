//
//  InStoreCart.swift
//  Medico
//
//  Created by user on 07/02/22.
//  Copyright © 2022 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

//MARK: Main Screen
struct InStoreCartScreen: View {
    
    let scope: InStoreCartScope
    
    @ObservedObject var items: SwiftDataSource<NSArray>
    @ObservedObject var total: SwiftDataSource<DataTotal>
    
    var needsPadding: Bool {
        if #available(iOS 15.0, *) {
            return false
        }
        else {
            return true
        }
    }

    var body: some View {
        
        VStack(spacing: 0) {
            
            VStack(spacing: 0) {
                
                HStack(alignment: .center) {
                    
                    InStoreCartNumberView(items: items)
                    
                    Button(action: {
                        scope.clearCart()
                    }, label: {
                        Image("RemoveCart")
                            .fixedSize()
                            .frame(width: 35, height: 35, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    })
                    .frame(width: 50, height: 50)
                    .background(appColor: .red)
                    
                }
                .background(appColor: .white)
                
                AppColor.lightGrey.color
                    .frame(height: 1)
            }
            .padding(.vertical, 20)
            
            VStack {
                
                if (self.items.value?.count ?? 0) > 0 {
                    List {
                        VStack(spacing: 8) {
                            ForEach(self.items.value as? [DataInStoreCartEntry] ?? [], id: \.self) { item in
                                InstoreCartItemView(item: item,
                                                    onQuantitySelect: { onQuantitySelect(item: item, quantity: $0, freeQuantity: $1) },
                                                    onRemoveItem: { scope.removeItem(item: $0) })
                            }
                        }
                        .buttonStyle(PlainButtonStyle())
                        .listRowInsets(.init())
                        .background(appColor: .primary)
                        .modifier(TableViewSeparatorModifier())
                    }
                    .onAppear {
                        UITableView.appearance().separatorStyle = .none
                    }
                    .padding(.horizontal, !needsPadding ? -16 : 0)
                    .background(appColor: .clear)
                    
                    Spacer()
                }                
                
                VStack(spacing: 12) {
                    
                    AppColor.black.color
                        .opacity(0.27)
                        .frame(height: 1)
                    
                    HStack(spacing: 6) {
                        
                        HStack(spacing: 4) {
                            
                            LocalizedText(localizationKey: "total:",
                                          textWeight: .medium,
                                          fontSize: 20)
                            
                            Text(total.value?.formattedPrice ?? "")
                                .medicoText(textWeight: .bold,
                                            fontSize: 20)
                        }
                        
                        Spacer()
                        
                        MedicoButton(localizedStringKey: "complete_order",
                                     isEnabled: true,
                                     width: 170,
                                     height: 48,
                                     cornerRadius: 24,
                                     fontSize: 15,
                                     fontWeight: .bold) {
                            scope.continueWithCart()
                        }
                    }
                    .padding(.horizontal, 17)
                }
                .padding(.bottom, 20)
            }
        }
    }
    
    init(scope: InStoreCartScope) {
        self.scope = scope
        self.items = SwiftDataSource(dataSource: scope.items)
        self.total = SwiftDataSource(dataSource: scope.total)
    }
    
    func onQuantitySelect(item: DataInStoreCartEntry,
                          quantity: Double?,
                          freeQuantity: Double?) {
        guard let quantity = quantity,
              let freeQuantity = freeQuantity else { return }
        scope.updateItemCount(item: item, quantity: quantity, freeQuantity: freeQuantity)
    }
}

//MARK: InStore Cart Number View
struct InStoreCartNumberView: View {
    
    @ObservedObject var items: SwiftDataSource<NSArray>

    var body: some View {
        HStack {
            Spacer()
            numberView(title: "items", value: "\(items.value?.count ?? 0)")
            Spacer()
            let qty = items.value?.reduce(0.0, { sum, element in
                if let entry = element as? DataInStoreCartEntry  {
                    return  (sum ?? 0) + Double(truncating: entry.quantity.value ?? 0)
                }
                return sum
            })
            numberView(title: "qty", value: "\(qty ?? 0)")
            Spacer()
            let freeQty = items.value?.reduce(0.0, { sum, element in
                if let entry = element as? DataInStoreCartEntry  {
                    return  (sum ?? 0) + Double(truncating: entry.freeQty.value ?? 0)
                }
                return sum
            })
            numberView(title: "free", value: "\(freeQty ?? 0)")
            Spacer()
        }
    }
    
    init(items: SwiftDataSource<NSArray>) {
        self.items = items
    }
    
    func numberView(title: String, value: String) -> some View {
        VStack(alignment: .center, spacing: 5) {
            LocalizedText(localizationKey: title, textWeight: .regular, fontSize: 12, color: .darkBlue)
            Text(value).medicoText(textWeight: .semiBold, fontSize: 12, color: .darkBlue)
        }
    }
}

//MARK: InStore Cart Item View
struct InstoreCartItemView: View {
    
    let item: DataInStoreCartEntry
    let onQuantitySelect: ((Double?, Double?) -> Void)?
    let onRemoveItem: ((DataInStoreCartEntry) -> ())?

    var body: some View {
        
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                getDetailView(titleLocalizationKey: "ptr:", body: item.price.formatted)
                Spacer()
                getDetailView(titleLocalizationKey: "tot:", body: item.totalPrice.formatted)
            }
            .padding(.top, 2)
        }
        .fixedSize(horizontal: false, vertical: true)
        .modifier(BaseSellerView(initialMode: nil,
                                 header: header,
                                 isReadonly: false,
                                 isQuoted: false,
                                 initialQuantity: Double(truncating: item.quantity.value ?? 0),
                                 initialFreeQuantity: Double(truncating: item.freeQty.value ?? 0),
                                 maxQuantity: .infinity,
                                 onQuantitySelect: { onQuantitySelect?($0, $1) }))
        .padding(.bottom, 8)
    }
    
    init(item: DataInStoreCartEntry,
         onQuantitySelect: ((Double?, Double?) -> Void)? = nil,
         onRemoveItem: ((DataInStoreCartEntry) -> ())? = nil) {
        self.item = item
        self.onQuantitySelect = onQuantitySelect
        self.onRemoveItem = onRemoveItem
    }
    
    private var header: some View {
        HStack(spacing: 8) {
            
            Text(item.productName)
                .medicoText(textWeight: .bold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
                .lineLimit(1)
            
            AppColor.darkBlue.color
                .opacity(0.33)
                .frame(width: 1)
                .padding(.vertical, 4)
            
            Text("(\(item.manufacturerCode))")
                .medicoText(textWeight: .semiBold,
                            fontSize: 16,
                            multilineTextAlignment: .leading)
                .opacity(0.8)
                .lineLimit(1)

            Spacer()

            if let onRemoveItem = self.onRemoveItem {
                RemoveButton(onTapRemove: { onRemoveItem(item) })
            }
        }
    }
    
    private func getDetailView(titleLocalizationKey: String,
                               body: String) -> some View {
        HStack(spacing: 4) {
            LocalizedText(localizationKey: titleLocalizationKey,
                          textWeight: .bold,
                          fontSize: 16,
                          color: .greyBlue)
            
            Text(body)
                .medicoText(textWeight: .semiBold,
                            fontSize: 16)
        }
    }
    
    private struct RemoveButton: View {
        
        let onTapRemove: () -> Void
        
        var body: some View {
            Button(action: onTapRemove) {
                Image(systemName: "xmark")
                    .resizable()
                    .frame(width: 11, height: 11)
                    .font(Font.system(size: 14, weight: .medium))
                    .foregroundColor(.red)
                    .padding(7)
                    .background(
                        Circle()
                            .foregroundColor(appColor: .lightPink)
                    )
            }
        }
    }

}

