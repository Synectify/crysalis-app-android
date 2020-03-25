package com.tangem.tangemtest.cardUseCase.domain.paramsManager.managers

import com.tangem.CardManager
import com.tangem.common.tlv.TlvTag
import com.tangem.tangemtest.cardUseCase.domain.actions.DepesonalizeAction
import com.tangem.tangemtest.cardUseCase.domain.paramsManager.ActionCallback
import com.tangem.tangemtest.cardUseCase.domain.paramsManager.BaseParamsManager
import com.tangem.tangemtest.cardUseCase.domain.paramsManager.IncomingParameter

class DepersonalizeParamsManager : BaseParamsManager(DepesonalizeAction()) {
    override fun createParamsList(): List<IncomingParameter> {
        return listOf(IncomingParameter(TlvTag.CardId, null))
    }

    override fun invokeMainAction(cardManager: CardManager, callback: ActionCallback) {
        action.executeMainAction(getAttrsForAction(cardManager), callback)
    }

    override fun getActionByTag(tag: TlvTag, cardManager: CardManager): ((ActionCallback) -> Unit)? {
        return action.getActionByTag(tag, getAttrsForAction(cardManager))
    }
}