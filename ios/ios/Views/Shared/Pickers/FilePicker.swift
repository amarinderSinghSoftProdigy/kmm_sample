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
    @State private var showingActionSheet: Bool = false
    
    @State private var imageSourceType: UIImagePickerController.SourceType?
    @State private var documentPickerShown: Bool = false
    
    let bottomSheet: BottomSheet.UploadDocuments
    
    let onBottomSheetDismiss: () -> ()
    
    init(bottomSheet: BottomSheet.UploadDocuments,
         onBottomSheetDismiss: @escaping () -> ()) {
        self.bottomSheet = bottomSheet
        
        self._showingActionSheet = State(initialValue: !bottomSheet.isSeasonBoy)
        self._documentPickerShown = State(initialValue: bottomSheet.isSeasonBoy)
        
        self.onBottomSheetDismiss = onBottomSheetDismiss
    }
    
    func body(content: Content) -> some View {
        let activeSheet = Binding(get: { () -> ActiveSheet? in
            if documentPickerShown { return .documentPicker }
            
            if imageSourceType != nil { return .imagePicker }
            
            return nil
        }, set: { newValue in
            if newValue != nil { return }
            
            self.documentPickerShown = false
            self.imageSourceType = nil
        })
        
        return AnyView(
            content
                .popSheet(isPresented: $showingActionSheet) {
                    popSheet
                }
                .sheet(item: activeSheet) { sheet in
                    getCurrentSheet(sheet)
                        .onDisappear {
                            onBottomSheetDismiss()
                        }
                }
        )
    }
    
    var popSheet: PopSheet {
        PopSheet(title: Text(LocalizedStringKey("image_source")), buttons: [
            .default(Text(LocalizedStringKey("take_photo"))) {
                self.imageSourceType = .camera
            },
            .default(Text(LocalizedStringKey("choose_from_photo_library"))) {
                self.imageSourceType = .photoLibrary
            },
            .default(Text(LocalizedStringKey("choose_from_device"))) {
                self.documentPickerShown = true
            },
            .cancel {
                onBottomSheetDismiss()
            }
        ]) {
            if !documentPickerShown && imageSourceType == nil {
                onBottomSheetDismiss()
            }
        }
    }
    
    private func getCurrentSheet(_ sheet: ActiveSheet) -> some View {
        switch sheet {
        
        case .documentPicker:
            guard documentPickerShown else { return AnyView(EmptyView()) }
            
            let documentTypes = getAvailableDocumentTypes(from: self.bottomSheet.supportedFileTypes)
            return AnyView(
                DocumentPicker(documentTypes: documentTypes,
                               onDocumentPicked: uploadFile))
            
        case .imagePicker:
            guard let sourceType = self.imageSourceType else { return AnyView(EmptyView()) }
            
            return AnyView(
                ImagePicker(sourceType: sourceType,
                            onImagePicked: uploadImage))
        }
    }
    
    private func getAvailableDocumentTypes(from fileTypes: KotlinArray<DataFileType>) -> [String] {
        var documentTypes = [String]()
        
        let iterator = fileTypes.iterator()
        while iterator.hasNext() {
            guard let fileType = iterator.next_() as? DataFileType,
                  fileType.isMandatory,
                  let uti = fileType.getUniformTypeIdentifier() else { continue }
            
            documentTypes.append(uti)
        }
        
        return documentTypes
    }
    
    private func uploadImage(_ image: UIImage) {
        guard let imageData = image.jpegData(compressionQuality: 0.85) else { return }

        bottomSheet.uploadData(imageData, withFileExtension: "jpeg")
    }
    
    private func uploadFile(fromPath filePath: URL) {
        guard let fileData = try? Data(contentsOf: filePath) else { return }
        
        let fileExtension = filePath.pathExtension
        
        bottomSheet.uploadData(fileData, withFileExtension: fileExtension)
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
