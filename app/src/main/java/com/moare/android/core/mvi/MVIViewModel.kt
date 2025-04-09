package com.moare.android.core.mvi

import androidx.lifecycle.ViewModel

abstract class MVIViewModel<I, T> : ViewModel() {
    abstract fun send(intent: I)

    open fun initData(displayModel: T) {}
//    abstract fun initData(displayModel: T)

//    open val _displayModel = MutableStateFlow<T?>(null)
//    open val displayModel: StateFlow<T?> = _displayModel
}