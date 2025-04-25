package com.moare.android.core.constants

object Constants {
    object Keys {
        const val EPL_PLAYER_DIC = "epl_player"
        const val EPL_TEAM_DIC = "epl_team"
        const val LALIGA_PLAYER_DIC = "laliga_player"
        const val LALIGA_TEAM_DIC = "laliga_team"
        const val BUNDESLIGA_PLAYER_DIC = "bundesliga_player"
        const val BUNDESLIGA_TEAM_DIC = "bundesliga_team"
        const val LIGUE1_PLAYER_DIC = "ligue1_player"
        const val LIGUE1_TEAM_DIC = "ligue1_team"
        const val NBA_PLAYER_DIC = "nba_player"
        const val NBA_TEAM_DIC = "nba_team"
    }

    object Ids {
        // league
        const val EPL = 39
        const val LALIGA = 140
        const val BUNDESLIGA = 78
        const val LIGUE1 = 61
        const val NBA = 90001

        // nba team
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
    }

    object NBAGameStatus {
        const val NOT_STARTED = 1
        const val LIVE = 2
        const val FINISHED = 3
    }

    object FBGameStatus {
        const val NOT_STARTED = "NS"
        const val FIRST_HALF = "1H"
        const val HALF_TIME = "HT"
        const val SECOND_HALF = "2H"
        const val EXTRA_TIME = "ET" // 연장전
        const val BREAK_TIME = "BT" // 연장전 전반 후 휴식시간
        const val PENALTY_SHOOTOUT = "P" // 승부차기
        const val FINISHED = "FT"
        const val FINISHED_AFTER_EXTRA_TIME = "AET" // 승부차기 없이 연장전 후 경기 종료
        const val FINISHED_AFTER_PENALTY_SHOOTOUT = "PET" // 승부차기 후 경기 종료
        const val POSTPONED = "PST"
        const val CANCELLED = "CANC"
    }
}