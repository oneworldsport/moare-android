package com.moare.android.features.search.display.nba.store

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGameForSchedule
import com.moare.android.features.search.models.models.nba.NBAGameSummary
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface NBATournamentAction {
    data object InitData: NBATournamentAction
    data class SelectSeries(val gameList: List<NBAGameForSchedule>) : NBATournamentAction
}

sealed interface NBATournamentDelegate {
    data class ShowLeagueSchedule(val model: SportDecodableModel.NBALeagueSchedule) : NBATournamentDelegate
}

class NBATournamentStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: NBATournamentDisplayModel,
    @Assisted val emitToParent: (NBATournamentDelegate) -> Unit
) : BaseTournamentStore<NBATournamentAction, NBATournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    private val _gameListTuple = MutableStateFlow<List<GameListEntry<NBAGameSummary>>>(emptyList())
    val gameListTuple: StateFlow<List<GameListEntry<NBAGameSummary>>> = _gameListTuple

    private val _seedIdPairList = MutableStateFlow<List<List<Pair<Int?, Int?>>>>(emptyList())
    val seedIdPairList: StateFlow<List<List<Pair<Int?, Int?>>>> = _seedIdPairList

    @AssistedFactory
    interface Factory {
        fun create(
            model: NBATournamentDisplayModel,
            emitToParent: (NBATournamentDelegate) -> Unit
        ) : NBATournamentStore
    }

    override fun send(action: NBATournamentAction) {
        when (action) {
            is NBATournamentAction.InitData -> initData()
            is NBATournamentAction.SelectSeries -> selectSeries(action.gameList)
        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        val displayModel = displayModel.value
        val leagueId = displayModel.leagueId
        val season = displayModel.season
//        val season = 2024

        // 시드 순서를 유지해야해서 다음과 같은 로직 적용
        val firstRoundTeams = tournamentTeams["${leagueId}_${season}_16"] ?: emptyList()
        val westFirstRoundTeams = firstRoundTeams.take(8)
        val eastFirstRoundTeams = firstRoundTeams.takeLast(8)

        val secondRoundTeams = tournamentTeams["${leagueId}_${season}_8"] ?: emptyList()
        val westSecondRoundTeams = secondRoundTeams.take(4)
        val eastSecondRoundTeams = secondRoundTeams.takeLast(4)

        val thirdRoundTeams = tournamentTeams["${leagueId}_${season}_4"] ?: emptyList()
        val westThirdRoundTeams = thirdRoundTeams.take(2)
        val eastThirdRoundTeams = thirdRoundTeams.takeLast(2)

        val fourthRoundTeams = tournamentTeams["${leagueId}_${season}_2"] ?: emptyList()

        val westFirstRoundPairedTeams = westFirstRoundTeams.chunked(2)
        val eastFirstRoundPairedTeams = eastFirstRoundTeams.chunked(2)
        val westSecondRoundPairedTeams = westSecondRoundTeams.chunked(2)
        val eastSecondRoundPairedTeams = eastSecondRoundTeams.chunked(2)
        val westThirdRoundPairedTeams = westThirdRoundTeams.chunked(2)
        val eastThirdRoundPairedTeams = eastThirdRoundTeams.chunked(2)
        val fourthRoundPairedTeams = fourthRoundTeams.chunked(2)

        val games = displayModel.games.toMutableList()

        val (westFirstRoundSeedTuple, westFirstRound) = Util.collectRound(westFirstRoundPairedTeams, games)
        val (eastFirstRoundSeedTuple, eastFirstRound) = Util.collectRound(eastFirstRoundPairedTeams, games)
        val (westSecondRoundSeedTuple, westSecondRound) = Util.collectRound(westSecondRoundPairedTeams, games)
        val (eastSecondRoundSeedTuple, eastSecondRound) = Util.collectRound(eastSecondRoundPairedTeams, games)
        val (westThirdRoundSeedTuple, westThirdRound) = Util.collectRound(westThirdRoundPairedTeams, games)
        val (eastThirdRoundSeedTuple, eastThirdRound) = Util.collectRound(eastThirdRoundPairedTeams, games)
        val (fourthRoundSeedTuple, fourthRound) = Util.collectRound(fourthRoundPairedTeams, games)

        _gameListTuple.value = listOf(
            GameListEntry(title = "서부 컨퍼런스 1라운드", gameList = westFirstRound),
            GameListEntry(title = "서부 컨퍼런스 세미파이널", gameList = westSecondRound),
            GameListEntry(title = "서부 컨퍼런스 파이널", gameList = westThirdRound),
            GameListEntry(title = "NBA 파이널", gameList = fourthRound),
            GameListEntry(title = "동부 컨퍼런스 파이널", gameList = eastThirdRound),
            GameListEntry(title = "동부 컨퍼런스 세미파이널", gameList = eastSecondRound),
            GameListEntry(title = "동부 컨퍼런스 1라운드", gameList = eastFirstRound)
        )

        // gameListTuple에 추가되는 순서대로 추가
        _seedIdPairList.value = listOf(
            westFirstRoundSeedTuple,
            westSecondRoundSeedTuple,
            westThirdRoundSeedTuple,
            fourthRoundSeedTuple,
            eastThirdRoundSeedTuple,
            eastSecondRoundSeedTuple,
            eastFirstRoundSeedTuple
        )
    }

    private fun selectSeries(gameList: List<NBAGameForSchedule>) {
        val responseModel = NBAGameScheduleResponseModel(
            scheduleType = ScheduleType.TEAM_FLAT,
            schedule = gameList
        )

        val dataModel = SportDecodableModel.NBALeagueSchedule(
            responseModel,
            ModelConverter.nbaLeagueScheduleConverter(responseModel)
        )

        emitToParent(NBATournamentDelegate.ShowLeagueSchedule(dataModel))
    }
}














