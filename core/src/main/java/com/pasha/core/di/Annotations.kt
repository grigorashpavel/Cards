package com.pasha.core.di

import dagger.MapKey
import kotlin.reflect.KClass


@MapKey
annotation class DependeciesKey(val value: KClass<out Dependencies>)