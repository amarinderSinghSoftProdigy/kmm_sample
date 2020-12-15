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
                    
                    Text(LocalizedStringKey("back"))
                        .modifier(MedicoText(fontSize: 17))
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
    func errorAlert(withHandler errorsHandler: WithErrors) -> some View {
        self.modifier(ErrorAlert(errorsHandler: errorsHandler))
    }
    
    func notificationAlert(withHandler notificationsHandler: WithNotifications,
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
    
    func userInfoNavigationBar(isLimitedAppAccess: Bool,
                               forUser user: DataUser,
                               logOutAction: @escaping () -> ()) -> some View {
        self.modifier(
            UserInfoNavigationBar(isLimitedAppAccess: isLimitedAppAccess,
                                  user: user,
                                  logOutAction: logOutAction)
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
}
