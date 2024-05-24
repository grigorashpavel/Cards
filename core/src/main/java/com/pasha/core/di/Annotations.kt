package com.pasha.core.di

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
annotation class DependeciesKey(val value: KClass<out Dependencies>)
