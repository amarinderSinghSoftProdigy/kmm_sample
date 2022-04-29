package com.zealsoftsol.medico.core.mvi.scope.nested

import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.core.mvi.scope.Scopable
import com.zealsoftsol.medico.data.CartData

interface ToastScope : Scopable {
    val showToast: DataSource<Boolean>
    val cartData: DataSource<CartData?>
}