package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.ApiFetchState
import com.moare.android.features.search.models.EntityInfo
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BasePlayerStandingsViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<I, T>() {
    /* ---------------------
       constants
       --------------------- */

    /* ---------------------
       data state
       --------------------- */
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    protected val _displayDataState = MutableStateFlow<ApiFetchState>(ApiFetchState.Idle)
    val displayDataState: StateFlow<ApiFetchState> = _displayDataState

    /* ---------------------
       ui state
       --------------------- */
    protected var _firstSelectedIndex = MutableStateFlow(0)
    val firstSelectedIndex: StateFlow<Int> = _firstSelectedIndex

    protected var _secondCategorySelectedIndex = MutableStateFlow(0)
    val secondCategorySelectedIndex: StateFlow<Int> = _secondCategorySelectedIndex

    protected var _isKeyword = MutableStateFlow(false)
    val isKeyword: StateFlow<Boolean> = _isKeyword

    protected var _entityIndex = MutableStateFlow<Int?>(null)
    val entityIndex: StateFlow<Int?> = _entityIndex

    protected var _filteredStandingsStartIndex = MutableStateFlow(0)
    val filteredStandingsStartIndex: StateFlow<Int> = _filteredStandingsStartIndex

    /* ---------------------
       etc
       --------------------- */
    var playerNameDictionary: Map<String, String> = emptyMap()
    var teamNameDictionary: Map<String, String> = emptyMap()
    var shouldScrollCategory = true
    protected var selectedEntity: EntityInfo? = null
    protected var filteredStandingsEndIndex = 0 // NOTE: one bigger then actual showing end item's index. Because of subList.

    override fun initData(displayModel: T) {
        // init with default value
        _displayDataState.value = ApiFetchState.Idle

        _firstSelectedIndex.value = 0
        _secondCategorySelectedIndex.value = 0
        _isKeyword.value = false
        _entityIndex.value = null
        _filteredStandingsStartIndex.value = 0

        shouldScrollCategory = true
        selectedEntity = null
        filteredStandingsEndIndex = 0

        // init data
        _displayModel.value = displayModel

        if (displayModel is SportDisplayModel) {
            loadDictionaries(displayModel.leagueId)

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                // Check matching keyword in the order of categories, doesn't matter what keyword is in keywords
                val index = StringConstants.Football.PLAYER_STANDINGS_SECOND_CATEGORIES.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _secondCategorySelectedIndex.value = index
                    _isKeyword.value = true
                }
            }
        }

    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
            Constants.Ids.EPL -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.EPL_TEAM_DIC)
            }
            Constants.Ids.LALIGA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LALIGA_TEAM_DIC)
            }
            Constants.Ids.BUNDESLIGA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.BUNDESLIGA_TEAM_DIC)
            }
            Constants.Ids.LIGUE1 -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.LIGUE1_TEAM_DIC)
            }
            Constants.Ids.SERIEA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.SERIEA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.SERIEA_TEAM_DIC)
            }
            Constants.Ids.NBA -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                playerNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_PLAYER_DIC)
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectFirstCategory(index: Int) {
        shouldScrollCategory = true
    }

    open fun selectSecondCategory(index: Int, category: String) {
        shouldScrollCategory = false
        _secondCategorySelectedIndex.value = index
    }

    open fun fetchStandings(category: String) {
        _displayDataState.value = ApiFetchState.Fetching
    }

    abstract fun filterStandings()
    abstract fun addStandings(isUp: Boolean)
}