//
//  SwiftStore.swift
//  iosApp
//
//  Created by Arnis on 10.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import core

class SwiftDatasource<T: AnyObject> : ObservableObject {
    private var dataSource: DataSource<T>
    
    @Published private (set) var value: T?
    
    init(dataSource: DataSource<T>) {
        value = dataSource.value
        self.dataSource = dataSource
        self.dataSource.observeOnUi { newValue in
            self.value = newValue
        }
    }
}

class SwiftListDatasource<T: AnyObject> : ObservableObject {
    private var dataSource: DataSource<NSArray>
    
    @Published private (set) var value: [T]
    
    init(dataSource: DataSource<NSArray>) {
        value = dataSource.value as! [T]
        self.dataSource = dataSource
        self.dataSource.observeOnUi { newValue in
            self.value = newValue as! [T]
        }
    }
}

class SwiftCompletable<T: AnyObject>: ObservableObject{
    @Published private (set) var value: T?
    
    init(initial: T?, deferred: Kotlinx_coroutines_coreDeferred) {
        value = initial
        deferred.await { newValue, err in
            self.value = newValue as? T
        }
    }
}
