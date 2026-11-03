package com.ericwathome.stovetime.testing

import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler

class TestStovetimeDispatchers(
    scheduler: TestCoroutineScheduler,
) : StovetimeDispatchers {
    private val dispatcher = StandardTestDispatcher(scheduler)
    override val main: CoroutineDispatcher = dispatcher
    override val mainImmediate: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
}
