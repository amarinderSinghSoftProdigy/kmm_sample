//
//  LimitedAppAccessScreen.swift
//  ios
//
//  Created by Dasha Gurinovich on 11.12.20.
//  Copyright © 2020 Zeal Software Solutions. All rights reserved.
//

import SwiftUI
import core

struct LimitedAppAccessScreen: View {
    let scope: MainScope.LimitedAccess
    let user: DataUser
    
    private let documentTypes: [String]!
    @State private var filePickerOption: FilePickerOption?
    
    var body: some View {
        GeometryReader { geometry in
            VStack {
                let isDocumentUploaded = scope.isDocumentUploaded
                
                Spacer()
                
                VStack(spacing: 50) {
                    Text(LocalizedStringKey("welcome \(user.fullName())"))
                        .modifier(MedicoText(textWeight: .medium, fontSize: 20))
                    
                    if isDocumentUploaded {
                        self.uploadedDocumentView
                    }
                    else {
                        self.nonUploadedDocumentView
                    }
                }
                .padding([.leading, .trailing], geometry.size.width * 0.19)
                
                Spacer()
                
                if !isDocumentUploaded {
                    MedicoButton(localizedStringKey: "upload_new_document") {
                        filePickerOption = scope.isCameraOptionAvailable ? .actionSheet : .documentPicker
                    }
                    .padding()
                    .padding(.bottom, geometry.size.height * 0.1)
                    .filePicker(filePickerOption: $filePickerOption,
                                forAvailableTypes: documentTypes,
                                uploadData: uploadData)
                }
            }
        }
    }
    
    var uploadedDocumentView: some View {
        VStack(spacing: 30) {
            Image("Welcome")
            
            VStack(spacing: 19) {
                Text(LocalizedStringKey("thank_you_for_registration"))
                    .modifier(MedicoText(fontSize: 16))
                
                Text(LocalizedStringKey("documents_under_review"))
                    .modifier(MedicoText(fontSize: 16))
            }
        }
    }
    
    var nonUploadedDocumentView: some View {
        VStack(spacing: 30) {
            Image("UploadDocuments")
            
            let textKey = scope.isCameraOptionAvailable ? "drug_license_request" : "aadhaar_card_request"
            
            Text(LocalizedStringKey(textKey))
                .modifier(MedicoText(fontSize: 16))
        }
    }
    
    init(scope: MainScope.LimitedAccess,
         user: DataUser) {
        self.scope = scope
        self.user = user
        
        self.documentTypes = scope.getAvailableDocumentTypes(from: scope.supportedFileTypes)
    }
    
    private func uploadData(_ base64: String, withFileType fileType: DataFileType) {
        if scope.isCameraOptionAvailable {
            scope.uploadDrugLicense(base64: base64, fileType: fileType)
            
            return
        }
        
        scope.uploadAadhaar(base64: base64)
    }
}
