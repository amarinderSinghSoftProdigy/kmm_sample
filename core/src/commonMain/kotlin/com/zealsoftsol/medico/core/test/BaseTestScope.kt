package com.zealsoftsol.medico.core.test

import com.zealsoftsol.medico.core.directDI
import com.zealsoftsol.medico.core.mvi.Navigator
import org.kodein.di.instance

/**
 * All scopes that need to be used in UI testing have to be defined in com.zealsoftsol.medico.core.test package.
 * Naming convention for classes is "[name of the scope]TestScope".
 * E.g. if target top level scope is OtpScope, test scope class should be named OtpTestScope.
 * Such classes should extend BaseTestScope.
 * To set a scope define a function in that class.
 * This functions requirements:
 *  - it has to be inline (inline fun)
 *  - the name of the function has to be the same as target final scope (e.g. inline fun phoneNumberInput() for OtpScope.PhoneNumberInput)
 *  - it should have the same amount of parameters as in the target scope
 *  - no default parameters are allowed
 *  - if the parameter in target scope is a [DataSource] the function parameter should be just the underlying type of the [DataSource]
 *  Create a scope and call nav.setCurrentScope with created scope as parameter.
 */
abstract class BaseTestScope {
    val nav: Navigator = directDI.instance()
}