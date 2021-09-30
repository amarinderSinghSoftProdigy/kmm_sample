//
//  SwiftStore.swift
//  iosApp
//
//  Created by Arnis on 10.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import core

class SwiftDataSource<T: AnyObject> : ObservableObject {
    private var dataSource: BaseDataSource<T>
    
    var updateCount: Int32 {
        (dataSource as? DataSource<T>)?.updateCount ?? -1
    }
    
    @Published private(set) var value: T? {
        didSet {
            onValueDidSet?(value)
        }
    }
    
    var onValueDidSet: ((T?) -> ())?
    
    init(dataSource: BaseDataSource<T>) {
        self.dataSource = dataSource
        
        self.dataSource.observeOnUi { newValue in
            self.value = newValue
        }
    }
}
