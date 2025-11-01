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
        const val GAME_EXTRA_TIME = "ET" // 연장전
        const val GAME_BREAK_TIME = "BT" // 연장전 전반 후 휴식시간
        const val GAME_PENALTY_SHOOTOUT = "P" // 승부차기
        const val GAME_FINISHED = "FT"
        const val GAME_FINISHED_AFTER_EXTRA_TIME = "AET" // 승부차기 없이 연장전 후 경기 종료
        const val GAME_FINISHED_AFTER_PENALTY_SHOOTOUT = "PET" // 승부차기 후 경기 종료
        const val GAME_POSTPONED = "PST"
        const val GAME_CANCELLED = "CANC"
        val GAME_LIVE_LIST = listOf(GAME_FIRST_HALF, GAME_HALF_TIME, GAME_SECOND_HALF, GAME_EXTRA_TIME, GAME_BREAK_TIME, GAME_PENALTY_SHOOTOUT)
        val GAME_FINISHED_LIST = listOf(GAME_FINISHED, GAME_FINISHED_AFTER_EXTRA_TIME, GAME_FINISHED_AFTER_PENALTY_SHOOTOUT)

        val TEAM_STANDINGS_CATEGORIES = listOf("승점", "승", "무", "패", "경기수", "득점", "실점", "득실차", "홈성적", "원정성적")

        val PLAYER_STANDINGS_ATTACK_CATEGORIES = listOf("득점", "도움", "공격포인트", "슈팅", "유효슈팅", "키패스", "드리블 성공", "pk골")
        val PLAYER_STANDINGS_DEFEND_CATEGORIES = listOf("태클 시도", "볼 경합 성공")
        val PLAYER_STANDINGS_COMMON_CATEGORIES = listOf("패스 시도", "파울", "경고", "퇴장", "경기수", "선발출전", "교체출전", "출전시간(분)", "평균평점")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_ATTACK_CATEGORIES + PLAYER_STANDINGS_DEFEND_CATEGORIES + PLAYER_STANDINGS_COMMON_CATEGORIES

        // 보류: 세이브, 실점, 패널티 실패, 패널티 세이브
        val GAME_STATS_ATTACK_CATEGORIES = listOf("득점", "pk골", "도움", "슈팅", "유효슈팅", "키패스", "드리블 성공/시도(%)", "오프사이드")
        val GAME_STATS_DEFEND_CATEGORIES = listOf("태클 시도", "볼 경합 성공/시도(%)", "가로채기")
        val GAME_STATS_COMMON_CATEGORIES = listOf("패스 시도", "얻은 파울", "파울", "경고", "퇴장", "출전시간(분)", "평점")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_ATTACK_CATEGORIES + GAME_STATS_DEFEND_CATEGORIES + GAME_STATS_COMMON_CATEGORIES
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
        val TEAM_STANDINGS_CATEGORIES = listOf("게임차", "승률", "승", "패", "경기수", "경기당 득점", "경기당 득실마진", "경기당 도움", "경기당 리바운드", "야투 성공률", "3점 성공률", "자유투 성공률", "경기당 블록", "경기당 스틸", "경기당 턴오버", "경기당 파울")

        val PLAYER_STANDINGS_ATTACK_CATEGORIES = listOf("경기당 득점", "경기당 도움", "경기당 공격 리바운드", "경기당 야투 시도", "경기당 야투 성공", "야투 성공률", "경기당 3점 시도", "경기당 3점 성공", "3점 성공률", "경기당 자유투 시도", "경기당 자유투 성공", "자유투 성공률")
        val PLAYER_STANDINGS_DEFEND_CATEGORIES = listOf("경기당 수비 리바운드", "경기당 블록", "경기당 스틸")
        val PLAYER_STANDINGS_COMMON_CATEGORIES = listOf("경기당 리바운드", "경기당 턴오버", "경기당 파울", "경기당 파울 유도", "경기당 피블록", "경기당 득실마진", "경기수", "경기당 출전시간", "출전 경기 승", "출전 경기 패", "출전 경기 승률", "트리플더블", "더블더블")
        val PLAYER_STANDINGS_SECOND_CATEGORIES = PLAYER_STANDINGS_ATTACK_CATEGORIES + PLAYER_STANDINGS_DEFEND_CATEGORIES + PLAYER_STANDINGS_COMMON_CATEGORIES

        val GAME_STATS_ATTACK_CATEGORIES = listOf("득점", "도움", "공격 리바운드", "야투 시도", "야투 성공", "야투 성공률", "3점 시도", "3점 성공", "3점 성공률", "자유투 시도", "자유투 성공", "자유투 성공률")
        val GAME_STATS_DEFEND_CATEGORIES = listOf("수비 리바운드", "블록", "스틸")
        val GAME_STATS_COMMON_CATEGORIES = listOf("리바운드", "턴오버", "파울", "득실마진", "출전시간")
        val GAME_STATS_SECOND_CATEGORIES = GAME_STATS_ATTACK_CATEGORIES + GAME_STATS_DEFEND_CATEGORIES + GAME_STATS_COMMON_CATEGORIES
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
        const val GAME_SCHEDULED = "Scheduled"
        const val GAME_LIVE = "In Progress"
        const val GAME_POSTPONED = "Postponed"
        const val GAME_RAIN = "Completed Early: Rain"
        const val GAME_FINAL = "Final"
        val GAME_FINISHED_LIST = listOf(GAME_RAIN, GAME_FINAL)

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

    fun viewPreparingAdviseText(type: String): String {
        return "${type} 화면은 더 나은 서비스 제공을 위해 현재 개선 작업 중입니다. 이용에 불편을 드려 죄송합니다."
    }

    fun tournamentButtonText(leagueId: Int): String {
        return when (leagueId) {
            Constants.Ids.MLS -> "플레이오프 대진표"
            Constants.Ids.NBA -> "플레이오프 대진표"
            Constants.Ids.MLB -> "포스트시즌 대진표"
            Constants.Ids.KBO -> "가을야구 대진표"
            else -> ""
        }
    }
}
















