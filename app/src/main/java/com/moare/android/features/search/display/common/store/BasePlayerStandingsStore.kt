package com.moare.android.features.search.display.common.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BasePlayerStandingsStore<A, R, D: SportDisplayModel>(
    val responseModel: R,
    private val initial: D,
    private val nameProvider: TranslatedNameProvider
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected val _displayModel = MutableStateFlow(initial)
    val displayModel: StateFlow<D> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    protected val _playerNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val playerNameDic: StateFlow<Map<String, String>> = _playerNameDic

    protected val _teamNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val teamNameDic: StateFlow<Map<String, String>> = _teamNameDic

    protected var _categorySelectedIndex = MutableStateFlow(0)
    val categorySelectedIndex: StateFlow<Int> = _categorySelectedIndex

    protected var _entityIndex = MutableStateFlow<Int?>(null)
    val entityIndex: StateFlow<Int?> = _entityIndex

    protected var _filteredStandingsStartIndex = MutableStateFlow(0)
    val filteredStandingsStartIndex: StateFlow<Int> = _filteredStandingsStartIndex

    var shouldScrollCategory = true
    protected var selectedEntity: EntityInfo? = null
    protected var filteredStandingsEndIndex = 0 // NOTE: one bigger then actual showing end item's index. Because of subList.

    abstract fun send(action: A)

    open fun initData() {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle

        _categorySelectedIndex.value = 0
        _entityIndex.value = null
        _filteredStandingsStartIndex.value = 0

        shouldScrollCategory = true
        selectedEntity = null
        filteredStandingsEndIndex = 0

        loadDictionaries(displayModel.value.leagueId)

        val keywords = displayModel.value.keywords
        if (keywords.isNotEmpty()) {
            // Check matching keyword in the order of categories, doesn't matter what keyword is in keywords
            val index = StringConstants.Football.PLAYER_STANDINGS_SECOND_CATEGORIES.indexOfFirst { category ->
                val keyword = keywords.find { it.keyword == category }
                keyword != null
            }

            if (index != -1) {
                _categorySelectedIndex.value = index
            }
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)

        when (leagueId) {
            Constants.Ids.EPL -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.EPL_PLAYER_DIC)
            }
            Constants.Ids.LALIGA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.LALIGA_PLAYER_DIC)
            }
            Constants.Ids.BUNDESLIGA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_PLAYER_DIC)
            }
            Constants.Ids.LIGUE1 -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.LIGUE1_PLAYER_DIC)
            }
            Constants.Ids.SERIEA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.SERIEA_PLAYER_DIC)
            }
            Constants.Ids.MLS -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.MLS_PLAYER_DIC)
            }
            Constants.Ids.NBA -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_PLAYER_DIC)
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                _playerNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_PLAYER_DIC)
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectCategory(index: Int, category: String) {
        shouldScrollCategory = false
        _categorySelectedIndex.value = index
    }

    open fun fetchStandings(category: String) {
        _displayDataState.value = ApiFetchState.Fetching
    }

    abstract fun filterStandings()
    abstract fun addStandings(isUp: Boolean)

    open fun dispose() {
        scope.cancel()
    }
}