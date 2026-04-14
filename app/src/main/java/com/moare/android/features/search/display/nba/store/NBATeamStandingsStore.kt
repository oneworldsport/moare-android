package com.moare.android.features.search.display.nba.store

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.withCompetitionRankBy
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplayModel
import com.moare.android.features.search.models.models.nba.NBATeamStats
import com.moare.android.features.search.models.responsemodels.nba.NBATeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBATeamStandingsResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface NBATeamStandingsAction {
    data object InitData : NBATeamStandingsAction
    data class SelectHeaderCategory(val index: Int) : NBATeamStandingsAction
    data class SelectCategory(val index: Int) : NBATeamStandingsAction
    data class ShowTeamStats(val id: Int) : NBATeamStandingsAction
}

sealed interface NBATeamStandingsDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : NBATeamStandingsDelegate
}

class NBATeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.NBATeamStandings,
    @Assisted val emitToParent: (NBATeamStandingsDelegate) -> Unit
) : BaseTeamStandingsStore<NBATeamStandingsAction, NBATeamStandingsResponseModel, NBATeamStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 44.dp
    val firstCategoryItemWidth = 132.dp
    val dataItemWidth = 50.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _standings = MutableStateFlow<List<NBATeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<NBATeamStandingsDisplay>> = _standings

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.NBATeamStandings,
            emitToParent: (NBATeamStandingsDelegate) -> Unit
        ) : NBATeamStandingsStore
    }

    override fun send(action: NBATeamStandingsAction) {
        scope.launch {
            when (action) {
                is NBATeamStandingsAction.InitData -> initData()
                is NBATeamStandingsAction.SelectHeaderCategory -> selectHeaderCategory(index = action.index)
                is NBATeamStandingsAction.SelectCategory -> selectCategory(action.index)
                is NBATeamStandingsAction.ShowTeamStats -> showTeamStats(action.id)
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
        _standings.value = emptyList()

        selectHeaderCategory(index = 0, isInit = true)
    }

    /* ---------------------
       implements
       --------------------- */
    override fun selectHeaderCategory(index: Int, isInit: Boolean) {
        super.selectHeaderCategory(index, isInit)

        val standings = if (isInit) {
            val entityTeam = displayModel.value.standings.firstOrNull { team ->
                // Any first team that matches with any team in entityInfo
                displayModel.value.entityInfo.firstOrNull { it.teamId == team.team.id } != null
            }

            // When init, if entity's conference is east, set index 1.
            // Otherwise do nothing, which would be set as default(0).
            if (entityTeam?.team?.teamConference?.lowercase() == "east") {
                _headerCategorySelectedIndex.value = 1
            }

            displayModel.value.standings.filter {
                if (entityTeam != null) {
                    it.team.teamConference == entityTeam.team.teamConference
                } else {
                    it.team.teamConference.lowercase() == "west" // default
                }
            }
        } else {
            _headerCategorySelectedIndex.value = index

            displayModel.value.standings.filter {
                if (index == 0) {
                    it.team.teamConference.lowercase() == "west"
                } else {
                    it.team.teamConference.lowercase() == "east"
                }
            }
        }

        _standings.value = standings

        sortStandings()
    }

    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        sortStandings()
    }

    private fun sortStandings() {
        when (categorySelectedIndex.value) {
            0, 1 -> {
                updateStandings(false) { it.stats.playoffRank.toFloat() }
            }
            2 -> {
                updateStandings(true) { it.stats.wins.toFloat() }
            }
            3 -> {
                updateStandings(false) { it.stats.losses.toFloat() }
            }
            4 -> {
                updateStandings(true) { it.stats.gp.toFloat() }
            }
            5 -> {
                updateStandings(true) {
                    val streak = it.stats.strCurrentStreak
                    val streakNumber = extractNumber(streak)
                    val sign = if (streak.startsWith("W", ignoreCase = true)) 1 else -1
                    (streakNumber * sign).toFloat()
                }
            }
            6 -> {
                _standings.update { list ->
                    list.sortedWith { a, b ->
                        val ra = a.stats.parseRecord(a.stats.l10)
                        val rb = b.stats.parseRecord(b.stats.l10)

                        when {
                            ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                            else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                        }
                    }
                }
                _standings.update { current ->
                    current.withCompetitionRankBy { it.stats.krL10 }
                }
            }
            7 -> {
                _standings.update { list ->
                    list.sortedWith { a, b ->
                        val ra = a.stats.parseRecord(a.stats.home)
                        val rb = b.stats.parseRecord(b.stats.home)

                        when {
                            ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                            else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                        }
                    }
                }
                _standings.update { current ->
                    current.withCompetitionRankBy { it.stats.krHome }
                }
            }
            8 -> {
                _standings.update { list ->
                    list.sortedWith { a, b ->
                        val ra = a.stats.parseRecord(a.stats.road)
                        val rb = b.stats.parseRecord(b.stats.road)

                        when {
                            ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                            else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                        }
                    }
                }
                _standings.update { current ->
                    current.withCompetitionRankBy { it.stats.road }
                }
            }
            10 -> {
                updateStandings(true) { it.stats.ptsPG.toFloat() }
            }
            11 -> {
                updateStandings(true) { it.stats.plusMinusPG.toFloat() }
            }
            12 -> {
                updateStandings(true) { it.stats.astPG.toFloat() }
            }
            13 -> {
                updateStandings(true) { it.stats.rebPG.toFloat() }
            }
            14 -> {
                updateStandings(true) { it.stats.fgPct.toFloat() }
            }
            15 -> {
                updateStandings(true) { it.stats.fg3Pct.toFloat() }
            }
            16 -> {
                updateStandings(true) { it.stats.ftPct.toFloat() }
            }
            17 -> {
                updateStandings(true) { it.stats.stlPG.toFloat() }
            }
            18 -> {
                updateStandings(true) { it.stats.blkPG.toFloat() }
            }
            19 -> {
                updateStandings(false) { it.stats.tovPG.toFloat() }
            }
            20 -> {
                updateStandings(false) { it.stats.pfPG.toFloat() }
            }
        }
    }

    private fun showTeamStats(id: Int) {
        val team = responseModel.standings.find { team ->
            team.team.id == id
        }
        val responseModel = NBATeamInfoResponseModel(info = team)

        val dataModel = SportDecodableModel.NBATeamStats(
            responseModel = responseModel,
            displayModel = ModelConverter.nbaTeamStatsConverter(responseModel)
        )

        emitToParent(NBATeamStandingsDelegate.ShowTeamStats(dataModel))
    }

    // TODO: Should move to util
    fun calculateGamesBack(team: NBATeamStats): Double {
        val leader = standings.value.maxBy { it.stats.winsPct }

        return ((leader.stats.wins - team.wins) + (team.losses - leader.stats.losses)) / 2.0
    }

    private fun extractNumber(str: String): Int {
        val upper = str.uppercase()
        val digits = upper
            .drop(1)
            .filter { it.isDigit() }
        return digits.toIntOrNull() ?: 0
    }

    private fun updateStandings(
        isDescending: Boolean,
        value: (NBATeamStandingsDisplay) -> Float?
    ) {
        _standings.update { list ->
            if (isDescending) {
                list.sortedByDescending(value)
            } else {
                list.sortedBy(value)
            }
        }

        _standings.update { current ->
            current.withCompetitionRankBy(value)
        }
    }
}






























