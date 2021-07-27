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
    private var availableDocumentTypes: [String]!
    
    let scope: SignUpScope.LegalDocuments
    
    @ObservedObject var canGoNext: SwiftDataSource<KotlinBoolean>
    
    @State private var safariLink: String?
    
    var body: some View {
        self.getView()
            .screenLogger(withScreenName: "SignUpLegalDocumentsScreen",
                          withScreenClass: SignUpLegalDocumentsScreen.self)
    }
    
    init(scope: SignUpScope.LegalDocuments) {
        self.scope = scope

        self.canGoNext = SwiftDataSource(dataSource: scope.canGoNext)
        
        defer {
            self.availableDocumentTypes = self.getAvailableDocumentTypes(from: scope.supportedFileTypes)
        }
    }
    
    private func getView() -> some View {
        let documentRequestTextKey: String
        let buttonTextKey: String
        let showsDownloadNote: Bool
        
        switch scope {

        case is SignUpScope.LegalDocuments.LegalDocumentsAadhaar:
            documentRequestTextKey = "aadhaar_card_request"
            buttonTextKey = "upload_aadhaar_card"
            showsDownloadNote = true

        case is SignUpScope.LegalDocuments.LegalDocumentsDrugLicense:
            documentRequestTextKey = "drug_license_request"
            buttonTextKey = "upload_new_document"
            showsDownloadNote = false
            
        default:
            documentRequestTextKey = ""
            buttonTextKey = ""
            showsDownloadNote = false
        }
        
        return AnyView(
            VStack(spacing: 20) {
                Spacer()
                
                VStack(spacing: 28) {
                    Image("UploadDocuments")
                    
                    LocalizedText(localizationKey: documentRequestTextKey,
                                  textWeight: .medium,
                                  fontSize: 16,
                                  color: .grey1)
                    
                    if showsDownloadNote {
                        VStack(spacing: 6) {
                            LocalizedText(localizationKey: "download_note",
                                          fontSize: 16,
                                          color: .grey1)
                            
                            let link = "https://resident.uidai.gov.in/offline-kyc"
                            Text(link)
                                .underline()
                                .medicoText(fontSize: 16,
                                            color: .lightBlue)
                                .onTapGesture {
                                    self.safariLink = link
                                }
                        }
                    }
                }
                
                VStack {
                    LocalizedText(localizationKey: "available_file_formats",
                                  textWeight: .semiBold,
                                  fontSize: 16,
                                  color: .grey2)
                    
                    HStack {
                        ForEach(self.availableDocumentTypes, id: \.self) {
                            Text($0)
                                .medicoText(textWeight: .medium)
                                .padding(.vertical, 4)
                                .padding(.horizontal, 8)
                                .background(AppColor.darkBlue.color
                                                .opacity(0.1)
                                                .cornerRadius(4))
                        }
                    }
                    
                    HStack(spacing: 3) {
                        LocalizedText(localizationKey: "maximum_file_size",
                                      fontSize: 16,
                                      color: .grey2)
                        
                        Text("10 MB")
                            .medicoText(textWeight: .semiBold,
                                        fontSize: 16)
                    }
                }
                .padding(.horizontal, 18)
                .padding(.vertical, 23)
                .background(RoundedRectangle(cornerRadius: 8)
                                .stroke(AppColor.darkBlue.color
                                            .opacity(0.12)))
                
                Spacer()
            }
            .padding()
            .modifier(SignUpButton(isEnabled: canGoNext.value != false,
                                   buttonTextKey: buttonTextKey,
                                   skipButtonAction: { scope.skip() },
                                   action: { scope.showBottomSheet() }))
            .safariViewModifier(link: $safariLink)
        )
    }
    
    private func getAvailableDocumentTypes(from fileTypes: KotlinArray<DataFileType>) -> [String] {
        var documentTypes = [String]()
        
        let iterator = fileTypes.iterator()
        while iterator.hasNext() {
            guard let fileType = iterator.next_() as? DataFileType,
                  fileType.isMandatory,
                  let formattedType = fileType.getFormattedString() else { continue }
            
            documentTypes.append(formattedType)
        }
        
        return documentTypes
    }
}
