package com.salnikoff.recipe.chain.handlers

import com.salnikoff.recipe.chain.ICorWorker

class CorWorker<T>(
    override val title: String,
    override val description: String = "",
    val blockOn: suspend T.() -> Boolean = { true },
    val blockExcept: suspend T.(Throwable) -> Unit = {},
    val blockHandle: suspend T.() -> Unit = {},
) : ICorWorker<T> {
    override suspend fun on(context: T): Boolean = blockOn(context)

    override suspend fun handle(context: T) = blockHandle(context)

    override suspend fun except(context: T, e: Throwable) = blockExcept(context, e)
}
