package com.moare.android.core.constants

import com.moare.android.core.constants.StringConstants.Football.GAME_BREAK_TIME
import com.moare.android.core.constants.StringConstants.Football.GAME_EXTRA_TIME
import com.moare.android.core.constants.StringConstants.Football.GAME_FINISHED
import com.moare.android.core.constants.StringConstants.Football.GAME_FINISHED_AFTER_EXTRA_TIME
import com.moare.android.core.constants.StringConstants.Football.GAME_FINISHED_AFTER_PENALTY_SHOOTOUT
import com.moare.android.core.constants.StringConstants.Football.GAME_FIRST_HALF
import com.moare.android.core.constants.StringConstants.Football.GAME_HALF_TIME
import com.moare.android.core.constants.StringConstants.Football.GAME_PENALTY_SHOOTOUT
import com.moare.android.core.constants.StringConstants.Football.GAME_SECOND_HALF

object StringConstants {
    // TODO: 대문자로 바꾸기
    const val RESULT_OPEN = "결과 보기"
    const val RESULT_HIDE = "결과 숨기기"
    const val GAME_NOT_STARTED_STR = "경기 전"
    const val GAME_FINISHED_STR = "경기 종료"
    const val GAME_LIVE_STR = "경기중"
    const val GAME_POSTPONED_STR = "경기 연기"
    const val GAME_CANCELED_STR = "경기 취소"

    const val STANDINGS_FIRST_CATEGORY = "순위"
    val STATS_FIRST_CATEGORIES = listOf("공격 지표", "수비 지표", "공통 지표")

    const val GAME_STATS_FIRST_CATEGORY = "선수 이름"

    object Football {
        const val GAME_FIRST_HALF_STR = "전반전"
        const val GAME_HALF_TIME_STR = "전반 종료"
        const val GAME_SECOND_HALF_STR = "후반전"

        // TODO: Should move to Constants.FBGameStatus
        const val GAME_NOT_STARTED = "NS"
        const val GAME_FIRST_HALF = "1H"
        const val GAME_HALF_TIME = "HT"
        const val GAME_SECOND_HALF = "2H"
        const val GAME_EXTRA_TIME = "연장전" // 연장전
        const val GAME_BREAK_TIME = "BT" // 연장전 전반 후 휴식시간
        const val GAME_PENALTY_SHOOTOUT = "승부차기" // 승부차기
        const val GAME_FINISHED = "FT"
        const val GAME_FINISHED_AFTER_EXTRA_TIME = "AET" // 승부차기 없이 연장전 후 경기 종료
        const val GAME_FINISHED_AFTER_PENALTY_SHOOTOUT = "PET" // 승부차기 후 경기 종료
        const val GAME_POSTPONED = "경기 연기"
        const val GAME_CANCELLED = "경기 취소"
        val GAME_LIVE_LIST = listOf(GAME_FIRST_HALF, GAME_HALF_TIME, GAME_SECOND_HALF, GAME_EXTRA_TIME, GAME_BREAK_TIME, GAME_PENALTY_SHOOTOUT)
        val GAME_FINISHED_LIST = listOf(GAME_FINISHED, GAME_FINISHED_AFTER_EXTRA_TIME, GAME_FINISHED_AFTER_PENALTY_SHOOTOUT)

        val TEAM_STANDINGS_CATEGORIES = listOf("승점", "승", "무", "패", "경기수", "득점", "실점", "득실차", "홈성적", "원정성적")
        val TEAM_GROUP_STANDINGS_CATEGORIES = listOf("승점", "승", "무", "패", "경기수", "득점", "실점", "득실차")

        val PLAYER_STANDINGS_ATTACK_CATEGORIES = listOf("득점", "도움", "공격포인트", "슈팅", "유효슈팅", "키패스", "드리블 성공", "pk골")
        val PLAYER_STANDINGS_DEFEND_CATEGORIES = listOf("태클 시도", "볼 경합 성공")
        val PLAYER_STANDINGS_COMMON_CATEGORIES = listOf("패스 시도", "파울", "경고", "퇴장", "경기수", "선발출전", "교체출전", "출전시간(분)", "평균평점")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_ATTACK_CATEGORIES + PLAYER_STANDINGS_DEFEND_CATEGORIES + PLAYER_STANDINGS_COMMON_CATEGORIES

        // 보류: 세이브, 실점, 패널티 실패, 패널티 세이브
        val GAME_STATS_ATTACK_CATEGORIES = listOf("득점", "pk골", "도움", "슈팅", "유효슈팅", "키패스", "드리블 성공/시도(%)", "오프사이드")
        val GAME_STATS_DEFEND_CATEGORIES = listOf("태클 시도", "볼 경합 성공/시도(%)", "가로채기")
        val GAME_STATS_COMMON_CATEGORIES = listOf("패스 시도", "얻은 파울", "파울", "경고", "퇴장", "출전시간(분)", "평점")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_ATTACK_CATEGORIES + GAME_STATS_DEFEND_CATEGORIES + GAME_STATS_COMMON_CATEGORIES
        val GAME_STATS_CATEGORIES = listOf("출전시간(분)", "득점", "pk골", "도움", "", "슈팅", "유효슈팅", "패스 시도", "드리블\n성공/시도(%)", "", "태클 시도", "볼 경합\n성공/시도(%)", "인터셉트", "", "오프사이드", "파울 당함", "파울 범함", "경고", "퇴장")

        fun leagueNameStr(leagueId: Int): String {
            return when (leagueId) {
                Constants.Ids.EPL -> "EPL"
                Constants.Ids.LALIGA -> "라리가"
                Constants.Ids.BUNDESLIGA -> "분데스리가"
                Constants.Ids.SERIEA -> "세리에A"
                Constants.Ids.LIGUE1 -> "리그1"
                Constants.Ids.MLS -> "MLS"
                Constants.Ids.CHAMPIONS_LEAGUE -> "챔피언스리그"
                Constants.Ids.EUROPA_LEAGUE -> "유로파리그"
                Constants.Ids.CONFERENCE_LEAGUE -> "컨퍼런스리그"
                Constants.Ids.FA_CUP -> "FA컵"
                Constants.Ids.EFL_CUP -> "EFL컵"
                Constants.Ids.DFB_POKAL -> "DFB 포칼"
                Constants.Ids.COUPE_DE_FRANCE -> "쿠프 드 프랑스"
                Constants.Ids.COPA_DEL_REY -> "코파 델 레이"
                Constants.Ids.COPPA_ITALIA -> "코파 이탈리아"
                else -> ""
            }
        }
    }

    object NBA {
        const val GAME_SCHEDULED = 1
        const val GAME_LIVE = 2
        const val GAME_FINAL = 3

        const val GAME_QTR_1 = "1쿼터"
        const val GAME_QTR_2 = "2쿼터"
        const val GAME_QTR_3 = "3쿼터"
        const val GAME_QTR_4 = "4쿼터"
        const val GAME_OT_1 = "연장 1쿼터"
        const val GAME_OT_2 = "연장 2쿼터"
        const val GAME_OT_3 = "연장 3쿼터"

        val CONFERENCE_CATEGORY = listOf("서부", "동부")
        // TODO: 나중에 데이터 추가되면 카테고리 추가
//        val teamStandingsCategories = listOf("게임차", "승률", "승", "패", "경기수", "홈성적", "원정성적", "경기당 득점", "경기당 득실마진", "경기당 도움", "경기당 리바운드", "야투 성공률", "3점 성공률", "자유투 성공률", "경기당 블록", "경기당 스틸", "경기당 턴오버", "경기당 파울")
        val TEAM_STANDINGS_CATEGORIES = listOf("게임차", "승률", "승", "패", "경기수", "연속", "최근 10경기", "홈성적", "원정성적", "", "경기당 득점", "경기당\n득실마진", "경기당 도움", "경기당\n리바운드", "야투 성공률", "3점 성공률", "자유투\n성공률", "경기당 스틸", "경기당 블록", "경기당\n턴오버", "경기당 파울")

        val PLAYER_STANDINGS_ATTACK_CATEGORIES = listOf("경기당 득점", "경기당 도움", "경기당 공격 리바운드", "경기당 야투 시도", "경기당 야투 성공", "야투 성공률", "경기당 3점 시도", "경기당 3점 성공", "3점 성공률", "경기당 자유투 시도", "경기당 자유투 성공", "자유투 성공률")
        val PLAYER_STANDINGS_DEFEND_CATEGORIES = listOf("경기당 수비 리바운드", "경기당 블록", "경기당 스틸")
        val PLAYER_STANDINGS_COMMON_CATEGORIES = listOf("경기당 리바운드", "경기당 턴오버", "경기당 파울", "경기당 파울 유도", "경기당 피블록", "경기당 득실마진", "경기수", "경기당 출전시간", "출전 경기 승", "출전 경기 패", "출전 경기 승률", "트리플더블", "더블더블")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_ATTACK_CATEGORIES + PLAYER_STANDINGS_DEFEND_CATEGORIES + PLAYER_STANDINGS_COMMON_CATEGORIES

        val GAME_STATS_ATTACK_CATEGORIES = listOf("득점", "도움", "공격 리바운드", "야투 시도", "야투 성공", "야투 성공률", "3점 시도", "3점 성공", "3점 성공률", "자유투 시도", "자유투 성공", "자유투 성공률")
        val GAME_STATS_DEFEND_CATEGORIES = listOf("수비 리바운드", "블록", "스틸")
        val GAME_STATS_COMMON_CATEGORIES = listOf("리바운드", "턴오버", "파울", "득실마진", "출전시간")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_ATTACK_CATEGORIES + GAME_STATS_DEFEND_CATEGORIES + GAME_STATS_COMMON_CATEGORIES
        val GAME_STATS_CATEGORIES = listOf("출전시간", "득점", "도움", "리바운드", "", "야투\n성공/시도(성공률)", "3점\n성공/시도(성공률)", "자유투\n성공/시도(성공률)", "", "스틸", "블록", "", "턴오버", "파울", "", "공격/수비\n리바운드", "득실마진")
    }

    object KBO {
        const val GAME_SCHEDULED = 1
        const val GAME_LIVE = 2
        const val GAME_FINAL = 3
        const val GAME_CANCELED = 4

        val DIVISION_CATEGORY = listOf("내셔널 서부", "내셔널 동부", "내셔널 중부", "아메리칸 서부", "아메리칸 동부", "아메리칸 중부")

        val TEAM_STANDINGS_CATEGORIES = listOf("게임차", "승률", "승", "패", "경기수", "연속", "타율", "안타", "홈런", "장타율", "득점", "평균자책", "피안타율", "피안타", "피홈런", "실점", "도루성공률")

        val PLAYER_STANDINGS_HITTING_CATEGORIES = listOf("경기당 득점", "경기당 도움", "경기당 공격 리바운드", "경기당 야투 시도", "경기당 야투 성공", "야투 성공률", "경기당 3점 시도", "경기당 3점 성공", "3점 성공률", "경기당 자유투 시도", "경기당 자유투 성공", "자유투 성공률")
        val PLAYER_STANDINGS_PITCHING_CATEGORIES = listOf("경기당 수비 리바운드", "경기당 블록", "경기당 스틸")
        val PLAYER_STANDINGS_RUNNING_CATEGORIES = listOf("경기당 리바운드", "경기당 턴오버", "경기당 파울", "경기당 파울 유도", "경기당 피블록", "경기당 득실마진", "경기수", "경기당 출전시간", "출전 경기 승", "출전 경기 패", "출전 경기 승률", "트리플더블", "더블더블")
        val PLAYER_STANDINGS_FIELDING_CATEGORIES = listOf("")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_HITTING_CATEGORIES + PLAYER_STANDINGS_PITCHING_CATEGORIES + PLAYER_STANDINGS_RUNNING_CATEGORIES

//        val GAME_STATS_HITTING_CATEGORIES = listOf("타수", "안타", "2루타", "홈런", "타점", "득점", "볼넷", "삼진", "병살타", "사구")
        val GAME_STATS_HITTING_CATEGORIES = listOf("타수", "안타", "홈런", "타점", "득점", "볼넷", "삼진", "병살타")
        val GAME_STATS_PITCHING_CATEGORIES = listOf("이닝", "실점", "자책", "볼넷", "삼진", "피안타")
        val GAME_STATS_RUNNING_CATEGORIES = listOf("")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_HITTING_CATEGORIES + GAME_STATS_PITCHING_CATEGORIES + GAME_STATS_RUNNING_CATEGORIES
    }

    object MLB {
        val CONFERENCE_CATEGORY = listOf("내셔널리그", "아메리칸리그")

        val DIVISION_CATEGORY = listOf("내셔널 서부", "내셔널 동부", "내셔널 중부", "아메리칸 서부", "아메리칸 동부", "아메리칸 중부")

        val TEAM_STANDINGS_CATEGORIES = listOf("게임차", "승률", "승", "패", "경기수", "연속", "타율", "안타", "홈런", "장타율", "득점", "평균자책", "피안타율", "피안타", "피홈런", "실점", "도루성공률")

        val PLAYER_STANDINGS_HITTING_CATEGORIES = listOf("경기당 득점", "경기당 도움", "경기당 공격 리바운드", "경기당 야투 시도", "경기당 야투 성공", "야투 성공률", "경기당 3점 시도", "경기당 3점 성공", "3점 성공률", "경기당 자유투 시도", "경기당 자유투 성공", "자유투 성공률")
        val PLAYER_STANDINGS_PITCHING_CATEGORIES = listOf("경기당 수비 리바운드", "경기당 블록", "경기당 스틸")
        val PLAYER_STANDINGS_RUNNING_CATEGORIES = listOf("")
        val PLAYER_STANDINGS_FIELDING_CATEGORIES = listOf("")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_HITTING_CATEGORIES + PLAYER_STANDINGS_PITCHING_CATEGORIES + PLAYER_STANDINGS_RUNNING_CATEGORIES

        val GAME_STATS_HITTING_CATEGORIES = listOf("타수", "안타", "홈런", "타점", "득점", "도루", "볼넷", "삼진")
        val GAME_STATS_PITCHING_CATEGORIES = listOf("이닝", "실점", "자책", "볼넷", "삼진", "피안타")
        val GAME_STATS_RUNNING_CATEGORIES = listOf("")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_HITTING_CATEGORIES + GAME_STATS_PITCHING_CATEGORIES + GAME_STATS_RUNNING_CATEGORIES
    }

    object Tennis {
        fun leagueNameStr(leagueId: Int): String {
            return "${tournamentNameStr(leagueId)} ${relatedLeaguesKrName(leagueId) ?: ""}"
        }

        fun tournamentNameStr(leagueId: Int): String {
            return when (leagueId) {
                in Constants.Ids.AUS_OPEN_ALL -> "호주오픈"
                in Constants.Ids.ROLAND_GARROS_ALL -> "롤랑가로스"
                in Constants.Ids.WIMBLEDON_ALL -> "윔블던"
                in Constants.Ids.US_OPEN_ALL -> "US 오픈"
                in Constants.Ids.INDIAN_WELLS_ALL -> "인디언웰스 마스터스"
                in Constants.Ids.MIAMI_ALL -> "마이애미 마스터스"
                in Constants.Ids.MONTE_CARLO_ALL -> "몬테카를로 마스터스"
                in Constants.Ids.MADRID_ALL -> "마드리드 오픈"
                in Constants.Ids.ROME_ALL -> "로마 오픈"
                in Constants.Ids.TORONTO_ALL -> "캐나다 오픈"
                in Constants.Ids.MONTREAL_ALL -> "캐나다 오픈"
                in Constants.Ids.CINCINNATI_ALL -> "신시내티 마스터스"
                in Constants.Ids.SHANGHAI_ALL -> "상하이 마스터스"
                in Constants.Ids.PARIS_ALL -> "파리 마스터스"
                in Constants.Ids.DOHA_ALL -> "카타르 오픈"
                in Constants.Ids.DUBAI_ALL -> "두바이 챔피언십"
                in Constants.Ids.BEIJING_ALL -> "차이나 오픈"
                in Constants.Ids.WUHAN_ALL -> "우한 오픈"
                else -> ""
            }
        }

        val playerStatKeyList = listOf("aces", "doubleFaults", "firstServeAccuracy", "firstServePointsAccuracy", "secondServePointsAccuracy", "breakPointsSaved", "pointsTotal", "servicePointsScored", "receiverPointsScored", "gamesWon", "serviceGamesWon", "winnersTotal", "forehandWinners", "backhandWinners", "errorsTotal", "unforcedErrorsTotal")
        val playerStatKrnameMap = mapOf(
            "aces" to "에이스",
            "doubleFaults" to "더블 폴트",
            "firstServeAccuracy" to "1st 서브 성공",
            "firstServePointsAccuracy" to "1st 서브 득점",
            "secondServePointsAccuracy" to "2nd 서브 득점",
            "breakPointsSaved" to "브레이크 포인트",
            "pointsTotal" to "총 포인트",
            "servicePointsScored" to "서브 포인트",
            "receiverPointsScored" to "리턴 포인트",
            "gamesWon" to "이긴 게임",
            "serviceGamesWon" to "이긴 서브 게임",
            "winnersTotal" to "위너",
            "forehandWinners" to "포핸드 위너",
            "backhandWinners" to "백핸드 위너",
            "errorsTotal" to "실책",
            "unforcedErrorsTotal" to "자책(Unforced errors)"
        )

        fun relatedLeaguesKrName(leagueId: Int): String? {
            return when (leagueId) {
                in Constants.Ids.M_SINGLE_ALL -> "남자 단식"
                in Constants.Ids.W_SINGLE_ALL -> "여자 단식"
                in Constants.Ids.M_DOUBLES_ALL -> "남자 복식"
                in Constants.Ids.W_DOUBLES_ALL -> "여자 복식"
                in Constants.Ids.MIXED_DOUBLES_ALL -> "혼합 복식"
                else -> null
            }
        }

        fun relatedLeagueRank(leagueId: Int): Int {
            return when (leagueId) {
                in Constants.Ids.M_SINGLE_ALL -> 0
                in Constants.Ids.W_SINGLE_ALL -> 1
                in Constants.Ids.M_DOUBLES_ALL -> 2
                in Constants.Ids.W_DOUBLES_ALL -> 3
                in Constants.Ids.MIXED_DOUBLES_ALL -> 4
                else -> 999
            }
        }

        fun groundTypeKr(groundType: String?): String {
            val groundType = groundType ?: return ""

            if (groundType.lowercase().contains("hardcourt")) {
                return "하드"
            }
            if (groundType.lowercase().contains("grass")) {
                return "잔디"
            }
            if (groundType.lowercase().contains("clay")) {
                return "클레이"
            }
            return groundType
        }
    }

    fun viewPreparingAdviseText(type: String): String {
        return "${type} 화면은 더 나은 서비스 제공을 위해 현재 개선 작업 중입니다. 이용에 불편을 드려 죄송합니다."
    }

    fun tournamentButtonText(leagueId: Int): String {
        return when (leagueId) {
            Constants.Ids.MLS -> "플레이오프 대진표"
            Constants.Ids.NBA -> "플레이오프 대진표"
            Constants.Ids.MLB -> "포스트시즌 대진표"
            Constants.Ids.KBO -> "가을야구 대진표"
            else -> "대진표"
        }
    }

    fun tournamentOrStandingsText(leagueId: Int): String {
        return if (Constants.Ids.FOOTBALL_DRAW_TOURNAMENT_LEAGUES.contains(leagueId) || Constants.Ids.TENNIS_ALL.contains(leagueId)) {
            "대진표"
        } else {
            "리그 순위"
        }
    }
}
















