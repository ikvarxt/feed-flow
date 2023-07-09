package com.prof18.feedflow.presentation

import com.prof18.feedflow.domain.feed.manager.FeedManagerRepository
import com.prof18.feedflow.domain.feed.retriever.FeedRetrieverRepository
import com.prof18.feedflow.domain.opml.OPMLInput
import com.prof18.feedflow.domain.opml.OPMLOutput
import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val feedManagerRepository: FeedManagerRepository,
    private val feedRetrieverRepository: FeedRetrieverRepository,
) : BaseViewModel() {

    private val isImportDoneMutableState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    @NativeCoroutinesState
    val isImportDoneState = isImportDoneMutableState.asStateFlow()

    private val isExportDoneMutableState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    @NativeCoroutinesState
    val isExportDoneState = isExportDoneMutableState.asStateFlow()

    fun importFeed(opmlInput: OPMLInput) {
        scope.launch {
            isImportDoneMutableState.update { false }
            // todo: add a try/catch?
            feedManagerRepository.addFeedsFromFile(opmlInput)
            isImportDoneMutableState.update { true }
            feedRetrieverRepository.fetchFeeds(updateLoadingInfo = false)
        }
    }

    fun exportFeed(opmlOutput: OPMLOutput) {
        scope.launch {
            isExportDoneMutableState.update { false }
            // todo: add a try/catch?
            feedManagerRepository.exportFeedsAsOpml(opmlOutput)
            isExportDoneMutableState.update { true }
        }
    }
}