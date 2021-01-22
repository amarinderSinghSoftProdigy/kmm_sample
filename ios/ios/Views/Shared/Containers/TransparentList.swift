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
    
    let listName: ListScrollData.Name
    
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
        
        return AnyView(
            List {
                ForEach(Array(data.enumerated()), id: \.element) { index, element in
                    self.getCellView(index, element)
                        .padding(.bottom, elementsSpacing)
                        .listRowInsets(.init())
                        .listRowBackground(Color.clear)
                        .background(appColor: .primary)
                        .onAppear {
                            if index == data.count - 1 &&
                                self.pagination.canLoadMore() &&
                                self.isInProgress.value == false {
                                self.loadItems()

                                self.scrollData.lists[listName]?.elementToScrollTo = index
                            }
                        }
                        .onTapGesture {
                            self.scrollData.lists[listName]?.elementToScrollTo = index
                            
                            self.onTapGesture(element)
                        }
                }
            }
            .onAppear {
                UITableView.appearance().showsVerticalScrollIndicator = false
                
                UITableView.appearance().backgroundColor = UIColor.clear
                UITableViewCell.appearance().backgroundColor = UIColor.clear
            }
            .introspectTableView { (tableView) in
                if self.scrollData.lists[listName]?.listTableView == nil {
                    self.scrollData.lists[listName]?.listTableView = tableView
                }
                
                if self.isInProgress.value == true { return }
                
                setUpInitialScrollData()
                
                if let elementToScrollTo = self.scrollData.lists[listName]?.elementToScrollTo {
                    scrollToCell(elementToScrollTo, animated: true)
                }
            }
            .onReceive(self.data.$value) { value in
                guard let value = value, value.count > 0 else { return }

                self.scrollData.lists[listName]?.listTableView = nil
            }
        )
    }
    
    init(data: SwiftDataSource<NSArray>,
         dataType: T.Type,
         listName: ListScrollData.Name,
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
        guard !self.scrollData.lists.contains(where: { $0.key == self.listName }) else { return }
        
        self.scrollData.lists[self.listName] = ListScrollData.Data()
    }
    
    private func scrollToCell(_ index: Int, animated: Bool = false) {
        let indexPath = IndexPath(row: index, section: 0)
        
        guard let listTableView = self.scrollData.lists[listName]?.listTableView,
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
        
        case allStockists
        case yourStockists
    }
}
