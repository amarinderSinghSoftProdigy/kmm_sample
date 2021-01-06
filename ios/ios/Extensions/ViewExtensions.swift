//
//  ViewExtensions.swift
//  ios
//
//  Created by Arnis on 18.11.2020.
//  Copyright © 2020 orgName. All rights reserved.
//

import SwiftUI
import core

extension View {
    
    @ViewBuilder func isHidden(_ hidden: Bool, remove: Bool = false) -> some View {
        if hidden {
            if !remove {
                self.hidden()
            }
        } else {
            self
        }
    }
    
    // MARK: Navigation bar
    func backButton(action: @escaping () -> ()) -> some View {
        let backButton = AnyView(
            Button(action: action) {
                HStack(spacing: 3) {
                    Image("Back")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                    
                    LocalizedText(localizationKey: "back",
                                  fontSize: 17)
                }
            }
        )
        
        return self.navigationBarBackButtonHidden(true)
            .navigationBarItems(leading: backButton)
    }
    
    // MARK: Alerts
    func alert(_ isPresented: Binding<Bool>,
               withTitleKey titleKey: String,
               withMessageKey messageKey: String,
               withButtonTextKey buttonTextKey: String) -> some View {
        return self.alert(isPresented: isPresented) {
            Alert(title: Text(LocalizedStringKey(titleKey)),
                  message: Text(LocalizedStringKey(messageKey)),
                  dismissButton: Alert.Button.default(Text(LocalizedStringKey(buttonTextKey))))
        }
    }
    
    func getAlert(withTitleKey titleKey: String,
                  withMessageKey messageKey: String,
                  withButtonTextKey buttonTextKey: String = "okay",
                  withButtonAction buttonAction: (() -> ())? = nil) -> Alert {
        Alert(title: Text(LocalizedStringKey(titleKey)),
              message: Text(LocalizedStringKey(messageKey)),
              dismissButton: Alert.Button.default(Text(LocalizedStringKey(buttonTextKey)),
                                                  action: buttonAction))
    }
    
    // MARK: Input Fields
    func hideKeyboardOnTap() -> some View {
        self.onTapGesture {
            UIApplication.shared.endEditing()
        }
    }
    
    // MARK: View Modifiers
    func errorAlert(withHandler errorsHandler: CommonScopeWithErrors) -> some View {
        self.modifier(ErrorAlert(errorsHandler: errorsHandler))
    }
    
    func notificationAlert(withHandler notificationsHandler: CommonScopeWithNotifications,
                           onDismiss: (() -> ())? = nil) -> some View {
        self.modifier(NotificationAlert(notificationsHandler: notificationsHandler,
                                        onDismiss: onDismiss))
    }
    
    func fieldError(withLocalizedKey errorMessageKey: String?,
                    withPadding padding: CGFloat = 16) -> some View {
        self.modifier(FieldError(errorMessageKey: errorMessageKey, padding: padding))
    }
    
    func scrollView(withInitialHeight initialHeight: CGFloat = 350) -> some View {
        self.modifier(ScrollViewModifier(initialInputFieldsHeight: initialHeight))
    }
    
    func userInfoNavigationBar(withScope scope: NavAndSearchMainScope,
                               withNavigationSection navigationSection: NavigationSection) -> some View {
        self.modifier(
            UserInfoNavigationBar(scope: scope,
                                  navigationSection: navigationSection)
        )
    }
    
    func filePicker(filePickerOption: Binding<FilePickerOption?>,
                    forAvailableTypes types: [String],
                    uploadData: @escaping (String, DataFileType) -> ()) -> some View {
        self.modifier(
            FilePicker(filePickerOption: filePickerOption,
                       documentTypes: types,
                       uploadData: uploadData)
        )
    }
    
    func testingIdentifier(_ id: String) -> some View {
        self.modifier(
            TestingIdentifier(identifier: id)
        )
    }
    
    func medicoText(textWeight: TextWeight = .regular,
                    fontSize: CGFloat = 14,
                    color: AppColor = .darkBlue,
                    multilineTextAlignment: TextAlignment = .center,
                    testingIdentifier: String? = nil) -> some View {
        let view = AnyView(
            self.modifier(
                MedicoText(textWeight: textWeight,
                           fontSize: fontSize,
                           color: color,
                           multilineTextAlignment: multilineTextAlignment)
            )
        )
        
        guard let testingIdentifier = testingIdentifier else { return view }
        
        return AnyView(
            view.testingIdentifier(testingIdentifier)
        )
    }
    
    func navigationBar(withNavigationBarContent content: AnyView) -> some View {
        self.modifier(
            NavigationBar(navigationBarContent: content)
        )
    }
}

