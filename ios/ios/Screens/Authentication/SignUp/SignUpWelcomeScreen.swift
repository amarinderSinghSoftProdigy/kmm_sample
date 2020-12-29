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
    
    @State private var filePickerOption: FilePickerOption?
    
    @ObservedObject var uploadButtonEnabled: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        GeometryReader { geometry in
            VStack {
                Spacer()
                
                VStack(spacing: 50) {
                    Text(LocalizedStringKey("welcome \(userName)"))
                        .modifier(MedicoText(textWeight: .medium, fontSize: 20))
                        .testingIdentifier("welcome")
                    
                    if welcomeOption is WelcomeOption.Thanks {
                        self.uploadedDocumentCenterView
                    }
                    else if let uploadOption = welcomeOption as? WelcomeOption.Upload {
                        self.getUploadDocumentCenterView(for: uploadOption)
                    }
                }
                .padding([.leading, .trailing], geometry.size.width * 0.19)
                
                Spacer()
                
                self.getBottomView(forGeometry: geometry)
            }
        }
    }
    
    var uploadedDocumentCenterView: some View {
        VStack(spacing: 30) {
            Image("Welcome")
            
            VStack(spacing: 19) {
                LocalizedText(localizedStringKey: "thank_you_for_registration",
                              fontSize: 16)
                
                LocalizedText(localizedStringKey: "documents_under_review",
                              fontSize: 16)
            }
        }
    }
    
    func getUploadDocumentCenterView(for optionView: WelcomeOption.Upload) -> some View {
        VStack(spacing: 30) {
            Image("UploadDocuments")
            
            LocalizedText(localizedStringKey: optionView.uploadDocumentTextKey,
                          fontSize: 16,
                          color: .grey)
        }
    }
    
    init(welcomeOption: WelcomeOption,
         userName: String) {
        self.welcomeOption = welcomeOption
        self.userName = userName
        
        if let aadhaarCardOption = welcomeOption as? WelcomeOption.Upload.AadhaarCard {
            self.uploadButtonEnabled = SwiftDataSource(dataSource: aadhaarCardOption.isVerified)
        }
        else {
            self.uploadButtonEnabled = SwiftDataSource(dataSource: DataSource(initialValue: true))
        }
    }
    
    private func getBottomView(forGeometry geometry: GeometryProxy) -> some View {
        let view: AnyView
        
        if let thanksOption = self.welcomeOption as? WelcomeOption.Thanks,
           let thanksButtonAction = thanksOption.onButtonClick {
            
            view = AnyView(
                MedicoButton(localizedStringKey: "okay") {
                    thanksButtonAction()
                }
                .navigationBarHidden(true)
            )
        }
        else if let uploadOption = self.welcomeOption as? WelcomeOption.Upload {
            view = AnyView(
                VStack(spacing: 32) {
                    if let aadhaarCard = uploadOption as? WelcomeOption.Upload.AadhaarCard {
                        AadhaardCardDataFields(aadhaarData: aadhaarCard.aadhaarData,
                                               changeCard: aadhaarCard.changeCard,
                                               changeShareCode: aadhaarCard.changeShareCode)
                    }
                    
                    MedicoButton(localizedStringKey: uploadOption.buttonTextKey,
                                 isEnabled: self.uploadButtonEnabled.value == true) {
                        self.filePickerOption = uploadOption.filePickerOption
                    }
                }
                .filePicker(filePickerOption: $filePickerOption,
                            forAvailableTypes: uploadOption.documentTypes,
                            uploadData: uploadOption.uploadData)
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
    class Thanks: WelcomeOption {
        let onButtonClick: (() -> ())?
        
        init(onButtonClick: (() -> ())? = nil) {
            self.onButtonClick = onButtonClick
        }
    }
    
    class Upload: WelcomeOption {
        var uploadDocumentTextKey: String { "" }
        var buttonTextKey: String { "" }
        var filePickerOption: FilePickerOption { .actionSheet }
        
        let documentTypes: [String]
        let uploadData: (String, DataFileType) -> ()
        
        init(documentTypes: [String],
             uploadData: @escaping (String, DataFileType) -> ()) {
            self.documentTypes = documentTypes
            self.uploadData = uploadData
        }
        
        class AadhaarCard: Upload {
            override var uploadDocumentTextKey: String { "aadhaar_card_request" }
            override var buttonTextKey: String { "upload_aadhaar_card" }
            override var filePickerOption: FilePickerOption { .documentPicker }
            
            let isVerified: DataSource<KotlinBoolean>
            
            let aadhaarData: DataSource<DataAadhaarData>
            
            let changeCard: (String) -> ()
            let changeShareCode: (String) -> ()
            
            init(documentTypes: [String],
                 isVerified: DataSource<KotlinBoolean>,
                 aadhaarData: DataSource<DataAadhaarData>,
                 changeCard: @escaping (String) -> (),
                 changeShareCode: @escaping (String) -> (),
                 uploadData: @escaping (String, DataFileType) -> ()) {
                self.isVerified = isVerified
                
                self.aadhaarData = aadhaarData
                
                self.changeCard = changeCard
                self.changeShareCode = changeShareCode
                
                super.init(documentTypes: documentTypes,
                           uploadData: uploadData)
            }
        }
        
        class DrugLicense: Upload {
            override var uploadDocumentTextKey: String { "drug_license_request" }
            override var buttonTextKey: String { "upload_new_document" }
            override var filePickerOption: FilePickerOption { .actionSheet }
        }
    }
}

