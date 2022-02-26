package com.nagarro.currency.domain.util.extension

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import com.nagarro.currency.domain.model.Result
import kotlinx.coroutines.flow.onEach

fun <T> resultFlow(block: suspend () -> T): Flow<Result<T>> {
    return flow {
        emit(Result.Loading)
        try {
            Log.d("ConvertViewModel", "entered resultFlow")
            emit(Result.Success(block()))
        } catch (exception: Exception) {
            emit(Result.Error(exception))
        }
    }
}

fun <T> Flow<Result<T>>.onSuccess(action: suspend (data: T) -> Unit): Flow<Result<T>> =
    onEach { result ->
        if (result is Result.Success)
            action(result.data)
    }

fun <T> Flow<Result<T>>.onError(action: suspend (Result.Error) -> Unit): Flow<Result<T>> =
    onEach { result ->
        if (result is Result.Error)
            action(result)
    }

fun <T> Flow<Result<T>>.onLoading(action: suspend () -> Unit): Flow<Result<T>> = onEach { result ->
    if (result is Result.Loading)
        action()
}
