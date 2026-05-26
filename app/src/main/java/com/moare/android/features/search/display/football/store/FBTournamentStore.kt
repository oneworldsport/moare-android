package com.moare.android.features.search.display.football.store

import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTournamentDisplayModel
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import com.moare.android.features.search.models.responsemodels.football.FBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.data.networking.SearchClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface FBTournamentAction {
    data object InitData: FBTournamentAction
    data class SelectSeries(val gameList: List<FBGameForSchedule>) : FBTournamentAction
    data class SelectGame(val game: FBGameForSchedule) : FBTournamentAction
}

sealed interface FBTournamentDelegate {
    data class ShowLeagueSchedule(val model: SportDecodableModel.FBLeagueSchedule) : FBTournamentDelegate
    data class ShowGameStats(val model: SportDecodableModel.FBGameStats) : FBTournamentDelegate
}

class FBTournamentStore @AssistedInject constructor(
    private val searchClient: SearchClient,
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: FBTournamentDisplayModel,
    @Assisted val emitToParent: (FBTournamentDelegate) -> Unit
) : BaseTournamentStore<FBTournamentAction, FBTournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    private val _gameListTuple = MutableStateFlow<List<GameListEntry<FBGameInfoForSchedule>>>(emptyList())
    val gameListTuple: StateFlow<List<GameListEntry<FBGameInfoForSchedule>>> = _gameListTuple

    @AssistedFactory
    interface Factory {
        fun create(
            model: FBTournamentDisplayModel,
            emitToParent: (FBTournamentDelegate) -> Unit
        ) : FBTournamentStore
    }

    override fun send(action: FBTournamentAction) {
        when (action) {
            is FBTournamentAction.InitData -> initData()
            is FBTournamentAction.SelectSeries -> selectSeries(action.gameList)
            is FBTournamentAction.SelectGame -> selectGame(action.game)
        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        val displayModel = displayModel.value
        val leagueId = displayModel.leagueId
        val season = displayModel.season

        if (leagueId == Constants.Ids.MLS) {
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

            val (_, westFirstRound) = Util.collectRound(westFirstRoundPairedTeams, games)
            val (_, eastFirstRound) = Util.collectRound(eastFirstRoundPairedTeams, games)
            val (_, westSecondRound) = Util.collectRound(westSecondRoundPairedTeams, games)
            val (_, eastSecondRound) = Util.collectRound(eastSecondRoundPairedTeams, games)
            val (_, westThirdRound) = Util.collectRound(westThirdRoundPairedTeams, games)
            val (_, eastThirdRound) = Util.collectRound(eastThirdRoundPairedTeams, games)
            val (_, fourthRound) = Util.collectRound(fourthRoundPairedTeams, games)

            _gameListTuple.value = listOf(
                GameListEntry(title = "서부 컨퍼런스 1라운드", gameList = westFirstRound),
                GameListEntry(title = "서부 컨퍼런스 세미파이널", gameList = westSecondRound),
                GameListEntry(title = "서부 컨퍼런스 파이널", gameList = westThirdRound),
                GameListEntry(title = "MLS 컵", gameList = fourthRound),
                GameListEntry(title = "동부 컨퍼런스 파이널", gameList = eastThirdRound),
                GameListEntry(title = "동부 컨퍼런스 세미파이널", gameList = eastSecondRound),
                GameListEntry(title = "동부 컨퍼런스 1라운드", gameList = eastFirstRound)
            )
        } else {
            val firstRoundTeams = tournamentTeams["${leagueId}_${season}_64"] ?: emptyList()
            val secondRoundTeams = tournamentTeams["${leagueId}_${season}_32"] ?: emptyList()
            val thirdRoundTeams = tournamentTeams["${leagueId}_${season}_16"] ?: emptyList()
            val fourthRoundTeams = tournamentTeams["${leagueId}_${season}_8"] ?: emptyList()
            val fifthRoundTeams = tournamentTeams["${leagueId}_${season}_4"] ?: emptyList()
            val sixthRoundTeams = tournamentTeams["${leagueId}_${season}_2"] ?: emptyList()

            val firstRoundPairedTeams = firstRoundTeams.chunked(2)
            val secondRoundPairedTeams = secondRoundTeams.chunked(2)
            val thirdRoundPairedTeams = thirdRoundTeams.chunked(2)
            val fourthRoundPairedTeams = fourthRoundTeams.chunked(2)
            val fifthRoundPairedTeams = fifthRoundTeams.chunked(2)
            val sixthRoundPairedTeams = sixthRoundTeams.chunked(2)

            val games = displayModel.games.toMutableList()

            val (_, firstRound) = Util.collectRound(firstRoundPairedTeams, games)
            val (_, secondRound) = Util.collectRound(secondRoundPairedTeams, games)
            val (_, thirdRound) = Util.collectRound(thirdRoundPairedTeams, games)
            val (_, fourthRound) = Util.collectRound(fourthRoundPairedTeams, games)
            val (_, fifthRound) = Util.collectRound(fifthRoundPairedTeams, games)
            val (_, sixthRound) = Util.collectRound(sixthRoundPairedTeams, games)

            val rounds: List<GameListEntry<FBGameInfoForSchedule>> = listOf(
                GameListEntry(title = "64강", gameList = firstRound),
                GameListEntry(title = "32강", gameList = secondRound),
                GameListEntry(title = "16강", gameList = thirdRound),
                GameListEntry(title = "8강", gameList = fourthRound),
                GameListEntry(title = "준결승", gameList = fifthRound),
                GameListEntry(title = "결승", gameList = sixthRound)
            )

            // 가장 먼저 비어있지 않은 라운드부터 마지막 라운드까지 할당.
            // ex: firstRound가 비어있고 secondRound에 값이 있으면 그 이후는 비어있는거와 상관없이 모두 할당해서, secondRound ~ fifthRound 값이 들어감.
            val startIndex = rounds.indexOfFirst { it.gameList.isNotEmpty() }
            if (startIndex != -1) {
                _gameListTuple.value = rounds.subList(startIndex, rounds.size)
            }
        }
    }

    private fun selectSeries(gameList: List<FBGameForSchedule>) {
        val responseModel = FBGameScheduleResponseModel(
            scheduleType = ScheduleType.TEAM_FLAT,
            schedule = gameList
        )

        val dataModel = SportDecodableModel.FBLeagueSchedule(
            responseModel,
            ModelConverter.fbLeagueScheduleConverter(responseModel)
        )

        emitToParent(FBTournamentDelegate.ShowLeagueSchedule(dataModel))
    }

    private fun selectGame(game: FBGameForSchedule) {
        scope.launch {
            val result = searchClient.fetchById(
                season = displayModel.value.season,
                category = "football",
                date = game.date,
                dataType = "football_game_stats",
                leagueId = displayModel.value.leagueId,
                id = game.gameId
            )

            if (result.data is SportDecodableModel.FBGameStats) {
                emitToParent(FBTournamentDelegate.ShowGameStats(result.data))
            }
        }
    }
}











