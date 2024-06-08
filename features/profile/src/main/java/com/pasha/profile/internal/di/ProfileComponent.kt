package com.pasha.profile.internal.di

import android.content.Context
import com.pasha.profile.api.ProfileDeps
import com.pasha.profile.api.ProfileFragment
import com.pasha.profile.internal.data.UploadService
import com.pasha.profile.internal.presentation.SettingsFragment
import dagger.BindsInstance
import dagger.Component


@Component(modules = [InternalProfileModule::class], dependencies = [ProfileDeps::class])
interface ProfileComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: ProfileDeps, @BindsInstance context: Context): ProfileComponent
    }

    fun inject(profileFragment: ProfileFragment)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(uploadService: UploadService)
}