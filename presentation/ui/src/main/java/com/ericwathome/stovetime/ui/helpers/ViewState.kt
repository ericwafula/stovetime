package com.ericwathome.stovetime.ui.helpers

import com.ericwathome.stovetime.core.common.result.DataError

sealed interface ViewState<out T> {
    data object Initial : ViewState<Nothing>

    data object Loading : ViewState<Nothing>

    data class Success<T>(
        val data: T,
    ) : ViewState<T>

    data class Error(
        val error: DataError,
    ) : ViewState<Nothing>
}

fun <T> ViewState<T>.dataOrNull(): T? = (this as? ViewState.Success)?.data
