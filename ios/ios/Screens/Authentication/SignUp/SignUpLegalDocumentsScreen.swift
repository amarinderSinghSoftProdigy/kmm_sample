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
    
    @State private var showingActionSheet = false
    
    @ObservedObject var fileUploadData = FileUploadData()
    
    @ObservedObject var canGoNext: SwiftDatasource<KotlinBoolean>
    
    var body: some View {
        self.getView()
    }
    
    init(scope: SignUpScope.LegalDocuments) {
        self.scope = scope

        self.canGoNext = SwiftDatasource(dataSource: scope.canGoNext)
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
            
                .actionSheet(isPresented: $showingActionSheet) {
                    actionSheet
                }
                .sheet(item: $fileUploadData.activeSheet) { sheet in
                    getCurrentSheet(sheet)
                }
        )
    }
    
    var actionSheet: ActionSheet {
        ActionSheet(title: Text(LocalizedStringKey("image_source")), buttons: [
            .default(Text(LocalizedStringKey("take_photo"))) {
                self.fileUploadData.imageSourceType = .camera
            },
            .default(Text(LocalizedStringKey("choose_from_photo_library"))) {
                self.fileUploadData.imageSourceType = .photoLibrary
            },
            .default(Text(LocalizedStringKey("choose_from_device"))) {
                self.fileUploadData.documentPickerType = .picture
            },
            .cancel()
        ])
    }
    
    private func getCurrentSheet(_ sheet: ActiveSheet) -> some View {
        switch sheet {
        
        case .documentPicker:
            guard let documentsType = self.fileUploadData.documentPickerType else { return AnyView(EmptyView()) }
            
            return AnyView(
                DocumentPicker(documentTypes: documentsType.getDocumentsTypes(),
                               onDocumentPicked: uploadFile))
            
        case .imagePicker:
            guard let sourceType = self.fileUploadData.imageSourceType else { return AnyView(EmptyView()) }
            
            return AnyView(
                ImagePicker(sourceType: sourceType,
                            onImagePicked: uploadImage))
        }
    }
    
    private func uploadDocuments() {
        switch scope {

        case is SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            fileUploadData.documentPickerType = .archive
        
        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            showingActionSheet = true
            
        default:
            break
        }
    }
    
    private func uploadImage(_ image: UIImage) {
        guard let imageData = image.pngData() else { return }

        uploadData(imageData, withFileExtension: "png")
    }
    
    private func uploadFile(fromPath filePath: URL) {
        guard let fileData = try? Data(contentsOf: filePath) else { return }
        
        let fileExtension = filePath.pathExtension
        
        uploadData(fileData, withFileExtension: fileExtension)
    }
    
    private func uploadData(_ data: Data, withFileExtension fileExtension: String) {
        let base64String = data.base64EncodedString(options: .lineLength64Characters)
        switch scope {

        case let aadhaarScope as SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            _ = aadhaarScope.upload(base64: base64String)
                    
        case let drugLicenseScope as SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
//            _ = drugLicenseScope.upload(base64String)
            break

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

// MARK: File Upload Data

fileprivate enum ActiveSheet: Identifiable {
    case documentPicker
    case imagePicker
    
    var id: Int { hashValue }
}

fileprivate enum DocumentsType: Identifiable {
    case archive
    case picture
    
    var id: Int { hashValue }
    
    func getDocumentsTypes() -> [String] {
        switch (self) {
        case .archive:
            return ["com.pkware.zip-archive"]
        case .picture:
            return ["public.jpeg", "public.png", "com.adobe.pdf"]
        }
    }
}

final class FileUploadData: ObservableObject {
    @Published fileprivate var activeSheet: ActiveSheet? {
        didSet {
            if activeSheet == nil {
                self.imageSourceType = nil
                self.documentPickerType = nil
            }
        }
    }
    
    var imageSourceType: UIImagePickerController.SourceType? {
        didSet {
            if imageSourceType == nil { return }
            
            activeSheet = .imagePicker
        }
    }
    fileprivate var documentPickerType: DocumentsType? {
        didSet {
            if documentPickerType == nil { return }
            
            activeSheet = .documentPicker
        }
    }
}

