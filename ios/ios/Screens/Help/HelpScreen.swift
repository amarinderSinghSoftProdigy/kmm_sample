//
//  HelpScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 20.04.21.
//  Copyright © 2021 Zeal Software Solutions. All rights reserved.
//

import core
import SwiftUI

struct HelpScreen: View {
    let scope: HelpScope
    
    @State private var activeSheet: ActiveSheet?
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Image("CustomerCare")
                
                LocalizedText(localizationKey: "instantly_connect_with_us",
                              textWeight: .semiBold,
                              color: .white)
                
                Spacer()
                
                MedicoButton(localizedStringKey: "call_now",
                             width: 82,
                             height: 30,
                             fontSize: 14,
                             fontWeight: .bold) {
                    self.call(scope.helpData.contactUs.customerCarePhoneNumber)
                }
            }
            .padding(10)
            .background(
                AppColor.lightBlue.color
                    .cornerRadius(8, corners: [.topLeft, .topRight])
            )
            
            LocalizedText(localizationKey: "contact_information",
                          textWeight: .semiBold,
                          fontSize: 16)
                .padding(.horizontal, 20)
            
            VStack(spacing: 22) {
                Divider()
                
                let phone = scope.helpData.contactUs.salesPhoneNumber
                VStack(spacing: 16) {
                    InfoItem(titleLocalizationKey: "call_sales",
                             onTapGesture: { self.call(phone) }) {
                        
                        HStack(spacing: 10) {
                            Image(systemName: "phone.fill")
                                .foregroundColor(appColor: .lightBlue)

                            Text(phone)
                                .medicoText(textWeight: .medium,
                                            fontSize: 15,
                                            color: .lightBlue)
                        }
                    }
                    
                    InfoItem(titleLocalizationKey: "send_email",
                             onTapGesture: { self.activeSheet = .mail }) {
                        Text(scope.helpData.contactUs.email)
                            .medicoText(textWeight: .medium,
                                        fontSize: 15,
                                        color: .lightBlue)
                    }
                }
                .padding(.horizontal, 20)
                
                Divider()
                
                Group {
                    LinkItem(titleLocalizationKey: "terms_and_conditions",
                             imageName: "TermsAndConditions")
                        .onTapGesture {
                            self.openLink(scope.helpData.tosUrl)
                        }
                    
                    LinkItem(titleLocalizationKey: "privacy_policy",
                             systemImageName: "shield.checkerboard")
                        .onTapGesture {
                            self.openLink(scope.helpData.privacyPolicyUrl)
                        }
                }
                .padding(.horizontal, 20)
            }
            .padding(.bottom, 20)
        }
        .background(AppColor.white.color.cornerRadius(8))
        .frame(maxWidth: 345)
        .sheet(item: $activeSheet) {
            switch $0 {
            case .mail:
                let isShowingMailView = Binding(get: { self.activeSheet == .mail },
                                                set: { if !$0 { self.activeSheet = nil } })
                
                MailView(recipientEmail: scope.helpData.contactUs.email,
                         isShowing: isShowingMailView)
                
            case .safari(let url):
                SafariView(url: url)
            }
        }
        .centerWithStacks()
    }
    
    private func openLink(_ link: String) {
        guard let url = URL(string: link) else { return }
        
        self.activeSheet = .safari(url: url)
    }
    
    private struct InfoItem<Content: View>: View {
        let titleLocalizationKey: String
        let onTapGesture: () -> ()
        
        let content: Content
        
        var body: some View {
            HStack {
                LocalizedText(localizationKey: titleLocalizationKey,
                              textWeight: .medium,
                              fontSize: 15)
                
                Spacer()
                
                content
                    .padding(.vertical, 8)
                    .padding(.horizontal, 12)
                    .background(
                        RoundedRectangle(cornerRadius: 8)
                            .stroke(lineWidth: 1)
                            .foregroundColor(appColor: .lightBlue)
                            .background(AppColor.lightBlue.color
                                            .opacity(0.12)
                                            .cornerRadius(8)
                            )
                    )
                    .onTapGesture {
                        self.onTapGesture()
                    }
            }
        }
        
        init(titleLocalizationKey: String,
             onTapGesture: @escaping () -> (),
             @ViewBuilder content: () -> Content) {
            self.titleLocalizationKey = titleLocalizationKey
            self.onTapGesture = onTapGesture
            
            self.content = content()
        }
    }
    
    private struct LinkItem: View {
        let titleLocalizationKey: String
        
        let imageName: String?
        let systemImageName: String?
        
        var body: some View {
            HStack(spacing: 27) {
                if let imageName = self.imageName {
                    Image(imageName)
                }
                if let systemName = self.systemImageName {
                    Image(systemName: systemName)
                        .foregroundColor(appColor: .darkBlue)
                }
                
                LocalizedText(localizationKey: titleLocalizationKey,
                              textWeight: .semiBold)
                
                Spacer()
                
                Image(systemName: "link")
                    .foregroundColor(appColor: .lightBlue)
            }
        }
        
        init(titleLocalizationKey: String,
             imageName: String) {
            self.titleLocalizationKey = titleLocalizationKey
            
            self.imageName = imageName
            self.systemImageName = nil
        }
        
        init(titleLocalizationKey: String,
             systemImageName: String) {
            self.titleLocalizationKey = titleLocalizationKey
            
            self.imageName = nil
            self.systemImageName = systemImageName
        }
    }
    
    private enum ActiveSheet: Identifiable, Equatable {
        var id: Int {
            switch self {
            case .mail:
                return "mail".hashValue
                
            case .safari(let url):
                return url.hashValue
            }
        }
        
        case mail
        case safari(url: URL)
    }
}
