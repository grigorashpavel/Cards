package com.pasha.core.account

import androidx.core.app.AppComponentFactory
import dagger.Component

@Component(dependencies = [AccountDeps::class])
interface AccountComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: AccountDeps): AccountComponent
    }

    fun inject(authenticatorService: AuthenticatorService)
}