//
//  HostingController.swift
//  ios
//
//  Created by Arnis on 17.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI

class HostingController<ContentView>: UIHostingController<ContentView> where ContentView : View {
    override var preferredStatusBarStyle: UIStatusBarStyle {
        return .lightContent
    }
}
