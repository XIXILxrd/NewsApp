package com.example.news.data

import com.example.news.data.RequestResult.Error
import com.example.news.data.RequestResult.Success
import com.example.news.data.RequestResult.Loading


interface MergeStrategy<T> {
    fun merge(right: T, left: T): T
}

internal class RequestResponseMergeStrategy<T : Any> : MergeStrategy<RequestResult<T>> {
    @Suppress("CyclomaticComplexMethod")
    override fun merge(
        right: RequestResult<T>,
        left: RequestResult<T>
    ): RequestResult<T> {
        return when {
            right is Loading && left is Loading -> merge(right, left)
            right is Success && left is Loading -> merge(right, left)
            right is Loading && left is Success -> merge(right, left)
            right is Success && left is Success -> merge(right, left)
            right is Success && left is Error -> merge(right, left)
            right is Loading && left is Error -> merge(right, left)
            right is Error && left is Loading -> merge(right, left)
            right is Error && left is Success -> merge(right, left)

            else -> error("Unimplemented branch right=$right & left=$left")
        }
    }

    private fun merge(
        cache: Loading<T>,
        server: Loading<T>
    ): RequestResult<T> {
        return Loading(data = server.data ?: cache.data)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: Success<T>,
        server: Loading<T>
    ): RequestResult<T> {
        return Loading(cache.data)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: Loading<T>,
        server: Success<T>
    ): RequestResult<T> {
        return Loading(server.data)
    }

    private fun merge(
        cache: Success<T>,
        server: Error<T>
    ): RequestResult<T> {
        return Error(data = cache.data, error = server.error)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: Success<T>,
        server: Success<T>
    ): RequestResult<T> {
        return Success(data = server.data)
    }

    private fun merge(
        cache: Loading<T>,
        server: Error<T>
    ): RequestResult<T> {
        return Error(data = server.data ?: cache.data, error = server.error)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: Error<T>,
        server: Loading<T>
    ): RequestResult<T> {
        return server
    }

    @Suppress("UNUSED_PARAMETER")
    private fun merge(
        cache: Error<T>,
        server: Success<T>
    ): RequestResult<T> {
        return server
    }
}