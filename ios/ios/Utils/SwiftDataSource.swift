//
//  SwiftStore.swift
//  iosApp
//
//  Created by Arnis on 10.09.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import core

class SwiftDataSource<T: AnyObject> : ObservableObject {
    private var dataSource: DataSource<T>
    
    @Published private(set) var value: T?
    
    init(dataSource: DataSource<T>) {
        self.dataSource = dataSource
        self.dataSource.observeOnUi { newValue in
            self.value = newValue
        }
    }
}
