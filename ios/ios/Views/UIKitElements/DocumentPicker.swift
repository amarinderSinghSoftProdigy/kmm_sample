//
//  DocumentPicker.swift
//  ios
//
//  Created by Dasha Gurinovich on 8.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI

struct DocumentPicker: UIViewControllerRepresentable {
    @Environment(\.presentationMode) private var presentationMode
    
    let documentTypes: [String]
    /// The max uploaded document size in MB
    let maxDocumentSize: Double
    
    let onDocumentPicked: (URL) -> Void
    let onAboveLimitDocumentPicked: (() -> ())?
    
    init(documentTypes: [String],
         maxDocumentSize: Double = 1.0,
         onDocumentPicked: @escaping (URL) -> Void,
         onAboveLimitDocumentPicked: (() -> ())? = nil) {
        self.documentTypes = documentTypes
        self.maxDocumentSize = maxDocumentSize
        
        self.onDocumentPicked = onDocumentPicked
        self.onAboveLimitDocumentPicked = onAboveLimitDocumentPicked
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    func makeUIViewController(context: UIViewControllerRepresentableContext<DocumentPicker>) -> UIDocumentPickerViewController {
        
        let picker = UIDocumentPickerViewController(documentTypes: documentTypes, in: .import)
        picker.delegate = context.coordinator
        
        return picker
    }

    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController,
                                context: UIViewControllerRepresentableContext<DocumentPicker>) { }
    
    class Coordinator: NSObject,
                       UIDocumentPickerDelegate {
        let parent: DocumentPicker

        init(_ parent: DocumentPicker) {
            self.parent = parent
        }
        
        func documentPicker(_ controller: UIDocumentPickerViewController,
                            didPickDocumentsAt urls: [URL]) {
            guard controller.documentPickerMode == .import,
                  let url = urls.first
            else { return }
            
            if let attributes = try? FileManager.default.attributesOfItem(atPath: url.path),
               let fileSize = attributes[FileAttributeKey.size] as? Double,
               fileSize <= parent.maxDocumentSize * 1000000.0 {
                parent.onDocumentPicked(url)
            }
            else {
                parent.onAboveLimitDocumentPicked?()
            }
            
            parent.presentationMode.wrappedValue.dismiss()
        }
        
        func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}
