package com.moare.android.features.search.display.mlb.viewmodel

import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.util.Util
import com.moare.android.features.search.display.common.container.state.GameListEntry
import com.moare.android.features.search.display.common.store.BaseTournamentStore
import com.moare.android.features.search.models.ModelConverter
import com.moare.android.features.search.models.SportDecodableModel
import com.moare.android.features.search.models.displaymodels.mlb.MLBTournamentDisplayModel
import com.moare.android.features.search.models.displaymodels.nba.NBATournamentDisplayModel
import com.moare.android.features.search.models.models.mlb.MLBGameForSchedule
import com.moare.android.features.search.models.models.mlb.MLBGameInfoForSchedule
import com.moare.android.features.search.models.models.nba.NBAGameSummary
import com.moare.android.features.search.models.responsemodels.football.ScheduleType
import com.moare.android.features.search.models.responsemodels.mlb.MLBGameScheduleResponseModel
import com.moare.android.features.search.models.responsemodels.nba.NBAGameScheduleResponseModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed interface MLBTournamentAction {
    data object InitData: MLBTournamentAction
    data class SelectSeries(val gameList: List<MLBGameForSchedule>) : MLBTournamentAction
}

sealed interface MLBTournamentDelegate {
    data class ShowLeagueSchedule(val model: SportDecodableModel.MLBLeagueSchedule) : MLBTournamentDelegate
}

class MLBTournamentStore @AssistedInject constructor(
    private val nameProvider: TranslatedNameProvider,
    private val tournamentTeamsDeferred: CompletableDeferred<Map<String, List<Int?>>>,
    @Assisted val model: MLBTournamentDisplayModel,
    @Assisted val emitToParent: (MLBTournamentDelegate) -> Unit
) : BaseTournamentStore<MLBTournamentAction, MLBTournamentDisplayModel>(
    model, nameProvider, tournamentTeamsDeferred
) {
    private val _gameListTuple = MutableStateFlow<List<GameListEntry<MLBGameInfoForSchedule>>>(emptyList())
    val gameListTuple: StateFlow<List<GameListEntry<MLBGameInfoForSchedule>>> = _gameListTuple

    private val _seedIdPairList = MutableStateFlow<List<List<Pair<Int?, Int?>>>>(emptyList())
    val seedIdPairList: StateFlow<List<List<Pair<Int?, Int?>>>> = _seedIdPairList

    @AssistedFactory
    interface Factory {
        fun create(
            model: MLBTournamentDisplayModel,
            emitToParent: (MLBTournamentDelegate) -> Unit
        ) : MLBTournamentStore
    }

    override fun send(action: MLBTournamentAction) {
        when (action) {
            is MLBTournamentAction.InitData -> initData()
            is MLBTournamentAction.SelectSeries -> selectSeries(action.gameList)
        }
    }

    override fun initTournamentTeams(tournamentTeams: Map<String, List<Int?>>) {
        val displayModel = displayModel.value
        val leagueId = displayModel.leagueId
        val season = displayModel.season

        // 시드 순서를 유지해야해서 다음과 같은 로직 적용
        val firstRoundTeams = tournamentTeams["${leagueId}_${season}_16"] ?: emptyList()
        val nlFirstRoundTeams = firstRoundTeams.take(4)
        val alFirstRoundTeams = firstRoundTeams.takeLast(4)

        val secondRoundTeams = tournamentTeams["${leagueId}_${season}_8"] ?: emptyList()
        val nlSecondRoundTeams = secondRoundTeams.take(4)
        val alSecondRoundTeams = secondRoundTeams.takeLast(4)

        val thirdRoundTeams = tournamentTeams["${leagueId}_${season}_4"] ?: emptyList()
        val nlThirdRoundTeams = thirdRoundTeams.take(2)
        val alThirdRoundTeams = thirdRoundTeams.takeLast(2)

        val fourthRoundTeams = tournamentTeams["${leagueId}_${season}_2"] ?: emptyList()

        val nlFirstRoundPairedTeams = nlFirstRoundTeams.chunked(2)
        val alFirstRoundPairedTeams = alFirstRoundTeams.chunked(2)
        val nlSecondRoundPairedTeams = nlSecondRoundTeams.chunked(2)
        val alSecondRoundPairedTeams = alSecondRoundTeams.chunked(2)
        val nlThirdRoundPairedTeams = nlThirdRoundTeams.chunked(2)
        val alThirdRoundPairedTeams = alThirdRoundTeams.chunked(2)
        val fourthRoundPairedTeams = fourthRoundTeams.chunked(2)

        val games = displayModel.games.toMutableList()

        var (nlFirstRoundSeedTuple, nlFirstRound) = Util.collectRound(nlFirstRoundPairedTeams, games)
        var (alFirstRoundSeedTuple, alFirstRound) = Util.collectRound(alFirstRoundPairedTeams, games)
        // TournamentBracket화면에서 와일드카드 시리즈는 한시리즈를 비워놔야해서 추가
        nlFirstRound = nlFirstRound.toMutableList().apply {
            add(index= 1, null)
        }
        nlFirstRoundSeedTuple = nlFirstRoundSeedTuple.toMutableList().apply {
            add(index = 1, (null to null))
        }
        alFirstRound = alFirstRound.toMutableList().apply {
            add(index= 1, null)
        }
        alFirstRoundSeedTuple = alFirstRoundSeedTuple.toMutableList().apply {
            add(index = 1, (null to null))
        }

        val (nlSecondRoundSeedTuple, nlSecondRound) = Util.collectRound(nlSecondRoundPairedTeams, games)
        val (alSecondRoundSeedTuple, alSecondRound) = Util.collectRound(alSecondRoundPairedTeams, games)
        val (nlThirdRoundSeedTuple, nlThirdRound) = Util.collectRound(nlThirdRoundPairedTeams, games)
        val (alThirdRoundSeedTuple, alThirdRound) = Util.collectRound(alThirdRoundPairedTeams, games)
        val (fourthRoundSeedTuple, fourthRound) = Util.collectRound(fourthRoundPairedTeams, games)

        _gameListTuple.value = listOf(
            GameListEntry(title = "NL 와일드카드 시리즈", gameList = nlFirstRound),
            GameListEntry(title = "NL 디비전 시리즈", gameList = nlSecondRound),
            GameListEntry(title = "NL 챔피언십 시리즈", gameList = nlThirdRound),
            GameListEntry(title = "월드 시리즈", gameList = fourthRound),
            GameListEntry(title = "AL 챔피언십 시리즈", gameList = alThirdRound),
            GameListEntry(title = "AL 디비전 시리즈", gameList = alSecondRound),
            GameListEntry(title = "AL 와일드카드 시리즈", gameList = alFirstRound)
        )

        // gameListTuple에 추가되는 순서대로 추가
        _seedIdPairList.value = listOf(
            nlFirstRoundSeedTuple,
            nlSecondRoundSeedTuple,
            nlThirdRoundSeedTuple,
            fourthRoundSeedTuple,
            alThirdRoundSeedTuple,
            alSecondRoundSeedTuple,
            alFirstRoundSeedTuple
        )
    }

    private fun selectSeries(gameList: List<MLBGameForSchedule>) {
        val responseModel = MLBGameScheduleResponseModel(
            scheduleType = ScheduleType.TEAM_FLAT,
            schedule = gameList
        )

        val dataModel = SportDecodableModel.MLBLeagueSchedule(
            responseModel,
            ModelConverter.mlbLeagueScheduleConverter(responseModel)
        )

        emitToParent(MLBTournamentDelegate.ShowLeagueSchedule(dataModel))
    }
}