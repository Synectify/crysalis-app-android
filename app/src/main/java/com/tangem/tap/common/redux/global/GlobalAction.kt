package com.tangem.tap.common.redux.global

import com.tangem.common.CompletionResult
import com.tangem.tap.domain.configurable.config.ConfigManager
import com.tangem.tap.domain.configurable.warningMessage.WarningMessage
import com.tangem.tap.domain.configurable.warningMessage.WarningMessagesManager
import com.tangem.tap.domain.tasks.ScanNoteResponse
import com.tangem.tap.features.details.redux.SecurityOption
import com.tangem.tap.features.feedback.EmailData
import com.tangem.tap.features.feedback.FeedbackManager
import org.rekotlin.Action

sealed class GlobalAction : Action {

    object ScanFailsCounter {
        data class ChooseBehavior(val result: CompletionResult<ScanNoteResponse>) : GlobalAction()
        object Reset : GlobalAction()
        object Increment : GlobalAction()
    }

    data class SaveScanNoteResponse(val scanNoteResponse: ScanNoteResponse) : GlobalAction()
    data class ChangeAppCurrency(val appCurrency: FiatCurrencyName) : GlobalAction()
    object RestoreAppCurrency : GlobalAction() {
        data class Success(val appCurrency: FiatCurrencyName) : GlobalAction()
    }

    data class UpdateWalletSignedHashes(val walletSignedHashes: Int?) : GlobalAction()
    data class HideWarningMessage(val warning: WarningMessage) : GlobalAction()
    data class UpdateSecurityOptions(val securityOption: SecurityOption) : GlobalAction()

    data class SetConfigManager(val configManager: ConfigManager) : GlobalAction()
    data class SetWarningManager(val warningManager: WarningMessagesManager) : GlobalAction()
    data class SetFeedbackManager(val feedbackManager: FeedbackManager) : GlobalAction()

    data class SendFeedback(val emailData: EmailData) : GlobalAction()
}