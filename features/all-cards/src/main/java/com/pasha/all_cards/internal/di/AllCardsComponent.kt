package com.pasha.all_cards.internal.di

import com.pasha.all_cards.api.AllCardsFragment
import com.pasha.all_cards.api.AllCardsDeps
import com.pasha.all_cards.internal.presentation.ViewerFragment
import dagger.Component


@Component(dependencies = [AllCardsDeps::class], modules = [InternalAllCardsModule::class])
interface AllCardsComponent {
    @Component.Factory
    interface Factory {
        fun create(deps: AllCardsDeps): AllCardsComponent
    }

    fun inject(cardsFragment: AllCardsFragment)
    fun inject(viewerFragment: ViewerFragment)
}