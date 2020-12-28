package com.zealsoftsol.medico.core.mvi.scope

interface CommonScope {
    interface PhoneVerificationEntryPoint : CommonScope
    interface UploadDocument : CommonScope, WithErrors
}