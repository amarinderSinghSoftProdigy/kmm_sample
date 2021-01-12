//
//  FilePicker.swift
//  ios
//
//  Created by Dasha Gurinovich on 11.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct FilePicker: ViewModifier {
    @ObservedObject var bottomSheet: SwiftDataSource<BottomSheet.UploadDocuments>
    @ObservedObject var fileUploadData: FileUploadData
    
    let onBottomSheetDismiss: () -> ()
    
    init(bottomSheet: BaseDataSource<BottomSheet.UploadDocuments>,
         onBottomSheetDismiss: @escaping () -> ()) {
        let bottomSheetDataSource = SwiftDataSource(dataSource: bottomSheet)
        self.bottomSheet = bottomSheetDataSource
        
        self.onBottomSheetDismiss = onBottomSheetDismiss
        self.fileUploadData = FileUploadData(bottomSheet: bottomSheetDataSource)
    }
    
    func body(content: Content) -> some View {
        content
            .actionSheet(isPresented: $fileUploadData.showingActionSheet) {
                actionSheet
            }
            .sheet(item: $fileUploadData.activeSheet) { sheet in
                getCurrentSheet(sheet)
                    .onDisappear {
                        onBottomSheetDismiss()
                    }
            }
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
                self.fileUploadData.documentPickerShown = true
            },
            .cancel {
                onBottomSheetDismiss()
            }
        ])
    }
    
    private func getCurrentSheet(_ sheet: ActiveSheet) -> some View {
        switch sheet {
        
        case .documentPicker:
            guard fileUploadData.documentPickerShown,
                  let uploadDocumentsSheet = self.bottomSheet.value else { return AnyView(EmptyView()) }
            
            let documentTypes = getAvailableDocumentTypes(from: uploadDocumentsSheet.supportedFileTypes)
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
    
    private func getAvailableDocumentTypes(from fileTypes: KotlinArray<DataFileType>) -> [String] {
        var documentTypes = [String]()
        
        let iterator = fileTypes.iterator()
        while iterator.hasNext() {
            guard let fileType = iterator.next() as? DataFileType,
                  fileType.isMandatory,
                  let uti = fileType.getUniformTypeIdentifier() else { continue }
            
            documentTypes.append(uti)
        }
        
        return documentTypes
    }
    
    private func uploadImage(_ image: UIImage) {
        guard let imageData = image.jpegData(compressionQuality: 0.85) else { return }

        bottomSheet.value?.uploadData(imageData, withFileExtension: "jpeg")
    }
    
    private func uploadFile(fromPath filePath: URL) {
        guard let fileData = try? Data(contentsOf: filePath) else { return }
        
        let fileExtension = filePath.pathExtension
        
        bottomSheet.value?.uploadData(fileData, withFileExtension: fileExtension)
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
    @Published var showingActionSheet = false
    
    @Published fileprivate var activeSheet: ActiveSheet? {
        didSet {
            if activeSheet == nil {
                self.imageSourceType = nil
                self.documentPickerShown = false
            }
        }
    }
    
    var imageSourceType: UIImagePickerController.SourceType? {
        didSet {
            if imageSourceType == nil { return }
            
            activeSheet = .imagePicker
        }
    }
    
    var documentPickerShown: Bool = false {
        didSet {
            if !documentPickerShown { return }
            
            activeSheet = .documentPicker
        }
    }
    
    init(bottomSheet: SwiftDataSource<BottomSheet.UploadDocuments>) {
        bottomSheet.onValueDidSet = { newValue in
            self.showingActionSheet = newValue != nil && !newValue!.isSeasonBoy
            self.documentPickerShown = newValue != nil && newValue!.isSeasonBoy
        }
    }
}

extension BottomSheet.UploadDocuments {
    func uploadData(_ data: Data, withFileExtension fileExtension: String) {
        let base64String = data.base64EncodedString()
        
        if isSeasonBoy {
            self.uploadAadhaar(base64: base64String)
        }
        else {
            let fileType = DataFileType.Utils().fromExtension(ext: fileExtension)
            
            self.uploadDrugLicense(base64: base64String, fileType: fileType)
        }
    }
}
