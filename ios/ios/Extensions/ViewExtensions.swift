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
    
    // MARK: Corners
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
    
    // MARK: Input Fields
    func hideKeyboardOnTap() -> some View {
        self.onTapGesture {
            self.hideKeyboard()
        }
    }
    
    func hideKeyboard() {
        UIApplication.shared.endEditing()
    }
    
    // MARK: View Modifiers
    func errorAlert(withHandler errorsHandler: Scope.Host) -> some View {
        self.modifier(ErrorAlert(errorsHandler: errorsHandler))
    }
    
    func notificationAlertListener(withHandler notificationsHandler: CommonScopeWithNotifications) -> some View {
        self.modifier(NotificationAlert(notificationsHandler: notificationsHandler))
    }
    
    func notificationAlertSender(withHandler notificationsHandler: CommonScopeWithNotifications) -> some View {
        self.modifier(NotificationAlertSender(notificationsHandler: notificationsHandler))
    }
    
    func fieldError(withLocalizedKey errorMessageKey: String?,
                    withPadding padding: CGFloat = 16) -> some View {
        self.modifier(FieldError(errorMessageKey: errorMessageKey, padding: padding))
    }
    
    func scrollView(withInitialHeight initialHeight: CGFloat = 350) -> some View {
        self.modifier(ScrollViewModifier(initialInputFieldsHeight: initialHeight))
    }
    
    func slidingNavigationPanelView(withNavigationSection navigationSection: NavigationSection,
                                    showsSlidingPanel: Bool,
                                    closeSlidingPanel: @escaping (Bool) -> ()) -> some View {
        self.modifier(
            SlidingNavigationPanelView(navigationSection: navigationSection,
                                       showsSlidingPanel: showsSlidingPanel,
                                       closeSlidingPanel: closeSlidingPanel)
        )
    }
    
    func filePicker(bottomSheet: BottomSheet.UploadDocuments,
                    onBottomSheetDismiss: @escaping () -> ()) -> some View {
        self.modifier(
            FilePicker(bottomSheet: bottomSheet,
                       onBottomSheetDismiss: onBottomSheetDismiss)
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
    
    func navigationBar(withNavigationSection navigationSection: DataSource<NavigationSection>,
                       withNavigationBarInfo navigationBarInfo: DataSource<TabBarInfo>,
                       handleGoBack: @escaping () -> ()) -> some View {
        self.modifier(
            NavigationBar(navigationSection: navigationSection,
                          navigationBarInfo: navigationBarInfo,
                          handleGoBack: handleGoBack)
        )
    }
    
    func navigationBar(withNavigationBarContent navigationBarContent: AnyView) -> some View {
        self.modifier(
            NavigationBar(navigationBarContent: navigationBarContent)
        )
    }
    
    func screenLogger<T>(withScreenName screenName: String,
                         withScreenClass screenClass: T.Type) -> some View {
        self.modifier(
            ScreenLoggerModifier(screenName: screenName,
                                 screenClass: screenClass)
        )
    }
    
    func textFieldsModifiers() -> some View {
        self
            .keyboardResponder()
            .hideKeyboardOnTap()
    }
}

private struct RoundedCorner: Shape {
    var radius: CGFloat = .infinity
    var corners: UIRectCorner = .allCorners

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(roundedRect: rect,
                                byRoundingCorners: corners,
                                cornerRadii: CGSize(width: radius, height: radius))
        return Path(path.cgPath)
    }
}
