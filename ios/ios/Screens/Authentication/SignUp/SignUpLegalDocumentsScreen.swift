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
    
    @ObservedObject var canGoNext: SwiftDatasource<KotlinBoolean>
    
    var body: some View {
        self.getView()
    }
    
    init(scope: SignUpScope.LegalDocuments) {
        self.scope = scope

        self.canGoNext = SwiftDatasource(dataSource: scope.canGoNext)
        
        self.documentTypes = getAvailableDocumentTypes(for: scope)
    }
    
    private func getAvailableDocumentTypes(for scope: SignUpScope.LegalDocuments) -> [String] {
        var documentTypes = [String]()
        
        let iterator = scope.supportedFileTypes.iterator()
        while iterator.hasNext() {
            guard let fileType = iterator.next() as? DataFileType,
                  fileType.isMandatory,
                  let uti = fileType.getUniformTypeIdentifier() else { continue }
            
            documentTypes.append(uti)
        }
        
        return documentTypes
    }
    
    private func getView() -> some View {
        let view: AnyView
        let buttonTextKey: String
        let navigationBarTitle: String

        switch scope {

        case let aadhaarScope as SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            view = AnyView(AadhaardCardDataFields(scope: aadhaarScope))
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
                                       skipButtonAction: skip,
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
    
    private func skip() {
        scope.skip()
    }
}

// MARK: Documents specific views

fileprivate struct AadhaardCardDataFields: View  {
    let scope: SignUpScope.LegalDocuments.LegalDocumentsAadhaar
    
    @ObservedObject var aadhaarData: SwiftDatasource<DataAadhaarData>
    
    var body: some View {
        VStack(spacing: 12) {
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "aadhaar_card",
                                         text: aadhaarData.value?.cardNumber,
                                         onTextChange: { newValue in scope.changeCard(card: newValue) },
                                         keyboardType: .numberPad)
            
            FloatingPlaceholderTextField(placeholderLocalizedStringKey: "share_code",
                                         text: aadhaarData.value?.shareCode,
                                         onTextChange: { newValue in scope.changeShareCode(shareCode: newValue)},
                                         keyboardType: .numberPad)
            
            Spacer()
        }
    }
    
    init(scope: SignUpScope.LegalDocuments.LegalDocumentsAadhaar) {
        self.scope = scope
        
        self.aadhaarData = SwiftDatasource(dataSource: scope.aadhaarData)
    }
}

fileprivate struct DrugLicenseData: View  {
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 28) {
                Spacer()
                
                Image("UploadDocuments")
                
                Text(LocalizedStringKey("drug_license_request"))
                    .modifier(MedicoText(fontSize: 16, color: .grey))
                    .padding([.leading, .trailing], geometry.size.width * 0.17)
                
                Spacer()
            }
        }
    }
    
}
