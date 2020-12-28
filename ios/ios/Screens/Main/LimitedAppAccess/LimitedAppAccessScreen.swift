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
    
    private let isSeasonBoy: Bool
    
    private let documentTypes: [String]!
    @State private var filePickerOption: FilePickerOption?
    
    @ObservedObject var uploadButtonEnabled: SwiftDataSource<KotlinBoolean>
    
    var body: some View {
        GeometryReader { geometry in
            VStack {
                let isDocumentUploaded = scope.isDocumentUploaded
                
                Spacer()
                
                VStack(spacing: 50) {
                    Text(LocalizedStringKey("welcome \(user.fullName())"))
                        .modifier(MedicoText(textWeight: .medium, fontSize: 20))
                        .testingIdentifier("welcome")
                    
                    if isDocumentUploaded {
                        self.uploadedDocumentCenterView
                    }
                    else {
                        self.nonUploadedDocumentCenterView
                    }
                }
                .padding([.leading, .trailing], geometry.size.width * 0.19)
                
                Spacer()
                
                self.getBottomView(forGeometry: geometry)
            }
        }
    }
    
    var uploadedDocumentCenterView: some View {
        VStack(spacing: 30) {
            Image("Welcome")
            
            VStack(spacing: 19) {
                LocalizedText(localizedStringKey: "thank_you_for_registration",
                              fontSize: 16)
                
                LocalizedText(localizedStringKey: "documents_under_review",
                              fontSize: 16)
            }
        }
    }
    
    var nonUploadedDocumentCenterView: some View {
        VStack(spacing: 30) {
            Image("UploadDocuments")
            
            let textKey = !isSeasonBoy ? "drug_license_request" : "aadhaar_card_request"
            
            LocalizedText(localizedStringKey: textKey,
                          fontSize: 16,
                          color: .grey)
        }
    }
    
    init(scope: MainScope.LimitedAccess,
         user: DataUser) {
        self.scope = scope
        self.user = user
        
        self.documentTypes = scope.getAvailableDocumentTypes(from: scope.supportedFileTypes)
        
        if let aadhaarScope = scope as? MainScope.LimitedAccess.LimitedAccessSeasonBoy {
            self.isSeasonBoy = true
            self.uploadButtonEnabled = SwiftDataSource(dataSource: aadhaarScope.isVerified)
        }
        else {
            self.isSeasonBoy = false
            self.uploadButtonEnabled = SwiftDataSource(dataSource: DataSource(initialValue: true))
        }
    }
    
    private func getBottomView(forGeometry geometry: GeometryProxy) -> some View {
        if scope.isDocumentUploaded { return AnyView(EmptyView()) }
        
        let buttonTextKey = isSeasonBoy ? "upload_aadhaar_card" : "upload_new_document"
        let filePickerOption: FilePickerOption = isSeasonBoy ? .documentPicker : .actionSheet
        
        return AnyView(
            VStack(spacing: 32) {
                if let aadhaarScope = scope as? MainScope.LimitedAccess.LimitedAccessSeasonBoy {
                    AadhaardCardDataFields(aadhaarData: aadhaarScope.aadhaarData,
                                           changeCard: aadhaarScope.changeCard,
                                           changeShareCode: aadhaarScope.changeShareCode)
                }
                
                MedicoButton(localizedStringKey: buttonTextKey,
                             isEnabled: self.uploadButtonEnabled.value == true) {
                    self.filePickerOption = filePickerOption
                }
            }
            .padding()
            .padding(.bottom, geometry.size.height * 0.1)
            .filePicker(filePickerOption: $filePickerOption,
                        forAvailableTypes: documentTypes,
                        uploadData: uploadData)
        )
    }
    
    private func uploadData(_ base64: String, withFileType fileType: DataFileType) {
        if let scope = self.scope as? MainScope.LimitedAccess.LimitedAccessSeasonBoy {
            scope.uploadAadhaar(base64: base64)
            
            return
        }
        
        if let scope = self.scope as? MainScope.LimitedAccess.LimitedAccessNonSeasonBoy {
            scope.uploadDrugLicense(base64: base64, fileType: fileType)
        }
    }
}
