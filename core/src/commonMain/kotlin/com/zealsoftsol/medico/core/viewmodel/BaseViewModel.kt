package com.zealsoftsol.medico.core.viewmodel

import com.zealsoftsol.medico.core.extensions.warnIt
import com.zealsoftsol.medico.core.interop.DataSource
import com.zealsoftsol.medico.data.AuthCredentials
import com.zealsoftsol.medico.data.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
    protected val uniqueJobs = hashMapOf<String, Job>()

    protected inline fun uniqueJob(key: String, block: CoroutineScope.() -> Job) {
        if (uniqueJobs[key]?.isActive == true) {
            "can't start the job with key $key, it is already in progress".warnIt()
        } else {
            uniqueJobs[key] = block()
        }
    }
}

interface AuthViewModelFacade {
    val credentials: DataSource<AuthCredentials>
    val state: DataSource<AuthState?>

    fun updateAuthCredentials(credentials: AuthCredentials)

    fun tryLogIn()

    fun logOut()

    fun clearState()
}