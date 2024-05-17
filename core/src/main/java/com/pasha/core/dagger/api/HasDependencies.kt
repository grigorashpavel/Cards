package com.pasha.core.dagger.api


typealias DepsMap = Map<Class<out Dependencies>, @JvmSuppressWildcards Dependencies>

interface HasDependencies {
    val depsMap: DepsMap
}