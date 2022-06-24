package com.salnikoff.recipe.chain

import com.salnikoff.recipe.chain.handlers.ChainOfResponsibilityDsl
import com.salnikoff.recipe.chain.handlers.CorChainDsl

interface ICorExec<T> {
    suspend fun exec(context: T)
}

interface ICorExecDsl<T> {
    var title: String
    var description: String
    fun build(): ICorExec<T>
}

interface ICorHandlerDsl<T> {
    fun on(function: suspend T.() -> Boolean)
    fun except(function: suspend T.(Throwable) -> Unit)
}

interface ICorChainDsl<T> : ICorExecDsl<T>, ICorHandlerDsl<T> {
    fun add(worker: ICorExecDsl<T>)
}

interface ICorWorkerDsl<T>: ICorExecDsl<T>, ICorHandlerDsl<T> {
    fun handle(function: suspend T.() -> Unit)
}

interface ICorWorker<T>: ICorExec<T> {
    val title: String
    val description: String
    suspend fun on(context: T): Boolean
    suspend fun handle(context: T)
    suspend fun except(context: T, e: Throwable)

    override suspend fun exec(context: T) {
        if (on(context)) {
            try {
                handle(context)
            } catch (e: Throwable) {
                except(context, e)
            }
        }
    }
}

@ChainOfResponsibilityDsl
fun <T> rootChain(function: CorChainDsl<T>.() -> Unit) = CorChainDsl<T>().apply(function)
