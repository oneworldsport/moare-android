package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseTeamStandingsViewModel<I, T>(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<I, T>() {
    /* ---------------------
       data state
       --------------------- */
    protected val _displayModel = MutableStateFlow<T?>(null)
    val displayModel: StateFlow<T?> = _displayModel

    /* ---------------------
       ui state
       --------------------- */
    protected var _selectedCategoryIndex = MutableStateFlow(0)
    val selectedCategoryIndex: StateFlow<Int> = _selectedCategoryIndex

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    override fun initData(displayModel: T) {
        // init with default value
        _selectedCategoryIndex.value = 0

        // init data
        _displayModel.value = displayModel

        if (displayModel is SportDisplayModel) {
            loadDictionaries(displayModel.leagueId)

            val keywords = displayModel.keywords
            if (keywords.isNotEmpty()) {
                val index = StringConstants.Football.TEAM_STANDINGS_CATEGORIES.indexOfFirst { category ->
                    val keyword = keywords.find { it.keyword == category }
                    keyword != null
                }

                if (index != -1) {
                    _selectedCategoryIndex.value = index
                }
            }
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
            in Constants.Ids.FOOTBALL_LEAGUES -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)
            }
            Constants.Ids.NBA -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                teamNameDictionary = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectCategory(index: Int) {
        _selectedCategoryIndex.value = index
    }
}