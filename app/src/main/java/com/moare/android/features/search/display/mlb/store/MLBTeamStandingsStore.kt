package com.moare.android.features.search.display.mlb.store

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplayModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.mlb.MLBTeamStandingsResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface MLBTeamStandingsAction {
    data object InitData : MLBTeamStandingsAction
    data class SelectHeaderCategory(val index: Int) : MLBTeamStandingsAction
    data class SelectCategory(val index: Int) : MLBTeamStandingsAction
    data class ShowTeamStats(val id: Int) : MLBTeamStandingsAction
}

sealed interface MLBTeamStandingsDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : MLBTeamStandingsDelegate
}

class MLBTeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.MLBTeamStandings,
    @Assisted val emitToParent: (MLBTeamStandingsDelegate) -> Unit
) : BaseTeamStandingsStore<MLBTeamStandingsAction, MLBTeamStandingsResponseModel, MLBTeamStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 44.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp
    val columnWidthList = listOf(50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 50.dp, 60.dp, 60.dp, 50.dp, 50.dp, 50.dp, 70.dp)

    private var _westStandings = MutableStateFlow<List<MLBTeamStandingsDisplay>>(emptyList())
    val westStandings: StateFlow<List<MLBTeamStandingsDisplay>> = _westStandings

    private var _eastStandings = MutableStateFlow<List<MLBTeamStandingsDisplay>>(emptyList())
    val eastStandings: StateFlow<List<MLBTeamStandingsDisplay>> = _eastStandings

    private var _centralStandings = MutableStateFlow<List<MLBTeamStandingsDisplay>>(emptyList())
    val centralStandings: StateFlow<List<MLBTeamStandingsDisplay>> = _centralStandings

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.MLBTeamStandings,
            emitToParent: (MLBTeamStandingsDelegate) -> Unit
        ) : MLBTeamStandingsStore
    }

    override fun send(action: MLBTeamStandingsAction) {
        scope.launch {
            when (action) {
                is MLBTeamStandingsAction.InitData -> initData()
                is MLBTeamStandingsAction.SelectHeaderCategory -> selectHeaderCategory(index = action.index)
                is MLBTeamStandingsAction.SelectCategory -> selectCategory(action.index)
                is MLBTeamStandingsAction.ShowTeamStats -> showTeamStats(action.id)
            }
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData() {
        super.initData()

        // init with default value
        _categorySelectedIndex.value = 1 // defalue category is "승률"
        _westStandings.value = emptyList()
        _eastStandings.value = emptyList()
        _centralStandings.value = emptyList()

        selectHeaderCategory(index = 0, isInit = true)
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectHeaderCategory(index: Int, isInit: Boolean) {
        super.selectHeaderCategory(index, isInit)

        if (isInit) {
            val entityTeam = displayModel.value.standings.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value.entityInfo.firstOrNull { it.teamId == team.team.id } != null
            }
            val teamLeagueId = entityTeam?.team?.league?.id

            // When init, if entity's league is american, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (teamLeagueId == Constants.Ids.AMERICAN_LEAGUE) {
                _headerCategorySelectedIndex.value = 1
            }

            val standings = displayModel.value.standings

            _westStandings.value = standings.filter {
                if (teamLeagueId == Constants.Ids.AMERICAN_LEAGUE) {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_WEST
                } else {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_WEST
                }
            }
            _eastStandings.value = standings.filter {
                if (teamLeagueId == Constants.Ids.AMERICAN_LEAGUE) {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_EAST
                } else {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_EAST
                }
            }
            _centralStandings.value = standings.filter {
                if (teamLeagueId == Constants.Ids.AMERICAN_LEAGUE) {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_CENTRAL
                } else {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_CENTRAL
                }
            }
        } else {
            _headerCategorySelectedIndex.value = index

            val standings = displayModel.value.standings

            _westStandings.value = standings.filter {
                if (index == 0) {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_WEST
                } else {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_WEST
                }
            }
            _eastStandings.value = standings.filter {
                if (index == 0) {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_EAST
                } else {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_EAST
                }
            }
            _centralStandings.value = standings.filter {
                if (index == 0) {
                    it.team.division.id == Constants.Ids.NATIONAL_LEAGUE_CENTRAL
                } else {
                    it.team.division.id == Constants.Ids.AMERICAN_LEAGUE_CENTRAL
                }
            }
        }

        sortStandings()
    }

    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        sortStandings()
    }

    private fun sortStandings() {
        val westStandings = westStandings.value.toMutableList()
        val eastStandings = eastStandings.value.toMutableList()
        val centralStandings = centralStandings.value.toMutableList()

        when (categorySelectedIndex.value) {
            0 -> {
                westStandings.sortBy { it.stats.recordData?.gamesBack?.toFloatOrNull() }
                eastStandings.sortBy { it.stats.recordData?.gamesBack?.toFloatOrNull() }
                centralStandings.sortBy { it.stats.recordData?.gamesBack?.toFloatOrNull() }
            }
            1 -> {
                westStandings.sortByDescending { it.stats.recordData?.winningPercentage?.toFloatOrNull() }
                eastStandings.sortByDescending { it.stats.recordData?.winningPercentage?.toFloatOrNull() }
                centralStandings.sortByDescending { it.stats.recordData?.winningPercentage?.toFloatOrNull() }
            }
            2 -> {
                westStandings.sortByDescending { it.stats.recordData?.wins }
                eastStandings.sortByDescending { it.stats.recordData?.wins }
                centralStandings.sortByDescending { it.stats.recordData?.wins }
            }
            3 -> {
                westStandings.sortBy { it.stats.recordData?.losses }
                eastStandings.sortBy { it.stats.recordData?.losses }
                centralStandings.sortBy { it.stats.recordData?.losses }
            }
            4 -> {
                westStandings.sortByDescending { it.stats.recordData?.gamesPlayed }
                eastStandings.sortByDescending { it.stats.recordData?.gamesPlayed }
                centralStandings.sortByDescending { it.stats.recordData?.gamesPlayed }
            }
            5 -> {
                westStandings.sortByDescending {
                    val streak = it.stats.recordData?.streak
                    val streakNumber = streak?.streakNumber ?: 0
                    val sign = if (streak?.streakType?.lowercase()?.startsWith("w") == true) 1 else -1
                    streakNumber * sign
                }
                eastStandings.sortByDescending {
                    val streak = it.stats.recordData?.streak
                    val streakNumber = streak?.streakNumber ?: 0
                    val sign = if (streak?.streakType?.lowercase()?.startsWith("w") == true) 1 else -1
                    streakNumber * sign
                }
                centralStandings.sortByDescending {
                    val streak = it.stats.recordData?.streak
                    val streakNumber = streak?.streakNumber ?: 0
                    val sign = if (streak?.streakType?.lowercase()?.startsWith("w") == true) 1 else -1
                    streakNumber * sign
                }
            }
            6 -> {
                westStandings.sortByDescending { it.stats.hitting?.avg?.toFloatOrNull() }
                eastStandings.sortByDescending { it.stats.hitting?.avg?.toFloatOrNull() }
                centralStandings.sortByDescending { it.stats.hitting?.avg?.toFloatOrNull() }
            }
            7 -> {
                westStandings.sortByDescending { it.stats.hitting?.hits }
                eastStandings.sortByDescending { it.stats.hitting?.hits }
                centralStandings.sortByDescending { it.stats.hitting?.hits }
            }
            8 -> {
                westStandings.sortByDescending { it.stats.hitting?.homeRuns }
                eastStandings.sortByDescending { it.stats.hitting?.homeRuns }
                centralStandings.sortByDescending { it.stats.hitting?.homeRuns }
            }
            9 -> {
                westStandings.sortByDescending { it.stats.hitting?.slg?.toFloatOrNull() }
                eastStandings.sortByDescending { it.stats.hitting?.slg?.toFloatOrNull() }
                centralStandings.sortByDescending { it.stats.hitting?.slg?.toFloatOrNull() }
            }
            10 -> {
                westStandings.sortByDescending { it.stats.hitting?.runs }
                eastStandings.sortByDescending { it.stats.hitting?.runs }
                centralStandings.sortByDescending { it.stats.hitting?.runs }
            }
            11 -> {
                westStandings.sortBy { it.stats.pitching?.era?.toFloatOrNull() }
                eastStandings.sortBy { it.stats.pitching?.era?.toFloatOrNull() }
                centralStandings.sortBy { it.stats.pitching?.era?.toFloatOrNull() }
            }
            12 -> {
                westStandings.sortBy { it.stats.pitching?.avg?.toFloatOrNull() }
                eastStandings.sortBy { it.stats.pitching?.avg?.toFloatOrNull() }
                centralStandings.sortBy { it.stats.pitching?.avg?.toFloatOrNull() }
            }
            13 -> {
                westStandings.sortBy { it.stats.pitching?.hits }
                eastStandings.sortBy { it.stats.pitching?.hits }
                centralStandings.sortBy { it.stats.pitching?.hits }
            }
            14 -> {
                westStandings.sortBy { it.stats.pitching?.homeRuns }
                eastStandings.sortBy { it.stats.pitching?.homeRuns }
                centralStandings.sortBy { it.stats.pitching?.homeRuns }
            }
            15 -> {
                westStandings.sortBy { it.stats.pitching?.runs }
                eastStandings.sortBy { it.stats.pitching?.runs }
                centralStandings.sortBy { it.stats.pitching?.runs }
            }
            16 -> {
                westStandings.sortByDescending { it.stats.hitting?.stolenBasePercentage?.toFloatOrNull() }
                eastStandings.sortByDescending { it.stats.hitting?.stolenBasePercentage?.toFloatOrNull() }
                centralStandings.sortByDescending { it.stats.hitting?.stolenBasePercentage?.toFloatOrNull() }
            }
        }

        _westStandings.value = westStandings
        _eastStandings.value = eastStandings
        _centralStandings.value = centralStandings
    }

    private fun showTeamStats(id: Int) {
        val team = responseModel.standings.find { team ->
            team.team.id == id
        }
        val responseModel = MLBTeamInfoResponseModel(info = team)

        val dataModel = SportDecodableModel.MLBTeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.mlbTeamStatsConverter(responseModel)
        )

        emitToParent(MLBTeamStandingsDelegate.ShowTeamStats(dataModel))
    }
}