package com.ericwathome.stovetime.core.common.helpers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

interface ApplicationScope : CoroutineScope

internal class ApplicationScopeImpl(
    dispatchers: StovetimeDispatchers,
) : ApplicationScope {
    override val coroutineContext: CoroutineContext = SupervisorJob() + dispatchers.mainImmediate
}
