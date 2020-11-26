package com.zealsoftsol.medico.core.mvi.viewmodel

abstract class BaseViewModel {
//    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.Default
//    protected val uniqueJobs = hashSetOf<String>()
//
//    // TODO iOS implementation does not guarantee uniqueness
//    protected inline fun uniqueJob(key: String, crossinline block: suspend () -> Unit) {
//        when (platform) {
//            Platform.Android -> {
//                if (!uniqueJobs.add(key)) {
//                    "can't start the job with key $key, it is already in progress".warnIt()
//                } else {
//                    launch(Dispatchers.Default) {
//                        block()
//                        uniqueJobs.remove(key)
//                    }
//                }
//            }
//            Platform.iOS -> launch(Dispatchers.Main) {
//                block()
//            }
//        }
//    }
}