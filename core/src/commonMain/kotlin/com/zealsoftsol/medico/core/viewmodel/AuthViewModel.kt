package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.extensions.logIt
import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.interop.currentThread
import com.zealsoftsol.medico.core.repository.UserRepo
import com.zealsoftsol.medico.data.TestData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class AuthViewModel(private val userRepo: UserRepo) : BaseViewModel(), AuthViewModelFacade {
    override val testData: DataSource<TestData> = DataSource(TestData("empty"))

    override fun asyncTest() {
        launch {
            "launch in $currentThread".logIt()
            delay(500)
            "set async".warnIt()
            testData.value = TestData("async")
        }
    }
}