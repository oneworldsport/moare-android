package com.moare.android.features.search.display.football.store

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.withCompetitionRankBy
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplay
import com.moare.android.features.search.models.displaymodels.football.FBTeamStandingsDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATeamStandingsDisplay
import com.moare.android.features.search.models.models.football.FBTeamStatsFixtures
import com.moare.android.features.search.models.responsemodels.football.FBTeamInfoResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsResponseModel
import com.moare.android.features.search.models.responsemodels.football.FBTeamStandingsSource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface FBTeamStandingsAction {
    data object InitData : FBTeamStandingsAction
    data class SelectHeaderCategory(val index: Int) : FBTeamStandingsAction
    data class SelectCategory(val index: Int) : FBTeamStandingsAction
    data class ShowTeamStats(val id: Int) : FBTeamStandingsAction
}

sealed interface FBTeamStandingsDelegate {
    data class ShowTeamStats(val model: SportDecodableModel) : FBTeamStandingsDelegate
}

class FBTeamStandingsStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    @Assisted val model: SportDecodableModel.FBTeamStandings,
    @Assisted val emitToParent: (FBTeamStandingsDelegate) -> Unit
) : BaseTeamStandingsStore<FBTeamStandingsAction, FBTeamStandingsResponseModel, FBTeamStandingsDisplayModel>(
    model.responseModel, model.displayModel, nameProvider
) {
    val dataItemHeight = 40.dp
    val categoryItemHeight = 40.dp
    val firstCategoryItemWidth = 132.dp
    val intDataItemWidth = 50.dp
    val stringDataItemWidth = 110.dp
    val stringDataItemTextWidth = 34.dp
    val categoryFontSize = 15.sp
    val dataFontSize = 15.sp

    private var _standings = MutableStateFlow<List<FBTeamStandingsDisplay>>(emptyList())
    val standings: StateFlow<List<FBTeamStandingsDisplay>> = _standings

    private var _groupStandings = MutableStateFlow<Map<String, List<FBTeamStandingsDisplay>>>(emptyMap())
    val groupStandings: StateFlow<Map<String, List<FBTeamStandingsDisplay>>> = _groupStandings

    private var _isMLS = MutableStateFlow(false)
    val isMLS: StateFlow<Boolean> = _isMLS

    private var _isGroupStandings = MutableStateFlow(false)
    val isGroupStandings: StateFlow<Boolean> = _isGroupStandings

    @AssistedFactory
    interface Factory {
        fun create(
            model: SportDecodableModel.FBTeamStandings,
            emitToParent: (FBTeamStandingsDelegate) -> Unit
        ) : FBTeamStandingsStore
    }

    override fun send(action: FBTeamStandingsAction) {
        scope.launch {
            when (action) {
                is FBTeamStandingsAction.InitData -> initData()
                is FBTeamStandingsAction.SelectHeaderCategory -> selectHeaderCategory(index = action.index)
                is FBTeamStandingsAction.SelectCategory -> selectCategory(action.index)
                is FBTeamStandingsAction.ShowTeamStats -> showTeamStats(action.id)
            }
        }
    }

    override fun initData() {
        super.initData()

        // init data
        _standings.value = displayModel.value.standings
        _isMLS.value = displayModel.value.leagueId == Constants.Ids.MLS
        _isGroupStandings.value = displayModel.value.leagueId == Constants.Ids.WORLD_CUP
        _groupStandings.value = displayModel.value.groupStandings

        if (isMLS.value || isGroupStandings.value) {
            selectHeaderCategory(index = 0, isInit = true)
        } else {
            if (isGroupStandings.value) {
                sortGroupStandings()
            } else {
                sortStandings()
            }
        }
    }

    override fun selectHeaderCategory(index: Int, isInit: Boolean) {
        super.selectHeaderCategory(index, isInit)

        if (isGroupStandings.value) {
            val firstGroup = listOf("A", "B", "C", "D", "E", "F")
            val secondGroup = listOf("G", "H", "I", "J", "K", "L")

            _groupStandings.value = displayModel.value.groupStandings.filter { (key, _) ->
                if (index == 0) {
                    firstGroup.contains(key)
                } else {
                    secondGroup.contains(key)
                }
            }

            sortGroupStandings()
        } else {
            val standings = if (isInit) {
                val entityTeam = displayModel.value.standings.firstOrNull { team ->
                    // Any first team that matches with any team in entityInfo
                    displayModel.value.entityInfo.firstOrNull { it.teamId == team.team.id } != null
                }

                // When init, if entity's conference is east, set index 1.
                // Otherwise do nothing, which would be set as default(0).
                if (Constants.Ids.MLSTeam.eastConference.contains(entityTeam?.team?.id)) {
                    _headerCategorySelectedIndex.value = 1
                }

                displayModel.value.standings.filter {
                    if (entityTeam != null) {
                        Constants.Ids.MLSTeam.eastConference.contains(it.team.id)
                    } else {
                        Constants.Ids.MLSTeam.westConference.contains(it.team.id)
                    }
                }
            } else {
                _headerCategorySelectedIndex.value = index

                displayModel.value.standings.filter {
                    if (index == 0) {
                        Constants.Ids.MLSTeam.westConference.contains(it.team.id)
                    } else {
                        Constants.Ids.MLSTeam.eastConference.contains(it.team.id)
                    }
                }
            }

            _standings.value = standings

            sortStandings()
        }
    }

    override fun selectCategory(index: Int) {
        super.selectCategory(index)

        if (isGroupStandings.value) {
            sortGroupStandings()
        } else {
            sortStandings()
        }
    }

    private fun sortStandings() {
        when (categorySelectedIndex.value) {
            0 -> {
                updateStandings(false) { it.rank.toFloat() }
            }
            1 -> {
                updateStandings(true) { it.homeAwayStats.wins.total.toFloat() }
            }
            2 -> {
                updateStandings(true) { it.homeAwayStats.draws.total.toFloat() }
            }
            3 -> {
                updateStandings(false) { it.homeAwayStats.loses.total.toFloat() }
            }
            4 -> {
                updateStandings(true) { it.homeAwayStats.played.total.toFloat() }
            }
            5 -> {
                updateStandings(true) { it.goalsFor.total.toFloat() }
            }
            6 -> {
                updateStandings(false) { it.goalsAgainst.total.toFloat() }
            }
            7 -> {
                updateStandings(true) { (it.goalsFor.total - it.goalsAgainst.total).toFloat() }
            }
            8 -> {
                _standings.update { list ->
                    list.sortedWith { a, b ->
                        val pa = calculateHomePoints(a.homeAwayStats)
                        val pb = calculateHomePoints(b.homeAwayStats)

                        when {
                            pa != pb -> pb.compareTo(pa) // 1) points 내림차순
                            else -> {
                                val wa = a.homeAwayStats.wins.home
                                val wb = b.homeAwayStats.wins.home

                                when {
                                    wa != wb -> wb.compareTo(wa) // 2) wins 내림차순
                                    else -> {
                                        val la = a.homeAwayStats.loses.home
                                        val lb = b.homeAwayStats.loses.home

                                        when {
                                            la != lb -> la.compareTo(lb) // 3) loses 오름차순
                                            else -> 0
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                _standings.update { current ->
                    current.withCompetitionRankBy { getRecordString(it.homeAwayStats) }
                }
            }
            9 -> {
                _standings.update { list ->
                    list.sortedWith { a, b ->
                        val pa = calculateAwayPoints(a.homeAwayStats)
                        val pb = calculateAwayPoints(b.homeAwayStats)

                        when {
                            pa != pb -> pb.compareTo(pa) // 1) points 내림차순
                            else -> {
                                val wa = a.homeAwayStats.wins.away
                                val wb = b.homeAwayStats.wins.away

                                when {
                                    wa != wb -> wb.compareTo(wa) // 2) wins 내림차순
                                    else -> {
                                        val la = a.homeAwayStats.loses.away
                                        val lb = b.homeAwayStats.loses.away

                                        when {
                                            la != lb -> la.compareTo(lb) // 3) loses 오름차순
                                            else -> 0
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                _standings.update { current ->
                    current.withCompetitionRankBy { getRecordString(it.homeAwayStats, false) }
                }
            }
        }
    }

    private fun sortGroupStandings() {
        when (categorySelectedIndex.value) {
            0 -> {
                updateGroupStandings(false) { it.rank.toFloat() }
            }
            1 -> {
                updateGroupStandings(true) { it.homeAwayStats.wins.total.toFloat() }
            }
            2 -> {
                updateGroupStandings(true) { it.homeAwayStats.draws.total.toFloat() }
            }
            3 -> {
                updateGroupStandings(false) { it.homeAwayStats.loses.total.toFloat() }
            }
            4 -> {
                updateGroupStandings(true) { it.homeAwayStats.played.total.toFloat() }
            }
            5 -> {
                updateGroupStandings(true) { it.goalsFor.total.toFloat() }
            }
            6 -> {
                updateGroupStandings(false) { it.goalsAgainst.total.toFloat() }
            }
            7 -> {
                updateGroupStandings(true) { (it.goalsFor.total - it.goalsAgainst.total).toFloat() }
            }
        }
    }

    private fun showTeamStats(id: Int) {
        if (responseModel.standings is FBTeamStandingsSource.Db) {
            val team = responseModel.standings.teams.find { team ->
                team.team.id == id
            }
            val responseModel = FBTeamInfoResponseModel(info = team)

            val dataModel = SportDecodableModel.FBTeamStats(
                responseModel = responseModel,
                displayModel = ModelConverter.fbTeamStatsConverter(responseModel)
            )

            emitToParent(FBTeamStandingsDelegate.ShowTeamStats(dataModel))
        }
    }

    // TODO: Should move to util or make it as intent(mvi)
    fun isStringData(index: Int): Boolean {
        return index == 8 || index == 9
    }

    fun getItemWidth(index: Int): Dp {
        return if (isStringData(index)) stringDataItemWidth else intDataItemWidth
    }

    fun calculatePoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.total) * 3 + (data.draws.total))
    }

    fun getRecordString(
        data: FBTeamStatsFixtures,
        isHome: Boolean = true
    ): String {
        return if (isHome) {
            "${data.wins.home}승 ${data.draws.home}무 ${data.loses.home}패"
        } else {
            "${data.wins.away}승 ${data.draws.away}무 ${data.loses.away}패"
        }
    }

    private fun calculateHomePoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.home) * 3 + (data.draws.home))
    }

    private fun calculateAwayPoints(data: FBTeamStatsFixtures): Int {
        return ((data.wins.away) * 3 + (data.draws.away))
    }

    private fun updateStandings(
        isDescending: Boolean,
        value: (FBTeamStandingsDisplay) -> Float?
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

    private fun updateGroupStandings(
        isDescending: Boolean,
        value: (FBTeamStandingsDisplay) -> Float?
    ) {
        _groupStandings.update {
            if (isDescending) {
                it.mapValues { (_, standings) ->
                    standings.sortedByDescending(value)
                }
            } else {
                it.mapValues { (_, standings) ->
                    standings.sortedBy(value)
                }
            }
        }
        _groupStandings.update {
            it.mapValues { (_, standings) ->
                standings.withCompetitionRankBy(value)
            }
        }
    }
}