//
//  TransparentList.swift
//  Medico
//
//  Created by Dasha Gurinovich on 21.01.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct TransparentList<Content: View, T: Hashable>: View {
    @EnvironmentObject var scrollData: ListScrollData
    
    let listName: ListScrollData.Name?
    
    let data: SwiftDataSource<NSArray>
    let dataType: T.Type
    
    @ObservedObject var isInProgress: SwiftDataSource<KotlinBoolean>
    let pagination: Pagination
    
    let elementsSpacing: CGFloat
    
    let onTapGesture: (T) -> ()
    let loadItems: () -> ()
    let getCellView: (Int, T) -> Content
    
    var body: some View {
        guard let data = self.data.value as? [T] else { return AnyView(EmptyView()) }
        
        let needsPadding: Bool
        if #available(iOS 15.0, *) {
            needsPadding = false
        }
        else {
            needsPadding = true
        }
        
        return AnyView(
            List {
                ForEach(Array(data.enumerated()), id: \.offset) { index, element in
                    self.getCellView(index, element)
                        .padding(.bottom, elementsSpacing)
                        .listRowInsets(.init())
                        .listRowBackground(Color.blue)
                        .background(appColor: .primary)
                        .onAppear {
                            if index == data.count - 1 &&
                                self.pagination.canLoadMore() &&
                                self.isInProgress.value == false {
                                self.loadItems()

                                if let listName = self.listName {
                                    self.scrollData.lists[listName]?.elementToScrollTo = index
                                }
                            }
                        }
                        .onTapGesture {
                            if let listName = self.listName {
                                self.scrollData.lists[listName]?.elementToScrollTo = index
                            }
                            
                            self.onTapGesture(element)
                        }
                }
            }
            .onAppear {
                UITableView.appearance().showsVerticalScrollIndicator = false
                
                UITableView.appearance().backgroundColor = UIColor.clear
                UITableViewCell.appearance().backgroundColor = UIColor.clear
                
                UITableView.appearance().contentInset.top = !needsPadding ? -35 : 0
            }
            .introspectTableView { (tableView) in
                if let listName = self.listName,
                   self.scrollData.lists[listName]?.listTableView == nil {
                    self.scrollData.lists[listName]?.listTableView = tableView
                }
                
                if self.isInProgress.value == true { return }
                
                setUpInitialScrollData()
                
                if let listName = self.listName,
                   let elementToScrollTo = self.scrollData.lists[listName]?.elementToScrollTo {
                    scrollToCell(elementToScrollTo, animated: true)
                }
            }
            .onReceive(self.data.$value) { value in
                guard let listName = self.listName,
                      let value = value, value.count > 0 else { return }

                self.scrollData.lists[listName]?.listTableView = nil
            }
            .padding(.horizontal, !needsPadding ? -16 : 0)
        )
    }
    
    init(data: SwiftDataSource<NSArray>,
         dataType: T.Type,
         listName: ListScrollData.Name?,
         isInProgress: DataSource<KotlinBoolean>? = nil,
         pagination: Pagination,
         elementsSpacing: CGFloat = 16,
         onTapGesture: @escaping (T) -> (),
         loadItems: @escaping  () -> (),
         getCellView: @escaping (Int, T) -> Content) {
        self.listName = listName
        
        self.data = data
        self.dataType = dataType
        
        self.isInProgress = SwiftDataSource(dataSource: isInProgress ?? DataSource(initialValue: false))
        self.pagination = pagination
        
        self.elementsSpacing = elementsSpacing
        
        self.onTapGesture = onTapGesture
        self.loadItems = loadItems
        self.getCellView = getCellView
    }
    
    private func setUpInitialScrollData() {
        guard let listName = self.listName,
              !self.scrollData.lists.contains(where: { $0.key == listName }) else { return }
        
        self.scrollData.lists[listName] = ListScrollData.Data()
    }
    
    private func scrollToCell(_ index: Int, animated: Bool = false) {
        let indexPath = IndexPath(row: index, section: 0)
        
        guard let listName = self.listName,
              let listTableView = self.scrollData.lists[listName]?.listTableView,
              listTableView.dataSource != nil,
              listTableView.hasRowAtIndexPath(indexPath) else { return }
        
        listTableView.scrollToRow(at: indexPath,
                                  at: .middle,
                                  animated: animated)
        
        self.scrollData.lists[listName]?.elementToScrollTo = nil
    }
}

class ListScrollData: ObservableObject {
    var lists = [Name: Data]()
    
    class Data {
        var listTableView: UITableView?
        var elementToScrollTo: Int?
    }
    
    func clear(list: Name) {
        _ = self.lists.removeValue(forKey: list)
    }
    
    enum Name {
        case globalSearchProducts
        case storeProducts
        
        case notifications
        case stores
        case instoreOrders
        case instoreProducts
        case allStockists
        case yourStockists
        case yourRetailers
        case yourHospitals
        case yourSeasonBoys
        
        case orders
        case invoices
    }
}
