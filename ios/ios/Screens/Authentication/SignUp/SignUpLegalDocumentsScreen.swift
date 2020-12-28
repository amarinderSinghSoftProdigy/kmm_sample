//
//  SeasonBoyAadharCardDataScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 7.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct SignUpLegalDocumentsScreen: View {
    let scope: SignUpScope.LegalDocuments
    
    private var documentTypes: [String]!
    
    @State private var filePickerOption: FilePickerOption?
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        self.getView()
    }
    
    init(scope: SignUpScope.LegalDocuments) {
        self.scope = scope

        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        
        self.documentTypes = scope.getAvailableDocumentTypes(from: scope.supportedFileTypes)
    }
    
    private func getView() -> some View {
        let view: AnyView
        let buttonTextKey: String
        let navigationBarTitle: String
        
        switch scope {

        case let aadhaarScope as SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            view = AnyView(
                VStack {
                    AadhaardCardDataFields(aadhaarData: aadhaarScope.aadhaarData,
                                           changeCard: aadhaarScope.changeCard,
                                           changeShareCode: aadhaarScope.changeShareCode)
                    Spacer()
                })
            buttonTextKey = "upload_aadhaar_card"
            navigationBarTitle = "personal_data"

        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            view = AnyView(DrugLicenseData())
            buttonTextKey = "upload_new_document"
            navigationBarTitle = "legal_documents"
            
        default:
            view = AnyView(EmptyView())
            buttonTextKey = ""
            navigationBarTitle = ""
        }
        
        return AnyView(
            view
                .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                                       buttonTextKey: buttonTextKey,
                                       skipButtonAction: { scope.skip() },
                                       action: uploadDocuments))
                .keyboardResponder()
                .navigationBarTitle(LocalizedStringKey(navigationBarTitle), displayMode: .inline)
                .filePicker(filePickerOption: $filePickerOption,
                            forAvailableTypes: documentTypes,
                            uploadData: uploadData)
        )
    }
    
    private func uploadDocuments() {
        switch scope {
        
        case is SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            self.filePickerOption = .documentPicker
            
        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            self.filePickerOption = .actionSheet
            
        default:
            break
            
        }
    }
    
    private func uploadData(_ base64String: String, withFileType fileType: DataFileType) {
        switch scope {

        case let aadhaarScope as SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            _ = aadhaarScope.upload(base64: base64String)
                    
        case let drugLicenseScope as SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            _ = drugLicenseScope.upload(base64: base64String, fileType: fileType)

        default:
            break
        }
    }
}

// MARK: Documents specific views

struct AadhaardCardDataFields: View  {
    let changeCard: (String) -> ()
    let changeShareCode: (String) -> ()
    
    @ObservedObject var aadhaarData: SwiftDataSource<DataAadhaarData>
    
    var body: some View {
        VStack(spacing: 12) {
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "aadhaar_card",
                                         text: aadhaarData.value?.cardNumber,
                                         onTextChange: { newValue in self.changeCard(newValue) },
                                         keyboardType: .numberPad)
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "share_code",
                                         text: aadhaarData.value?.shareCode,
                                         onTextChange: { newValue in self.changeShareCode(newValue)},
                                         keyboardType: .numberPad)
        }
    }
    
    init(aadhaarData: DataSource<DataAadhaarData>,
         changeCard: @escaping (String) -> (),
         changeShareCode: @escaping (String) -> ()) {
        self.aadhaarData = SwiftDataSource(dataSource: aadhaarData)
        
        self.changeCard = changeCard
        self.changeShareCode = changeShareCode
    }
}

fileprivate struct DrugLicenseData: View  {
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 28) {
                Spacer()
                
                Image("UploadDocuments")
                
                LocalizedText(localizedStringKey: "drug_license_request",
                              fontSize: 16,
                              color: .grey)
                    .padding([.leading, .trailing], geometry.size.width * 0.17)
                
                Spacer()
            }
        }
    }
    
}
