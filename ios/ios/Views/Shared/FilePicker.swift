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
    @ObservedObject var fileUploadData: FileUploadData
    
    @Binding var filePickerOption: FilePickerOption?
    
    let documentTypes: [String]
    let uploadData: (String, DataFileType) -> ()
    
    init(filePickerOption: Binding<FilePickerOption?>,
         documentTypes: [String],
         uploadData: @escaping (String, DataFileType) -> ()) {
        self.documentTypes = documentTypes
        self.uploadData = uploadData
        
        self.fileUploadData = FileUploadData(filePickerOption: filePickerOption)
        self._filePickerOption = filePickerOption
    }
    
    func body(content: Content) -> some View {
        content
            .actionSheet(isPresented: $fileUploadData.showingActionSheet) {
                actionSheet
            }
            .sheet(item: $fileUploadData.activeSheet) { sheet in
                getCurrentSheet(sheet)
                    .onDisappear {
                        filePickerOption = nil
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
                fileUploadData.documentPickerShown = true
            },
            .cancel {
                filePickerOption = nil
            }
        ])
    }
    
    private func getCurrentSheet(_ sheet: ActiveSheet) -> some View {
        switch sheet {
        
        case .documentPicker:
            guard fileUploadData.documentPickerShown else { return AnyView(EmptyView()) }
            
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
        
        self.uploadData(base64String, fileType)
    }
}

enum FilePickerOption {
    case actionSheet
    case documentPicker
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
    @Published var filePickerOption: FilePickerOption? {
        didSet {
            guard let filePickerOption =  self.filePickerOption else { return }

            self.showingActionSheet = filePickerOption == .actionSheet
            self.documentPickerShown = filePickerOption == .documentPicker
        }
    }
    
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
    
    init(filePickerOption: Binding<FilePickerOption?>) {
        self.filePickerOption = filePickerOption.wrappedValue
    }
}
