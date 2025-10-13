package com.moare.android.core.constants

import androidx.compose.ui.graphics.Color
import com.moare.android.features.search.models.models.mlb.MLBGameLineScore
import com.moare.android.ui.theme.Moare

object Constants {
    object Keys {
        const val EPL_PLAYER_DIC = "epl_player"
        const val LALIGA_PLAYER_DIC = "laliga_player"
        const val BUNDESLIGA_PLAYER_DIC = "bundesliga_player"
        const val LIGUE1_PLAYER_DIC = "ligue1_player"
        const val SERIEA_PLAYER_DIC = "seriea_player"
        const val MLS_PLAYER_DIC = "mls_player"
        const val NBA_PLAYER_DIC = "nba_player"
        const val NBA_TEAM_DIC = "nba_team"
        const val KBO_PLAYER_DIC = "kbo_player"
        const val KBO_TEAM_DIC = "kbo_team"
        const val MLB_PLAYER_DIC = "mlb_player"
        const val MLB_TEAM_DIC = "mlb_team"
        const val FOOTBALL_TEAM_DIC = "football_team"

        const val TOURNAMENT_TEAMS = "tournament_teams"
    }

    object Ids {
        // league
        const val EPL = 39
        const val LALIGA = 140
        const val BUNDESLIGA = 78
        const val LIGUE1 = 61
        const val SERIEA = 135
        const val MLS = 253
        const val NBA = 90001
        const val KBO = 90101
        const val MLB = 90102
        val FOOTBALL_LEAGUES = listOf(EPL, LALIGA, BUNDESLIGA, LIGUE1, SERIEA, MLS)
        const val CHAMPIONS_LEAGUE = 2
        const val EUROPA_LEAGUE = 3
        const val CONFERENCE_LEAGUE = 848
        const val FA_CUP = 45
        const val EFL_CUP = 48
        const val DFB_POKAL = 81
        const val COUPE_DE_FRANCE = 66
        const val COPA_DEL_REY = 143
        const val COPPA_ITALIA = 137
        val FOOTBALL_TOURNAMENT_LEAGUES = listOf(CHAMPIONS_LEAGUE, EUROPA_LEAGUE, CONFERENCE_LEAGUE, FA_CUP, EFL_CUP, DFB_POKAL, COUPE_DE_FRANCE, COPA_DEL_REY, COPPA_ITALIA)
        val FOOTBALL_ALL = FOOTBALL_LEAGUES + FOOTBALL_TOURNAMENT_LEAGUES

        object NBATeam {
            const val ATL = 1610612737
            const val BOS = 1610612738
            const val CLE = 1610612739
            const val NOP = 1610612740
            const val CHI = 1610612741
            const val DAL = 1610612742
            const val DEN = 1610612743
            const val GSW = 1610612744
            const val HOU = 1610612745
            const val LAC = 1610612746
            const val LAL = 1610612747
            const val MIA = 1610612748
            const val MIL = 1610612749
            const val MIN = 1610612750
            const val BKN = 1610612751
            const val NYK = 1610612752
            const val ORL = 1610612753
            const val IND = 1610612754
            const val PHI = 1610612755
            const val PHX = 1610612756
            const val POR = 1610612757
            const val SAC = 1610612758
            const val SAS = 1610612759
            const val OKC = 1610612760
            const val TOR = 1610612761
            const val UTA = 1610612762
            const val MEM = 1610612763
            const val WAS = 1610612764
            const val DET = 1610612765
            const val CHA = 1610612766
            val eastConference = listOf(CLE, BOS, NYK, IND, MIL, DET, ORL, ATL, CHI, MIA, TOR, BKN, PHI, CHA, WAS)
            val westConference = listOf(NOP, DAL, DEN, GSW, HOU, LAC, LAL, MIN, PHX, POR, SAC, SAS, OKC, UTA, MEM)
            val all = eastConference + westConference
        }

        // MLBTeamInfo의 abbreviation 사용
        object MLBTeam {
            const val ATH = 133
            const val PIT = 134
            const val SD = 135
            const val SEA = 136
            const val SF = 137
            const val STL = 138
            const val TB = 139
            const val TEX = 140
            const val TOR = 141
            const val MIN = 142
            const val PHI = 143
            const val ATL = 144
            const val CWS = 145
            const val MIA = 146
            const val NYY = 147
            const val MIL = 158
            const val LAA = 108
            const val AZ = 109
            const val BAL = 110
            const val BOS = 111
            const val CHC = 112
            const val CIN = 113
            const val CLE = 114
            const val COL = 115
            const val DET = 116
            const val HOU = 117
            const val KC = 118
            const val LAD = 119
            const val WSH = 120
            const val NYM = 121
            val alConference = listOf(ATH, SEA, TB, TEX, TOR, MIN, CWS, NYY, LAA, BAL, BOS, CLE, DET, HOU, KC)
            val nlConference = listOf(PIT, SD, SF, STL, PHI, ATL, MIA, MIL, AZ, CHC, CIN, COL, LAD, WSH, NYM)
            val all = alConference + nlConference
        }

        object MLSTeam {
            const val SEA = 1595
            const val JOS = 1596
            const val DAL = 1597
            const val ORL = 1598
            const val PHI = 1599
            const val HOU = 1600
            const val TOR = 1601
            const val YOR = 1602
            const val VAN = 1603
            const val NYK = 1604
            const val ANG = 1605
            const val SAL = 1606
            const val CHI = 1607
            const val ATL = 1608
            const val ENG = 1609
            const val COR = 1610
            const val KAN = 1611
            const val MIN = 1612
            const val COL = 1613
            const val MON = 1614
            const val UNI = 1615
            const val LAF = 1616
            const val POR = 1617
            const val CIN = 2242
            const val MIA = 9568
            const val NAS = 9569
            const val AUS = 16489
            const val CHA = 18310
            const val STL = 20787
            const val SAN = 25484
            val eastConference = listOf(ORL, PHI, TOR, YOR, NYK, CHI, ATL, ENG, COL, MON, UNI, CIN, MIA, NAS, CHA)
            val westConference = listOf(SEA, JOS, DAL, HOU, VAN, ANG, SAL, COR, KAN, MIN, LAF, POR, AUS, STL, SAN)
            val all = eastConference + westConference
        }

        object KBOTeam {
            const val LG = 5
            const val LT = 2
            const val HH = 4
            const val SS = 3
            const val KT = 9
            const val SK = 10
            const val HT = 6
            const val OB = 1
            const val NC = 8
            const val WO = 7
            val all = listOf(LG, LT, HH, SS, KT, SK, HT, OB, NC, WO)
        }

        // mlb league, division
        const val AMERICAN_LEAGUE = 103
        const val NATIONAL_LEAGUE = 104
        const val AMERICAN_LEAGUE_WEST = 200
        const val AMERICAN_LEAGUE_EAST = 201
        const val AMERICAN_LEAGUE_CENTRAL = 202
        const val NATIONAL_LEAGUE_WEST = 203
        const val NATIONAL_LEAGUE_EAST = 204
        const val NATIONAL_LEAGUE_CENTRAL = 205

        fun checkTeamId(leagueId: Int, teamId: Int?): Int? {
            return when (leagueId) {
                in Ids.FOOTBALL_ALL -> teamId
                Ids.NBA -> if (Ids.NBATeam.all.contains(teamId)) teamId else null
                Ids.MLB -> if (Ids.MLBTeam.all.contains(teamId)) teamId else null
                Ids.KBO -> if (Ids.KBOTeam.all.contains(teamId)) teamId else null
                else -> null
            }
        }
    }

    object GameStatus {
        object Football {
            const val NOT_STARTED = "NS"
            const val FIRST_HALF = "1H"
            const val HALF_TIME = "HT"
            const val SECOND_HALF = "2H"
            const val EXTRA_TIME = "ET" // 연장전
            const val BREAK_TIME = "BT" // 연장전 전반 후 휴식시간
            const val PENALTY_SHOOTOUT = "P" // 승부차기
            const val FINISHED = "FT"
            const val FINISHED_AFTER_EXTRA_TIME = "AET" // 승부차기 없이 연장전 후 경기 종료
            const val FINISHED_AFTER_PENALTY_SHOOTOUT = "PEN" // 승부차기 후 경기 종료
            const val POSTPONED = "PST"
            const val CANCELLED = "CANC"
            val LIVE_LIST = listOf(FIRST_HALF, HALF_TIME, SECOND_HALF, EXTRA_TIME, BREAK_TIME, PENALTY_SHOOTOUT)
            val FINISHED_LIST = listOf(FINISHED, FINISHED_AFTER_EXTRA_TIME, FINISHED_AFTER_PENALTY_SHOOTOUT)
        }

        object NBA {
            const val NOT_STARTED = 1
            const val LIVE = 2
            const val FINISHED = 3
        }

        object MLB {
            const val SCHEDULED = "Scheduled"
            const val WARMUP = "Warmup"
            const val PRE_GAME = "Pre-Game"
            const val LIVE = "In Progress"
            const val POSTPONED = "Postponed"
            const val RAIN = "Completed Early: Rain"
            const val GAME_OVER = "Game Over"
            const val FINAL = "Final"
            val BEFORE_GAME_LIST = listOf(SCHEDULED, WARMUP, PRE_GAME)
            val FINISHED_LIST = listOf(RAIN, GAME_OVER, FINAL)
        }

        object KBO {
            const val SCHEDULED = "1"
            const val LIVE = "2"
            const val FINAL = "3"
            const val CANCELED = "4"
        }

        fun gameStatusText(
            leagueId: Int,
            status: String,
            isResultOpened: Boolean = true
        ): String {
            return when (leagueId) {
                in Ids.FOOTBALL_ALL -> {
                    when (status) {
                        Football.NOT_STARTED -> StringConstants.GAME_NOT_STARTED_STR
                        Football.FIRST_HALF -> StringConstants.Football.GAME_FIRST_HALF_STR
                        Football.HALF_TIME -> StringConstants.Football.GAME_HALF_TIME_STR
                        Football.SECOND_HALF -> StringConstants.Football.GAME_SECOND_HALF_STR
                        in Football.FINISHED_LIST -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
                        else -> ""
                    }
                }
                Ids.NBA -> ""
                Ids.KBO -> {
                    when (status) {
                        KBO.SCHEDULED -> StringConstants.GAME_NOT_STARTED_STR
                        KBO.LIVE -> StringConstants.GAME_LIVE_STR
                        KBO.FINAL -> StringConstants.GAME_FINISHED_STR
                        KBO.CANCELED -> StringConstants.GAME_CANCELED_STR
                        else -> ""
                    }
                }
                else -> ""
            }
        }

        fun mlbGameStatusText(
            status: String,
            currentInning: String? = null,
            linescore: MLBGameLineScore? = null,
            isResultOpened: Boolean = true
        ): String {
            return when (status) {
                in MLB.BEFORE_GAME_LIST -> StringConstants.GAME_NOT_STARTED_STR
                MLB.LIVE -> {
                    currentInning
                        ?: if (linescore != null) {
                            "${linescore.currentInning}회${if (linescore.isTopInning) "초" else "말"}"
                        } else {
                            StringConstants.GAME_LIVE_STR
                        }
                }
                MLB.POSTPONED -> StringConstants.GAME_POSTPONED_STR
                in MLB.FINISHED_LIST -> if (isResultOpened) StringConstants.GAME_FINISHED_STR else StringConstants.RESULT_OPEN
                else -> ""
            }
        }

        fun isLive(leagueId: Int, status: String): Boolean {
            return when (leagueId) {
                in Ids.FOOTBALL_ALL -> Football.LIVE_LIST.contains(status)
                Ids.NBA -> false
                Ids.MLB -> status == MLB.LIVE
                Ids.KBO -> status == KBO.LIVE
                else -> false
            }
        }

        fun isBeforeGame(leagueId: Int, status: String): Boolean {
            return when (leagueId) {
                in Ids.FOOTBALL_ALL -> status == Football.NOT_STARTED
                Ids.NBA -> status == NBA.NOT_STARTED.toString()
                Ids.MLB -> MLB.BEFORE_GAME_LIST.contains(status)
                Ids.KBO -> status == KBO.SCHEDULED
                else -> false
            }
        }

        fun gameStatusColor(leagueId: Int, status: String): Color {
            return if (isLive(leagueId, status)) Moare else Color.Gray
        }
    }
}


























