package com.moare.android.core.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseStore<A>() {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    abstract fun send(action: A)

    open fun initData() {
    }

    open fun dispose() {
        scope.cancel()
    }
}