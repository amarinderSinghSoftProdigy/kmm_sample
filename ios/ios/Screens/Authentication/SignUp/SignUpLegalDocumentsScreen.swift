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
        let documentRequestTextKey: String
        let buttonTextKey: String
        
        switch scope {

        case is SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            documentRequestTextKey = "aadhaar_card_request"
            buttonTextKey = "upload_aadhaar_card"

        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            documentRequestTextKey = "drug_license_request"
            buttonTextKey = "upload_new_document"
            
        default:
            documentRequestTextKey = ""
            buttonTextKey = ""
        }
        
        return AnyView(
            GeometryReader { geometry in
                HStack {
                    Spacer()
                    
                    VStack(spacing: 28) {
                        Spacer()
                        
                        Image("UploadDocuments")
                        
                        LocalizedText(localizedStringKey: documentRequestTextKey,
                                      fontSize: 16,
                                      color: .grey1)
                        
                        Spacer()
                    }
                    .padding([.leading, .trailing], geometry.size.width * 0.17)
                    
                    Spacer()
                }
            }
            .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                                   buttonTextKey: buttonTextKey,
                                   skipButtonAction: { scope.skip() },
                                   action: uploadDocuments))
            .navigationBarTitle(LocalizedStringKey("legal_documents"), displayMode: .inline)
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
