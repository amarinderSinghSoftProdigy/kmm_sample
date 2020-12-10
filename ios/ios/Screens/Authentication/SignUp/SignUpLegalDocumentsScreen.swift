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
    
    @State private var showingActionSheet = false
    
    @ObservedObject var fileUploadData = FileUploadData()
    
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
                fileUploadData.showDocumentPicker = true
            },
            .cancel()
        ])
    }
    
    private func getCurrentSheet(_ sheet: ActiveSheet) -> some View {
        switch sheet {
        
        case .documentPicker:
            guard fileUploadData.showDocumentPicker else { return AnyView(EmptyView()) }
            
            return AnyView(
                DocumentPicker(documentTypes: documentTypes,
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
            fileUploadData.showDocumentPicker = true
        
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
        let fileType = DataFileType.Utils().fromExtension(ext: fileExtension)
        
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

// MARK: File Upload Data

fileprivate enum ActiveSheet: Identifiable {
    case documentPicker
    case imagePicker
    
    var id: Int { hashValue }
}

extension DataFileType {
    func getUniformTypeIdentifier() -> String? {
        switch self {
        case .jpeg, .jpg:
            return "public.jpeg"
        
        case .pdf:
            return "com.adobe.pdf"
            
        case .png:
            return "public.png"
            
        case .xzip:
            return ""
            
        case .zip:
            return "com.pkware.zip-archive"
            
        case .unknown:
            return "public.content"
            
        default:
            return nil
        }
    }
}

final class FileUploadData: ObservableObject {
    @Published fileprivate var activeSheet: ActiveSheet? {
        didSet {
            if activeSheet == nil {
                self.imageSourceType = nil
                self.showDocumentPicker = false
            }
        }
    }
    
    var imageSourceType: UIImagePickerController.SourceType? {
        didSet {
            if imageSourceType == nil { return }
            
            activeSheet = .imagePicker
        }
    }
    
    var showDocumentPicker: Bool = false {
        didSet {
            if !showDocumentPicker { return }
            
            activeSheet = .documentPicker
        }
    }
}

