package com.moare.android.features.search.display.nba.store

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.assignCompetitionRankBy
import com.moare.android.features.search.display.common.store.BaseTeamStandingsStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
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
        val standings = standings.value.toMutableList()

        when (categorySelectedIndex.value) {
            0, 1 -> {
                standings.sortBy { it.stats.playoffRank }
                for (i in standings.indices) {
                    standings[i].displayRank = standings[i].stats.playoffRank
                }
            }
            2 -> {
                standings.sortByDescending { it.stats.wins }
                standings.assignCompetitionRankBy { it.stats.wins }
            }
            3 -> {
                standings.sortBy { it.stats.losses }
                standings.assignCompetitionRankBy { it.stats.losses }
            }
            4 -> {
                standings.sortByDescending { it.stats.gp }
                standings.assignCompetitionRankBy { it.stats.gp }
            }
            5 -> {
                standings.sortWith(streakComparator)
                standings.assignCompetitionRankBy { it.stats.krCurrentStreak }
            }
            6 -> {
                standings.sortWith { a, b ->
                    val ra = a.stats.parseRecord(a.stats.l10)
                    val rb = b.stats.parseRecord(b.stats.l10)

                    when {
                        ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                        else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                    }
                }
                standings.assignCompetitionRankBy { it.stats.krL10 }
            }
            7 -> {
                standings.sortWith { a, b ->
                    val ra = a.stats.parseRecord(a.stats.home)
                    val rb = b.stats.parseRecord(b.stats.home)

                    when {
                        ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                        else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                    }
                }
                standings.assignCompetitionRankBy { it.stats.krHome }
            }
            8 -> {
                standings.sortWith { a, b ->
                    val ra = a.stats.parseRecord(a.stats.road)
                    val rb = b.stats.parseRecord(b.stats.road)

                    when {
                        ra.first != rb.first -> rb.first.compareTo(ra.first)  // 1) 승률 내림차순
                        else -> rb.second.compareTo(ra.second)                // 2) 승수 내림차순
                    }
                }
                standings.assignCompetitionRankBy { it.stats.road }
            }
            10 -> {
                standings.sortByDescending { it.stats.ptsPG }
                standings.assignCompetitionRankBy { it.stats.ptsPG }
            }
            11 -> {
                standings.sortByDescending { it.stats.plusMinusPG }
                standings.assignCompetitionRankBy { it.stats.plusMinusPG }
            }
            12 -> {
                standings.sortByDescending { it.stats.astPG }
                standings.assignCompetitionRankBy { it.stats.astPG }
            }
            13 -> {
                standings.sortByDescending { it.stats.rebPG }
                standings.assignCompetitionRankBy { it.stats.rebPG }
            }
            14 -> {
                standings.sortByDescending { it.stats.fgPct }
                standings.assignCompetitionRankBy { it.stats.fgPct }
            }
            15 -> {
                standings.sortByDescending { it.stats.fg3Pct }
                standings.assignCompetitionRankBy { it.stats.fg3Pct }
            }
            16 -> {
                standings.sortByDescending { it.stats.ftPct }
                standings.assignCompetitionRankBy { it.stats.ftPct }
            }
            17 -> {
                standings.sortByDescending { it.stats.stlPG }
                standings.assignCompetitionRankBy { it.stats.stlPG }
            }
            18 -> {
                standings.sortByDescending { it.stats.blkPG }
                standings.assignCompetitionRankBy { it.stats.blkPG }
            }
            19 -> {
                standings.sortBy { it.stats.tovPG }
                standings.assignCompetitionRankBy { it.stats.tovPG }
            }
            20 -> {
                standings.sortBy { it.stats.pfPG }
                standings.assignCompetitionRankBy { it.stats.pfPG }
            }
        }

        _standings.value = standings
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

    val streakComparator = Comparator<NBATeamStandingsDisplay> { a, b ->
        val aStreak = a.stats.strCurrentStreak
        val bStreak = b.stats.strCurrentStreak

        val aIsWin = aStreak.startsWith("W", ignoreCase = true)
        val bIsWin = bStreak.startsWith("W", ignoreCase = true)

        val aNum = extractNumber(aStreak)
        val bNum = extractNumber(bStreak)

        when {
            aIsWin && bIsWin -> bNum.compareTo(aNum)      // 둘 다 승: 숫자 큰 순(내림차순)
            !aIsWin && !bIsWin -> aNum.compareTo(bNum)    // 둘 다 패: 숫자 작은 순(오름차순)
            aIsWin && !bIsWin -> -1                       // 승이 우선
            else -> 1
        }
    }

    private fun extractNumber(str: String): Int {
        val upper = str.uppercase()
        val digits = upper
            .drop(1)
            .filter { it.isDigit() }
        return digits.toIntOrNull() ?: 0
    }
}






























