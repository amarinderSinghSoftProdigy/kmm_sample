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
    let onDocumentPicked: (URL) -> Void
    
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
            
            parent.onDocumentPicked(url)
            
            parent.presentationMode.wrappedValue.dismiss()
        }
        
        func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}
