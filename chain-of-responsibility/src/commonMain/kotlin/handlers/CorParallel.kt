package com.salnikoff.recipe.chain.handlers

import com.salnikoff.recipe.chain.ICorExec
import com.salnikoff.recipe.chain.ICorWorker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class CorParallel<T>(
    private val execs: List<ICorExec<T>>,
    override val title: String,
    override val description: String,
    private val blockOn: suspend T.() -> Boolean = { true },
    private val blockExcept: suspend T.(Throwable) -> Unit = {},
) : ICorWorker<T> {
    override suspend fun on(context: T): Boolean = blockOn(context)

    override suspend fun handle(context: T) = coroutineScope {
        execs.map { launch { it.exec(context) } }.toList().forEach { it.join() }
    }

    override suspend fun except(context: T, e: Throwable) = blockExcept(context, e)
}
