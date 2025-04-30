package com.moare.android.core.mvi

import androidx.lifecycle.ViewModel

abstract class MVIViewModel<I, T> : ViewModel() {
    abstract fun send(intent: I)

    // STUDY: open은 선택적으로 override
    // 그냥 fun은 final이어서 override 불가능
    open fun initData(displayModel: T) {}
//    abstract fun initData(displayModel: T)

//    open val _displayModel = MutableStateFlow<T?>(null)
//    open val displayModel: StateFlow<T?> = _displayModel
}

//sealed class MVIIntent {
//    data class InitData<T>(val displayModel: T) : MVIIntent()
//}