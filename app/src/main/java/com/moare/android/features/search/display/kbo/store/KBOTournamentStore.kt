package com.moare.android.features.search.display.kbo.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.kbo.KBOTournamentDisplayModel
import com.moare.android.features.search.models.models.kbo.KBOGameForSchedule
import com.moare.android.features.search.models.models.kbo.KBOGameInfoForSchedule
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.models.responsemodels.kbo.KBOGameScheduleResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface KBOTournamentAction {
    data object InitData: KBOTournamentAction
    data class SelectSeries(val gameList: List<KBOGameForSchedule>) : KBOTournamentAction
}

sealed interface KBOTournamentDelegate {
    data class ShowLeagueSchedule(val model: SportDecodableModel.KBOLeagueSchedule) : KBOTournamentDelegate
}

class KBOTournamentStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: KBOTournamentDisplayModel,
    @Assisted val emitToParent: (KBOTournamentDelegate) -> Unit
) : BaseTournamentStore<KBOTournamentAction, KBOTournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    private val _gameListTuple = MutableStateFlow<List<GameListEntry<KBOGameInfoForSchedule>>>(emptyList())
    val gameListTuple: StateFlow<List<GameListEntry<KBOGameInfoForSchedule>>> = _gameListTuple

    private val _seedIdPairList = MutableStateFlow<List<List<Pair<Int?, Int?>>>>(emptyList())
    val seedIdPairList: StateFlow<List<List<Pair<Int?, Int?>>>> = _seedIdPairList

    @AssistedFactory
    interface Factory {
        fun create(
            model: KBOTournamentDisplayModel,
            emitToParent: (KBOTournamentDelegate) -> Unit
        ) : KBOTournamentStore
    }

    override fun send(action: KBOTournamentAction) {
        when (action) {
            is KBOTournamentAction.InitData -> initData()
            is KBOTournamentAction.SelectSeries -> selectSeries(action.gameList)
        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        val displayModel = displayModel.value
        val leagueId = displayModel.leagueId
        val season = displayModel.season

        // 시드 순서를 유지해야해서 다음과 같은 로직 적용
        val firstRoundTeams = tournamentTeams["${leagueId}_${season}_16"] ?: emptyList()
        val secondRoundTeams = tournamentTeams["${leagueId}_${season}_8"] ?: emptyList()
        val thirdRoundTeams = tournamentTeams["${leagueId}_${season}_4"] ?: emptyList()
        val fourthRoundTeams = tournamentTeams["${leagueId}_${season}_2"] ?: emptyList()

        if (firstRoundTeams.size == 2 &&
            secondRoundTeams.size == 2 &&
            thirdRoundTeams.size == 2 &&
            fourthRoundTeams.size == 2) {
            val games = displayModel.games.filter { it.gameStatus != Constants.GameStatus.KBO.CANCELED }.toMutableList()

            val (firstRoundSeedTuple, firstRound) = Util.collectRound(listOf(firstRoundTeams), games)
            val (secondRoundSeedTuple, secondRound) = Util.collectRound(listOf(secondRoundTeams), games)
            val (thirdRoundSeedTuple, thirdRound) = Util.collectRound(listOf(thirdRoundTeams), games)
            val (fourthRoundSeedTuple, fourthRound) = Util.collectRound(listOf(fourthRoundTeams), games)

            _gameListTuple.value = listOf(
                GameListEntry(title = "와일드카드 결정전", gameList = firstRound),
                GameListEntry(title = "준플레이오프", gameList = secondRound),
                GameListEntry(title = "플레이오프", gameList = thirdRound),
                GameListEntry(title = "한국시리즈", gameList = fourthRound)
            )

            // gameListTuple에 추가되는 순서대로 추가
            _seedIdPairList.value = listOf(
                firstRoundSeedTuple,
                secondRoundSeedTuple,
                thirdRoundSeedTuple,
                fourthRoundSeedTuple
            )
        }
    }

    private fun selectSeries(gameList: List<KBOGameForSchedule>) {
        val responseModel = KBOGameScheduleResponseModel(
            scheduleType = ScheduleType.TEAM_FLAT,
            schedule = gameList
        )

        val dataModel = SportDecodableModel.KBOLeagueSchedule(
            responseModel,
            ModelConverter.kboLeagueScheduleConverter(responseModel)
        )

        emitToParent(KBOTournamentDelegate.ShowLeagueSchedule(dataModel))
    }
}