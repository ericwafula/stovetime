package com.ericwathome.stovetime.core.common.di

import com.ericwathome.stovetime.core.common.helpers.ApplicationScope
import com.ericwathome.stovetime.core.common.helpers.ApplicationScopeImpl
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchers
import com.ericwathome.stovetime.core.common.helpers.StovetimeDispatchersImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val CommonModule =
    module {
        singleOf(::StovetimeDispatchersImpl) {
            bind<StovetimeDispatchers>()
        }
        singleOf(::ApplicationScopeImpl) {
            bind<ApplicationScope>()
        }
    }
