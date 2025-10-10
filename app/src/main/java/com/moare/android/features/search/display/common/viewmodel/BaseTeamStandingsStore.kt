package com.moare.android.features.search.display.common.viewmodel

import com.moare.android.core.constants.Constants
import com.moare.android.core.constants.StringConstants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.models.displaymodels.SportDisplayModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseTeamStandingsStore<A, T: SportDisplayModel>(
    private val initial: T,
    private val nameProvider: TranslatedNameProvider
) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    protected val _displayModel = MutableStateFlow(initial)
    val displayModel: StateFlow<T> = _displayModel

    protected val _playerNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val playerNameDic: StateFlow<Map<String, String>> = _playerNameDic

    protected val _teamNameDic = MutableStateFlow<Map<String, String>>(emptyMap())
    val teamNameDic: StateFlow<Map<String, String>> = _teamNameDic

    protected var _headerCategorySelectedIndex = MutableStateFlow(0)
    val headerCategorySelectedIndex: StateFlow<Int> = _headerCategorySelectedIndex

    protected var _categorySelectedIndex = MutableStateFlow(0)
    val categorySelectedIndex: StateFlow<Int> = _categorySelectedIndex

    abstract fun send(action: A)

    open fun initData() {
        // init with default value
        _headerCategorySelectedIndex.value = 0
        _categorySelectedIndex.value = 0

        loadDictionaries(displayModel.value.leagueId)

        val keywords = displayModel.value.keywords
        if (keywords.isNotEmpty()) {
            val index = StringConstants.Football.TEAM_STANDINGS_CATEGORIES.indexOfFirst { category ->
                val keyword = keywords.find { it.keyword == category }
                keyword != null
            }

            if (index != -1) {
                _categorySelectedIndex.value = index
            }
        }
    }

    private fun loadDictionaries(leagueId: Int) {
        when (leagueId) {
            in Constants.Ids.FOOTBALL_LEAGUES -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.FOOTBALL_TEAM_DIC)
            }
            Constants.Ids.NBA -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.NBA_TEAM_DIC)
            }
            Constants.Ids.KBO -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.KBO_TEAM_DIC)
            }
            Constants.Ids.MLB -> {
                _teamNameDic.value = nameProvider.getDictionary(Constants.Keys.MLB_TEAM_DIC)
            }
            else -> {}
        }
    }

    open fun selectHeaderCategory(index: Int, isInit: Boolean = false) {
        _headerCategorySelectedIndex.value = index
    }

    open fun selectCategory(index: Int) {
        _categorySelectedIndex.value = index
    }

    open fun dispose() {
        scope.cancel()
    }
}