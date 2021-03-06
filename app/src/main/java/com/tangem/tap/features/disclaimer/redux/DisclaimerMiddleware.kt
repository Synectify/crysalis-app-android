package com.tangem.tap.features.disclaimer.redux

import com.tangem.tap.common.redux.AppState
import com.tangem.tap.common.redux.navigation.AppScreen
import com.tangem.tap.common.redux.navigation.NavigationAction
import com.tangem.tap.preferencesStorage
import com.tangem.tap.store
import org.rekotlin.Middleware

class DisclaimerMiddleware {
    val disclaimerMiddleware: Middleware<AppState> = { dispatch, state ->
        { next ->
            { action ->
                when (action) {
                    is DisclaimerAction.AcceptDisclaimer -> {
                        preferencesStorage.saveDisclaimerAccepted()
                        if (store.state.walletState.twinCardsState != null) {
                            val showOnboarding = !preferencesStorage.wasTwinsOnboardingShown()
                            if (showOnboarding) {
                                store.dispatch(NavigationAction.NavigateTo(AppScreen.TwinsOnboarding))
                            } else {
                                store.dispatch(NavigationAction.NavigateTo(AppScreen.Wallet))
                            }
                        } else {
                            store.dispatch(NavigationAction.NavigateTo(AppScreen.Wallet))
                        }
                    }
                }
                next(action)
            }
        }
    }
}