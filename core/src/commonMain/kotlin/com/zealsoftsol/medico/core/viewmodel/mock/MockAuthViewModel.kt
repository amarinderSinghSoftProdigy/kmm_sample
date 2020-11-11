package com.zealsoftsol.medico.core.viewmodel.mock

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.viewmodel.AuthViewModelFacade
import com.zealsoftsol.medico.data.TestData


class MockAuthViewModel : AuthViewModelFacade {
    override val testData: DataSource<TestData> = DataSource(TestData("test"))

    override fun asyncTest() {

    }
}