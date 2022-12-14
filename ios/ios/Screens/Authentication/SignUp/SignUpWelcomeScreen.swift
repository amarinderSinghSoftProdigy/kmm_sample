//
//  SignUpWelcomeScreen.swift
//  Medico
//
//  Created by Dasha Gurinovich on 28.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct WelcomeScreen: View {
    let welcomeOption: WelcomeOption
    let userName: String
    
    @ObservedObject var uploadButtonEnabled: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        let view = AnyView(
            GeometryReader { geometry in
                VStack {
                    Spacer()
                    
                    VStack(spacing: 50) {
                        LocalizedText(localizedStringKey: LocalizedStringKey("welcome \(userName)"),
                                      textWeight: .medium,
                                      fontSize: 20)
                        
                        if welcomeOption is WelcomeOption.Thanks {
                            self.uploadedDocumentCenterView
                        }
                        else if let uploadOption = welcomeOption as? WelcomeOption.Upload {
                            self.getUploadDocumentCenterView(for: uploadOption)
                        }
                    }
                    .padding(.horizontal, geometry.size.width * 0.19)
                    
                    Spacer()
                    
                    self.getBottomView(forGeometry: geometry)
                }
            }
            .screenLogger(withScreenName: "WelcomeScreen.\(welcomeOption.optionName)",
                          withScreenClass: WelcomeScreen.self)
        )
        
        if !(welcomeOption is WelcomeOption.Upload.AadhaarCard) { return view }
        
        return AnyView(
            ZStack {
                AppColor.primary.color
                
                view
            }
            .textFieldsModifiers()
        )
    }
    
    var uploadedDocumentCenterView: some View {
        VStack(spacing: 30) {
            Image("Welcome")
            
            VStack(spacing: 19) {
                LocalizedText(localizationKey: "thank_you_for_registration",
                              fontSize: 16)
                
                LocalizedText(localizationKey: "documents_under_review",
                              fontSize: 16)
            }
            .fixedSize(horizontal: false, vertical: true)
        }
    }
    
    init(welcomeOption: WelcomeOption,
         userName: String) {
        self.welcomeOption = welcomeOption
        self.userName = userName
        
        if let aadhaarCardOption = welcomeOption as? WelcomeOption.Upload.AadhaarCard {
            self.uploadButtonEnabled = SwiftDataSource(dataSource: aadhaarCardOption.aadhaarDataComponent.isVerified)
        }
        else {
            self.uploadButtonEnabled = SwiftDataSource(dataSource: DataSource(initialValue: true))
        }
    }
    
    private func getUploadDocumentCenterView(for optionView: WelcomeOption.Upload) -> some View {
        VStack(spacing: 30) {
            Image("UploadDocuments")
            
            LocalizedText(localizationKey: optionView.uploadDocumentTextKey,
                          fontSize: 16,
                          color: .grey1)
        }
        .fixedSize(horizontal: false, vertical: true)
    }
    
    private func getBottomView(forGeometry geometry: GeometryProxy) -> some View {
        let view: AnyView
        
        if let thanksOption = self.welcomeOption as? WelcomeOption.Thanks,
           let thanksButtonAction = thanksOption.onButtonClick {
            
            view = AnyView(
                MedicoButton(localizedStringKey: "okay") {
                    thanksButtonAction()
                }
            )
        }
        else if let uploadOption = self.welcomeOption as? WelcomeOption.Upload {
            view = AnyView(
                VStack(spacing: 32) {
                    if let aadhaarCard = uploadOption as? WelcomeOption.Upload.AadhaarCard {
                        AadhaardCardDataFields(aadhaarData: aadhaarCard.aadhaarDataComponent.aadhaarData,
                                               changeCard: aadhaarCard.aadhaarDataComponent.changeCard,
                                               changeShareCode: aadhaarCard.aadhaarDataComponent.changeShareCode)
                    }
                    
                    MedicoButton(localizedStringKey: uploadOption.buttonTextKey,
                                 isEnabled: self.uploadButtonEnabled.value == true) {
                        uploadOption.onUploadClick()
                    }
                }
            )
        }
        else { return AnyView(EmptyView()) }
        
        return AnyView(
            view
                .padding()
                .padding(.bottom, geometry.size.height * 0.1)
            
        )
    }
}

class WelcomeOption {
    var optionName: String { "WelcomeOption" }
    
    class Thanks: WelcomeOption {
        override var optionName: String { "Thanks" }
        
        let onButtonClick: (() -> ())?
        
        init(onButtonClick: (() -> ())? = nil) {
            self.onButtonClick = onButtonClick
        }
    }
    
    class Upload: WelcomeOption {
        override var optionName: String { "Upload" }
        
        var uploadDocumentTextKey: String { "" }
        var buttonTextKey: String { "" }
        
        let onUploadClick: () -> ()
        
        init(onUploadClick: @escaping () -> ()) {
            self.onUploadClick = onUploadClick
        }
        
        class AadhaarCard: Upload {
            override var optionName: String { "UploadAadhaarCard" }
            
            override var uploadDocumentTextKey: String { "aadhaar_card_request" }
            override var buttonTextKey: String { "upload_aadhaar_card" }
            
            let aadhaarDataComponent: AadhaarDataComponent
            
            init(aadhaarDataComponent: AadhaarDataComponent,
                 onUploadClick: @escaping () -> ()) {
                self.aadhaarDataComponent = aadhaarDataComponent
                
                super.init(onUploadClick: onUploadClick)
            }
        }
        
        class DrugLicense: Upload {
            override var optionName: String { "UploadDrugLicense" }
            
            override var uploadDocumentTextKey: String { "drug_license_request" }
            override var buttonTextKey: String { "upload_new_document" }
        }
    }
}

