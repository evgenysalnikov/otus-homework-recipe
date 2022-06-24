package com.salnikoff.recipe.chain.handlers

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.ICorExec
import com.salnikoff.recipe.chain.ICorWorkerDsl

@ChainOfResponsibilityDsl
fun <T> ICorChainDsl<T>.worker(function: CorWorkerDsl<T>.() -> Unit) {
    add(CorWorkerDsl<T>().apply(function))
}

@ChainOfResponsibilityDsl
fun <T> ICorChainDsl<T>.worker(
    title: String,
    description: String,
    function: suspend T.() -> Unit
) {
    add(
        CorWorkerDsl<T>().apply {
            this.title = title
            this.description = description
            this.handle(function)
        }
    )
}

class CorWorkerDsl<T>(
    override var title: String = "",
    override var description: String = "",
    var blockOn: suspend T.() -> Boolean = { true },
    var blockExcept: suspend T.(Throwable) -> Unit = {},
    var blockHandle: suspend T.() -> Unit = {}
) : ICorWorkerDsl<T> {

    override fun build(): ICorExec<T> = CorWorker<T>(
        title = title,
        description = description,
        blockOn = blockOn,
        blockExcept = blockExcept,
        blockHandle = blockHandle
    )

    override fun on(function: suspend T.() -> Boolean) {
        blockOn = function
    }

    override fun except(function: suspend T.(Throwable) -> Unit) {
        blockExcept = function
    }

    override fun handle(function: suspend T.() -> Unit) {
        blockHandle = function
    }
}
