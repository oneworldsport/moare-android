package com.moare.android.features.search.display.football.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.football.FBTournamentDisplayModel
import com.moare.android.features.search.models.models.common.GameForSchedule
import com.moare.android.features.search.models.models.football.FBGameForSchedule
import com.moare.android.features.search.models.models.football.FBGameInfoForSchedule
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface FBTournamentAction {
    data object InitData: FBTournamentAction
}

class FBTournamentStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: FBTournamentDisplayModel
) : BaseTournamentStore<FBTournamentAction, FBTournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    protected val _gameListTuple = MutableStateFlow<List<GameListEntry<FBGameInfoForSchedule>>>(emptyList())
    val gameListTuple: StateFlow<List<GameListEntry<FBGameInfoForSchedule>>> = _gameListTuple

    @AssistedFactory
    interface Factory {
        fun create(
            model: FBTournamentDisplayModel
        ) : FBTournamentStore
    }

    override fun send(action: FBTournamentAction) {
        when (action) {
            is FBTournamentAction.InitData -> initData()
        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        val displayModel = displayModel.value
        val leagueId = displayModel.leagueId
        val season = displayModel.season

        val firstRoundTeams = tournamentTeams["${leagueId}_${season}_32"] ?: emptyList()
        val secondRoundTeams = tournamentTeams["${leagueId}_${season}_16"] ?: emptyList()
        val thirdRoundTeams = tournamentTeams["${leagueId}_${season}_8"] ?: emptyList()
        val fourthRoundTeams = tournamentTeams["${leagueId}_${season}_4"] ?: emptyList()
        val fifthRoundTeams = tournamentTeams["${leagueId}_${season}_2"] ?: emptyList()

        val firstRoundPairedTeamIds = firstRoundTeams.chunked(2)
        val secondRoundPairedTeamIds = secondRoundTeams.chunked(2)
        val thirdRoundPairedTeamIds = thirdRoundTeams.chunked(2)
        val fourthRoundPairedTeamIds = fourthRoundTeams.chunked(2)
        val fifthRoundPairedTeamIds = fifthRoundTeams.chunked(2)

        var games = displayModel.games.toMutableList()

        val (_, firstRound) = Util.collectRound(firstRoundPairedTeamIds, games)
        val (_, secondRound) = Util.collectRound(secondRoundPairedTeamIds, games)
        val (_, thirdRound) = Util.collectRound(thirdRoundPairedTeamIds, games)
        val (_, fourthRound) = Util.collectRound(fourthRoundPairedTeamIds, games)
        val (_, fifthRound) = Util.collectRound(fifthRoundPairedTeamIds, games)

        val rounds: List<GameListEntry<FBGameInfoForSchedule>> = listOf(
            GameListEntry(title = "32강", gameList = firstRound),
            GameListEntry(title = "16강", gameList = secondRound),
            GameListEntry(title = "8강", gameList = thirdRound),
            GameListEntry(title = "준결승", gameList = fourthRound),
            GameListEntry(title = "결승", gameList = fifthRound)
        )

        // 가장 먼저 비어있지 않은 라운드부터 마지막 라운드까지 할당.
        // ex: firstRound가 비어있고 secondRound에 값이 있으면 그 이후는 비어있는거와 상관없이 모두 할당해서, secondRound ~ fifthRound 값이 들어감.
        val startIndex = rounds.indexOfFirst { it.gameList.isNotEmpty() }
        if (startIndex != -1) {
            _gameListTuple.value = rounds.subList(startIndex, rounds.size)
        }
    }
}











