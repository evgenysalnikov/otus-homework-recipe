package com.salnikoff.recipe.chain.handlers

import com.salnikoff.recipe.chain.ICorChainDsl
import com.salnikoff.recipe.chain.ICorExec
import com.salnikoff.recipe.chain.ICorExecDsl

@ChainOfResponsibilityDsl
fun <T> CorChainDsl<T>.parallel(function: CorParallelDsl<T>.() -> Unit) {
    add(
        CorParallelDsl<T>().apply(function)
    )
}

@ChainOfResponsibilityDsl
class CorParallelDsl<T>(
    override var title: String = "",
    override var description: String = "",
    private val workers: MutableList<ICorExecDsl<T>> = mutableListOf(),
    private var blockOn: suspend T.() -> Boolean = { true },
    private var blockExcept: suspend T.(e: Throwable) -> Unit = { e: Throwable -> throw e },
) : ICorChainDsl<T> {
    override fun build(): ICorExec<T> = CorParallel<T>(
        title = title,
        description = description,
        blockOn = blockOn,
        blockExcept = blockExcept,
        execs = workers.map { it.build() }.toList()
    )

    override fun on(function: suspend T.() -> Boolean) {
        blockOn = function
    }

    override fun except(function: suspend T.(Throwable) -> Unit) {
        blockExcept = function
    }

    override fun add(worker: ICorExecDsl<T>) {
        workers.add(worker)
    }
}
