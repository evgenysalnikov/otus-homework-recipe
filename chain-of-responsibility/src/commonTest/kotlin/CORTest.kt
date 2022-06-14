package com.salnikoff.recipe.chain

import com.salnikoff.recipe.chain.handlers.chain
import com.salnikoff.recipe.chain.handlers.parallel
import com.salnikoff.recipe.chain.handlers.worker
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

enum class CorStatuses {
    NONE,
    RUNNING,
    FAILING,
    DONE,
    ERROR
}

data class TestContext(
    var status: CorStatuses = CorStatuses.NONE,
    var someString: String = "",
    var some: Int = 13
)

class CORTest {

    companion object {
        val chain = rootChain<TestContext> {
            worker {
                title = "Init status"
                description = "Change status from NONE to RUNNING"
                on { status == CorStatuses.NONE }
                handle { someString = "value"; status = CorStatuses.RUNNING }
                except { status = CorStatuses.ERROR }
            }
            worker(
                title = "Another constructor",
                description = "Set value"
            ) {
                someString = "new value"
            }
            chain {
                on { status == CorStatuses.RUNNING }

                worker(
                    title = "String update",
                    description = "String template usage"
                ) {
                    someString = "$someString!!!"
                }

            }
            parallel {
                on {
                    some < 15
                }

                worker {
                    title = "Increment if"
                    description = "Increment some if condition"
                    on { some < 15 }
                    handle { some++ }
                    except { status = CorStatuses.ERROR }
                }

                worker {
                    title = "Increment if"
                    description = "Increment some if condition"
                    on { some < 15 }
                    handle { some++ }
                    except { status = CorStatuses.ERROR }
                }

                worker {
                    title = "Increment if"
                    description = "Increment some if condition"
                    on { some < 15 }
                    handle { some++ }
                    except { status = CorStatuses.ERROR }
                }

            }

        }.build()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun chainTest() = runTest {
        val ctx = TestContext(someString = "testing")
        val chain = CORTest.chain

        chain.exec(ctx)
        assertEquals(CorStatuses.RUNNING, ctx.status)
        assertEquals(15, ctx.some)

    }
}


private fun ICorChainDsl<TestContext>.printResult() = worker(title = "Print example", description = "Print") {
    println("some = $some")
}
