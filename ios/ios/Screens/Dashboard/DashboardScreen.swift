//
//  DashboardScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 17.02.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct DashboardScreen: View {
    let scope: DashboardScope
    
    var body: some View {
        VStack(spacing: 16) {
            HStack(spacing: 17) {
                BigButton(imageName: "Bell",
                          textKey: "notifications",
                          counter: scope.unreadNotifications)
                    .onTapGesture {
                        scope.goToNotifications()
                    }
            }
        }
        .padding(.vertical, 32)
        .padding(.horizontal, 16)
    }
    
    private struct BigButton: View {
        let imageName: String
        let textKey: String
        
        @ObservedObject var counter: SwiftDataSource<KotlinInt>
        
        var body: some View {
            VStack(spacing: 12) {
                ZStack(alignment: .topTrailing) {
                    Image(imageName)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 30, height: 30)
                        .padding(.top, 10)
                        .padding(.horizontal, 18)
                    
                    if let counter = self.counter.value as? Int,
                       counter > 0 {
                        ZStack {
                            Circle()
                                .fill(appColor: .red)
                                .frame(width: 30, height: 30)

                            Text(String(counter))
                                .medicoText(textWeight: .bold,
                                            fontSize: 16,
                                            color: .white)
                        }
                    }
                }
                
                LocalizedText(localizationKey: textKey,
                              textWeight: .semiBold,
                              fontSize: 12,
                              color: .lightGrey)
            }
            .padding([.horizontal, .top], 10)
            .padding(.bottom, 8)
            .frame(height: 80)
            .frame(maxWidth: 250)
            .background(AppColor.white.color.cornerRadius(3))
        }
        
        init(imageName: String,
             textKey: String,
             counter: DataSource<KotlinInt>) {
            self.imageName = imageName
            self.textKey = textKey
            
            self.counter = SwiftDataSource(dataSource: counter)
        }
    }
}
