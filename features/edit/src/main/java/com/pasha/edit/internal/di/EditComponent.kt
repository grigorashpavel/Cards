package com.pasha.edit.internal.di

import android.content.Context
import com.pasha.edit.api.EditCardFragment
import com.pasha.edit.api.EditDeps
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [EditDeps::class], modules = [InternalEditModule::class])
interface EditComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: EditDeps, @BindsInstance context: Context): EditComponent
    }

    fun inject(editFragment: EditCardFragment)
}