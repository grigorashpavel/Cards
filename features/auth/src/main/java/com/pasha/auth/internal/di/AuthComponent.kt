package com.pasha.auth.internal.di

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.pasha.auth.api.AuthDeps
import com.pasha.auth.api.AuthNavCommandProvider
import com.pasha.auth.api.SignInFragment
import com.pasha.auth.internal.presentation.SignUpConfirmFragment
import com.pasha.auth.internal.presentation.SignUpFragment
import dagger.Component

@[Component(
    modules = [InternalAuthModule::class],
    dependencies = [AuthDeps::class]
)]
internal interface AuthComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: AuthDeps): AuthComponent
    }

    fun inject(fragment: SignInFragment)
    fun inject(fragment: SignUpFragment)
    fun inject(fragment: SignUpConfirmFragment)
}