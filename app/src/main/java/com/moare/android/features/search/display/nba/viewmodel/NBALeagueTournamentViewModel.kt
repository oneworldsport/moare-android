package com.moare.android.features.search.display.nba.viewmodel

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.moare.android.core.constants.Constants
import com.moare.android.core.di.TranslatedNameProvider
import com.moare.android.core.mvi.MVIViewModel
import com.moare.android.features.search.models.displaymodels.nba.NBALeagueScheduleDisplayModel
import com.moare.android.features.search.models.models.nba.NBAGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NBALeagueTournamentViewModel @Inject constructor(
    private val nameProvider: TranslatedNameProvider
) : MVIViewModel<NBALeagueTournamentViewModel.Intent, NBALeagueScheduleDisplayModel>() {
    /* ---------------------
       constants
       --------------------- */
    val infoContainerWidth = 130.dp
    val hBarWidth = 30.dp
    val barThickness = 1.dp
    val secondRoundContainerSpace = (infoContainerWidth + (hBarWidth * 2) + (barThickness * 2)) - (infoContainerWidth / 2)
    val finalRoundContainerSpace = secondRoundContainerSpace * 2

    /* ---------------------
       data state
       --------------------- */
    private val _displayModel = MutableStateFlow<NBALeagueScheduleDisplayModel?>(null)
    val displayModel: StateFlow<NBALeagueScheduleDisplayModel?> = _displayModel

    private val _westFirstRoundFirstGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westFirstRoundFirstGameList: StateFlow<List<NBAGame>?> = _westFirstRoundFirstGameList
    private val _westFirstRoundFirstGameFirstTeamId = MutableStateFlow(Constants.Ids.OKC)
    val westFirstRoundFirstGameFirstTeamId: StateFlow<Int> = _westFirstRoundFirstGameFirstTeamId
    private val _westFirstRoundFirstGameSecondTeamId = MutableStateFlow(Constants.Ids.MEM)
    val westFirstRoundFirstGameSecondTeamId: StateFlow<Int> = _westFirstRoundFirstGameSecondTeamId

    private val _westFirstRoundSecondGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westFirstRoundSecondGameList: StateFlow<List<NBAGame>?> = _westFirstRoundSecondGameList
    private val _westFirstRoundSecondGameFirstTeamId = MutableStateFlow(Constants.Ids.DEN)
    val westFirstRoundSecondGameFirstTeamId: StateFlow<Int> = _westFirstRoundSecondGameFirstTeamId
    private val _westFirstRoundSecondGameSecondTeamId = MutableStateFlow(Constants.Ids.LAC)
    val westFirstRoundSecondGameSecondTeamId: StateFlow<Int> = _westFirstRoundSecondGameSecondTeamId

    private val _westFirstRoundThirdGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westFirstRoundThirdGameList: StateFlow<List<NBAGame>?> = _westFirstRoundThirdGameList
    private val _westFirstRoundThirdGameFirstTeamId = MutableStateFlow(Constants.Ids.LAL)
    val westFirstRoundThirdGameFirstTeamId: StateFlow<Int> = _westFirstRoundThirdGameFirstTeamId
    private val _westFirstRoundThirdGameSecondTeamId = MutableStateFlow(Constants.Ids.MIN)
    val westFirstRoundThirdGameSecondTeamId: StateFlow<Int> = _westFirstRoundThirdGameSecondTeamId

    private val _westFirstRoundFourthGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westFirstRoundFourthGameList: StateFlow<List<NBAGame>?> = _westFirstRoundFourthGameList
    private val _westFirstRoundFourthGameFirstTeamId = MutableStateFlow(Constants.Ids.HOU)
    val westFirstRoundFourthGameFirstTeamId: StateFlow<Int> = _westFirstRoundFourthGameFirstTeamId
    private val _westFirstRoundFourthGameSecondTeamId = MutableStateFlow(Constants.Ids.GSW)
    val westFirstRoundFourthGameSecondTeamId: StateFlow<Int> = _westFirstRoundFourthGameSecondTeamId

    private val _eastFirstRoundFirstGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastFirstRoundFirstGameList: StateFlow<List<NBAGame>?> = _eastFirstRoundFirstGameList
    private val _eastFirstRoundFirstGameFirstTeamId = MutableStateFlow(Constants.Ids.CLE)
    val eastFirstRoundFirstGameFirstTeamId: StateFlow<Int> = _eastFirstRoundFirstGameFirstTeamId
    private val _eastFirstRoundFirstGameSecondTeamId = MutableStateFlow(Constants.Ids.MIA)
    val eastFirstRoundFirstGameSecondTeamId: StateFlow<Int> = _eastFirstRoundFirstGameSecondTeamId

    private val _eastFirstRoundSecondGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastFirstRoundSecondGameList: StateFlow<List<NBAGame>?> = _eastFirstRoundSecondGameList
    private val _eastFirstRoundSecondGameFirstTeamId = MutableStateFlow(Constants.Ids.IND)
    val eastFirstRoundSecondGameFirstTeamId: StateFlow<Int> = _eastFirstRoundSecondGameFirstTeamId
    private val _eastFirstRoundSecondGameSecondTeamId = MutableStateFlow(Constants.Ids.MIL)
    val eastFirstRoundSecondGameSecondTeamId: StateFlow<Int> = _eastFirstRoundSecondGameSecondTeamId

    private val _eastFirstRoundThirdGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastFirstRoundThirdGameList: StateFlow<List<NBAGame>?> = _eastFirstRoundThirdGameList
    private val _eastFirstRoundThirdGameFirstTeamId = MutableStateFlow(Constants.Ids.NYK)
    val eastFirstRoundThirdGameFirstTeamId: StateFlow<Int> = _eastFirstRoundThirdGameFirstTeamId
    private val _eastFirstRoundThirdGameSecondTeamId = MutableStateFlow(Constants.Ids.DET)
    val eastFirstRoundThirdGameSecondTeamId: StateFlow<Int> = _eastFirstRoundThirdGameSecondTeamId

    private val _eastFirstRoundFourthGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastFirstRoundFourthGameList: StateFlow<List<NBAGame>?> = _eastFirstRoundFourthGameList
    private val _eastFirstRoundFourthGameFirstTeamId = MutableStateFlow(Constants.Ids.BOS)
    val eastFirstRoundFourthGameFirstTeamId: StateFlow<Int> = _eastFirstRoundFourthGameFirstTeamId
    private val _eastFirstRoundFourthGameSecondTeamId = MutableStateFlow(Constants.Ids.ORL)
    val eastFirstRoundFourthGameSecondTeamId: StateFlow<Int> = _eastFirstRoundFourthGameSecondTeamId

    private val _westSecondRoundFirstGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westSecondRoundFirstGameList: StateFlow<List<NBAGame>?> = _westSecondRoundFirstGameList
    private val _westSecondRoundFirstGameFirstTeamId = MutableStateFlow<Int?>(null)
    val westSecondRoundFirstGameFirstTeamId: StateFlow<Int?> = _westSecondRoundFirstGameFirstTeamId
    private val _westSecondRoundFirstGameSecondTeamId = MutableStateFlow<Int?>(null)
    val westSecondRoundFirstGameSecondTeamId: StateFlow<Int?> = _westSecondRoundFirstGameSecondTeamId

    private val _westSecondRoundSecondGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westSecondRoundSecondGameList: StateFlow<List<NBAGame>?> = _westSecondRoundSecondGameList
    private val _westSecondRoundSecondGameFirstTeamId = MutableStateFlow<Int?>(null)
    val westSecondRoundSecondGameFirstTeamId: StateFlow<Int?> = _westSecondRoundSecondGameFirstTeamId
    private val _westSecondRoundSecondGameSecondTeamId = MutableStateFlow<Int?>(null)
    val westSecondRoundSecondGameSecondTeamId: StateFlow<Int?> = _westSecondRoundSecondGameSecondTeamId

    private val _eastSecondRoundFirstGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastSecondRoundFirstGameList: StateFlow<List<NBAGame>?> = _eastSecondRoundFirstGameList
    private val _eastSecondRoundFirstGameFirstTeamId = MutableStateFlow<Int?>(null)
    val eastSecondRoundFirstGameFirstTeamId: StateFlow<Int?> = _eastSecondRoundFirstGameFirstTeamId
    private val _eastSecondRoundFirstGameSecondTeamId = MutableStateFlow<Int?>(null)
    val eastSecondRoundFirstGameSecondTeamId: StateFlow<Int?> = _eastSecondRoundFirstGameSecondTeamId

    private val _eastSecondRoundSecondGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastSecondRoundSecondGameList: StateFlow<List<NBAGame>?> = _eastSecondRoundSecondGameList
    private val _eastSecondRoundSecondGameFirstTeamId = MutableStateFlow<Int?>(null)
    val eastSecondRoundSecondGameFirstTeamId: StateFlow<Int?> = _eastSecondRoundSecondGameFirstTeamId
    private val _eastSecondRoundSecondGameSecondTeamId = MutableStateFlow<Int?>(null)
    val eastSecondRoundSecondGameSecondTeamId: StateFlow<Int?> = _eastSecondRoundSecondGameSecondTeamId

    private val _westFinalRoundGameList = MutableStateFlow<List<NBAGame>?>(null)
    val westFinalRoundGameList: StateFlow<List<NBAGame>?> = _westFinalRoundGameList
    private val _westFinalRoundGameFirstTeamId = MutableStateFlow<Int?>(null)
    val westFinalRoundGameFirstTeamId: StateFlow<Int?> = _westFinalRoundGameFirstTeamId
    private val _westFinalRoundGameSecondTeamId = MutableStateFlow<Int?>(null)
    val westFinalRoundGameSecondTeamId: StateFlow<Int?> = _westFinalRoundGameSecondTeamId

    private val _eastFinalRoundGameList = MutableStateFlow<List<NBAGame>?>(null)
    val eastFinalRoundGameList: StateFlow<List<NBAGame>?> = _eastFinalRoundGameList
    private val _eastFinalRoundGameFirstTeamId = MutableStateFlow<Int?>(null)
    val eastFinalRoundGameFirstTeamId: StateFlow<Int?> = _eastFinalRoundGameFirstTeamId
    private val _eastFinalRoundGameSecondTeamId = MutableStateFlow<Int?>(null)
    val eastFinalRoundGameSecondTeamId: StateFlow<Int?> = _eastFinalRoundGameSecondTeamId

    private val _finalRoundGameList = MutableStateFlow<List<NBAGame>?>(null)
    val finalRoundGameList: StateFlow<List<NBAGame>?> = _finalRoundGameList
    private val _finalRoundGameFirstTeamId = MutableStateFlow<Int?>(null)
    val finalRoundGameFirstTeamId: StateFlow<Int?> = _finalRoundGameFirstTeamId
    private val _finalRoundGameSecondTeamId = MutableStateFlow<Int?>(null)
    val finalRoundGameSecondTeamId: StateFlow<Int?> = _finalRoundGameSecondTeamId

    /* ---------------------
       etc
       --------------------- */
    var teamNameDictionary: Map<String, String> = emptyMap()

    init {
        teamNameDictionary = nameProvider.getDictionary("nba_team")
    }

    /* ---------------------
       intent
       --------------------- */
    sealed class Intent {
        data class InitData(val displayModel: NBALeagueScheduleDisplayModel) : Intent()
    }

    override fun send(intent: Intent) {
        when (intent) {
            is Intent.InitData -> initData(intent.displayModel)
        }
    }

    /* ---------------------
       init
       --------------------- */
    override fun initData(displayModel: NBALeagueScheduleDisplayModel) {
        // init data
        _displayModel.value = displayModel

        // western first round
        _westFirstRoundFirstGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.OKC && it.gameSummary.visitorTeamId == Constants.Ids.MEM) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.MEM && it.gameSummary.visitorTeamId == Constants.Ids.OKC)
        }
        _westFirstRoundSecondGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.DEN && it.gameSummary.visitorTeamId == Constants.Ids.LAC) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.LAC && it.gameSummary.visitorTeamId == Constants.Ids.DEN)
        }
        _westFirstRoundThirdGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.LAL && it.gameSummary.visitorTeamId == Constants.Ids.MIN) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.MIN && it.gameSummary.visitorTeamId == Constants.Ids.LAL)
        }
        _westFirstRoundFourthGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.HOU && it.gameSummary.visitorTeamId == Constants.Ids.GSW) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.GSW && it.gameSummary.visitorTeamId == Constants.Ids.HOU)
        }

        // eastern first round
        _eastFirstRoundFirstGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.CLE && it.gameSummary.visitorTeamId == Constants.Ids.MIA) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.MIA && it.gameSummary.visitorTeamId == Constants.Ids.CLE)
        }
        _eastFirstRoundSecondGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.IND && it.gameSummary.visitorTeamId == Constants.Ids.MIL) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.MIL && it.gameSummary.visitorTeamId == Constants.Ids.IND)
        }
        _eastFirstRoundThirdGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.NYK && it.gameSummary.visitorTeamId == Constants.Ids.DET) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.DET && it.gameSummary.visitorTeamId == Constants.Ids.NYK)
        }
        _eastFirstRoundFourthGameList.value = displayModel.games.filter {
            (it.gameSummary?.homeTeamId == Constants.Ids.BOS && it.gameSummary.visitorTeamId == Constants.Ids.ORL) ||
                    (it.gameSummary?.homeTeamId == Constants.Ids.ORL && it.gameSummary.visitorTeamId == Constants.Ids.BOS)
        }

        // western second round
        getGameSeries(westFirstRoundFirstGameList.value)?.let { firstGameSeries ->
            // western second round first game first team id
            if (firstGameSeries.seasonSeries?.homeTeamWins == 4) {
                _westSecondRoundFirstGameFirstTeamId.value = firstGameSeries.seasonSeries.homeTeamId
            } else if (firstGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _westSecondRoundFirstGameFirstTeamId.value = firstGameSeries.seasonSeries.visitorTeamId
            }

            // western second round first game second team id
            getGameSeries(westFirstRoundSecondGameList.value)?.let { secondGameSeries ->
                if (secondGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _westSecondRoundFirstGameSecondTeamId.value = secondGameSeries.seasonSeries.homeTeamId
                } else if (secondGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _westSecondRoundFirstGameSecondTeamId.value = secondGameSeries.seasonSeries.visitorTeamId
                }
            }

            westSecondRoundFirstGameFirstTeamId.value?.let { first ->
                westSecondRoundFirstGameSecondTeamId.value?.let {  second ->
                    _westSecondRoundFirstGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }
        getGameSeries(westFirstRoundThirdGameList.value)?.let { thirdGameSeries ->
            // western second round second game first team id
            if (thirdGameSeries.seasonSeries?.homeTeamWins == 4) {
                _westSecondRoundSecondGameFirstTeamId.value = thirdGameSeries.seasonSeries.homeTeamId
            } else if (thirdGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _westSecondRoundSecondGameFirstTeamId.value = thirdGameSeries.seasonSeries.visitorTeamId
            }

            // western second round second game second team id
            getGameSeries(westFirstRoundFourthGameList.value)?.let { fourthGameSeries ->
                if (fourthGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _westSecondRoundSecondGameSecondTeamId.value = fourthGameSeries.seasonSeries.homeTeamId
                } else if (fourthGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _westSecondRoundSecondGameSecondTeamId.value = fourthGameSeries.seasonSeries.visitorTeamId
                }
            }

            westSecondRoundSecondGameFirstTeamId.value?.let { first ->
                westSecondRoundSecondGameSecondTeamId.value?.let {  second ->
                    _westSecondRoundSecondGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }

        // eastern second round
        getGameSeries(eastFirstRoundFirstGameList.value)?.let { firstGameSeries ->
            // eastern second round first game first team id
            if (firstGameSeries.seasonSeries?.homeTeamWins == 4) {
                _eastSecondRoundFirstGameFirstTeamId.value = firstGameSeries.seasonSeries.homeTeamId
            } else if (firstGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _eastSecondRoundFirstGameFirstTeamId.value = firstGameSeries.seasonSeries.visitorTeamId
            }

            // eastern second round first game second team id
            getGameSeries(eastFirstRoundSecondGameList.value)?.let { secondGameSeries ->
                if (secondGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _eastSecondRoundFirstGameSecondTeamId.value = secondGameSeries.seasonSeries.homeTeamId
                } else if (secondGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _eastSecondRoundFirstGameSecondTeamId.value = secondGameSeries.seasonSeries.visitorTeamId
                }
            }

            eastSecondRoundFirstGameFirstTeamId.value?.let { first ->
                eastSecondRoundFirstGameSecondTeamId.value?.let { second ->
                    _eastSecondRoundFirstGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }
        getGameSeries(eastFirstRoundThirdGameList.value)?.let { thirdGameSeries ->
            // eastern second round second game first team id
            if (thirdGameSeries.seasonSeries?.homeTeamWins == 4) {
                _eastSecondRoundSecondGameFirstTeamId.value = thirdGameSeries.seasonSeries.homeTeamId
            } else if (thirdGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _eastSecondRoundSecondGameFirstTeamId.value = thirdGameSeries.seasonSeries.visitorTeamId
            }

            // eastern second round second game second team id
            getGameSeries(eastFirstRoundFourthGameList.value)?.let { fourthGameSeries ->
                if (fourthGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _eastSecondRoundSecondGameSecondTeamId.value = fourthGameSeries.seasonSeries.homeTeamId
                } else if (fourthGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _eastSecondRoundSecondGameSecondTeamId.value = fourthGameSeries.seasonSeries.visitorTeamId
                }
            }

            eastSecondRoundSecondGameFirstTeamId.value?.let { first ->
                eastSecondRoundSecondGameSecondTeamId.value?.let {  second ->
                    _eastSecondRoundSecondGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }

        // western final round
        getGameSeries(westSecondRoundFirstGameList.value)?.let { firstGameSeries ->
            // western final round first team id
            if (firstGameSeries.seasonSeries?.homeTeamWins == 4) {
                _westFinalRoundGameFirstTeamId.value = firstGameSeries.seasonSeries.homeTeamId
            } else if (firstGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _westFinalRoundGameFirstTeamId.value = firstGameSeries.seasonSeries.visitorTeamId
            }

            // western final round second team id
            getGameSeries(westSecondRoundSecondGameList.value)?.let { secondGameSeries ->
                if (secondGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _westFinalRoundGameSecondTeamId.value = secondGameSeries.seasonSeries.homeTeamId
                } else if (secondGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _westFinalRoundGameSecondTeamId.value = secondGameSeries.seasonSeries.visitorTeamId
                }
            }

            westFinalRoundGameFirstTeamId.value?.let { first ->
                westFinalRoundGameSecondTeamId.value?.let { second ->
                    _westFinalRoundGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }

        // eastern final round
        getGameSeries(eastSecondRoundFirstGameList.value)?.let { firstGameSeries ->
            // eastern final round first team id
            if (firstGameSeries.seasonSeries?.homeTeamWins == 4) {
                _eastFinalRoundGameFirstTeamId.value = firstGameSeries.seasonSeries.homeTeamId
            } else if (firstGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _eastFinalRoundGameFirstTeamId.value = firstGameSeries.seasonSeries.visitorTeamId
            }

            // eastern final round second team id
            getGameSeries(eastSecondRoundSecondGameList.value)?.let { secondGameSeries ->
                if (secondGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _eastFinalRoundGameSecondTeamId.value = secondGameSeries.seasonSeries.homeTeamId
                } else if (secondGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _eastFinalRoundGameSecondTeamId.value = secondGameSeries.seasonSeries.visitorTeamId
                }
            }

            eastFinalRoundGameFirstTeamId.value?.let { first ->
                eastFinalRoundGameSecondTeamId.value?.let { second ->
                    _eastFinalRoundGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }

        // final round
        getGameSeries(westFinalRoundGameList.value)?.let { westGameSeries ->
            // final round first team id
            if (westGameSeries.seasonSeries?.homeTeamWins == 4) {
                _finalRoundGameFirstTeamId.value = westGameSeries.seasonSeries.homeTeamId
            } else if (westGameSeries.seasonSeries?.homeTeamLosses == 4) {
                _finalRoundGameFirstTeamId.value = westGameSeries.seasonSeries.visitorTeamId
            }

            // final round second team id
            getGameSeries(eastFinalRoundGameList.value)?.let { eastGameSeries ->
                if (eastGameSeries.seasonSeries?.homeTeamWins == 4) {
                    _finalRoundGameSecondTeamId.value = eastGameSeries.seasonSeries.homeTeamId
                } else if (eastGameSeries.seasonSeries?.homeTeamLosses == 4) {
                    _finalRoundGameSecondTeamId.value = eastGameSeries.seasonSeries.visitorTeamId
                }
            }

            finalRoundGameFirstTeamId.value?.let { first ->
                finalRoundGameSecondTeamId.value?.let { second ->
                    _finalRoundGameList.value = displayModel.games.filter {
                        (it.gameSummary?.homeTeamId == first && it.gameSummary.visitorTeamId == second) ||
                                (it.gameSummary?.homeTeamId == second && it.gameSummary.visitorTeamId == first)
                    }
                }
            }
        }
    }

    fun getGameSeries(gameList: List<NBAGame>?): NBAGame? {
        return gameList?.maxBy { (it.seasonSeries?.homeTeamWins ?: 0) + (it.seasonSeries?.homeTeamLosses ?: 0) }
    }
}




















