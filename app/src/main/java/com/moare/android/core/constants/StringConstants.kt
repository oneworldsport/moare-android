package com.moare.android.core.constants

object StringConstants {
    // TODO: 대문자로 바꾸기
    const val resultOpen = "결과 보기"
    const val resultHide = "결과 숨기기"
    const val gameNotStartedStr = "경기 전"
    const val gameFinishedStr = "경기 종료"

    const val standingsFirstCategory = "순위"
    val statsFirstCategories = listOf("공격 지표", "수비 지표", "공통 지표")

    const val gameStatsFirstCategory = "선수 이름"

    object Football {
        const val gameFirstHalfStr = "전반전"
        const val gameHalftimeStr = "전반 종료"
        const val gameSecondHalfStr = "후반전"

        const val gameNotStarted = "NS"
        const val gameFirstHalf = "1H"
        const val gameHalftime = "HT"
        const val gameSecondHalf = "2H"
        const val gameExtraTime = "ET" // 연장전
        const val gameBreakTime = "BT" // 연장전 전반 후 휴식시간
        const val gamePenaltyShootout = "P" // 승부차기
        const val gameFinished = "FT"
        const val gameFinishedAfterExtraTime = "AET" // 승부차기 없이 연장전 후 경기 종료
        const val gameFinishedAfterPenaltyShootout = "PET" // 승부차기 후 경기 종료
        const val gamePostponed = "PST"
        const val gameCancelled = "CANC"
        val gameLiveList = listOf(gameFirstHalf, gameHalftime, gameSecondHalf, gameExtraTime, gameBreakTime, gamePenaltyShootout)
        val gameFinishedList = listOf(gameFinished, gameFinishedAfterExtraTime, gameFinishedAfterPenaltyShootout)

        val teamStandingsCategories = listOf("승점", "승", "무", "패", "경기수", "득점", "실점", "득실차", "홈성적", "원정성적")

        val playerStandingsAttackCategories = listOf("득점", "도움", "공격포인트", "슈팅", "유효슈팅", "키패스", "드리블 성공", "pk골")
        val playerStandingsDefendCategories = listOf("태클 시도", "볼 경합 성공")
        val playerStandingsCommonCategories = listOf("패스 시도", "파울", "경고", "퇴장", "경기수", "선발출전", "교체출전", "출전시간(분)", "평균평점")
        val playerStandingsSecondCategories = playerStandingsAttackCategories + playerStandingsDefendCategories + playerStandingsCommonCategories

        // 보류: 세이브, 실점, 패널티 실패, 패널티 세이브
        val gameStatsAttackCategories = listOf("득점", "pk골", "도움", "슈팅", "유효슈팅", "키패스", "드리블 성공/시도(%)", "오프사이드")
        val gameStatsDefendCategories = listOf("태클 시도", "볼 경합 성공/시도(%)", "가로채기")
        val gameStatsCommonCategories = listOf("패스 시도", "얻은 파울", "파울", "경고", "퇴장", "출전시간(분)", "평점")
        val gameStatsSecondCategories = gameStatsAttackCategories + gameStatsDefendCategories + gameStatsCommonCategories
    }

    object NBA {
        const val gameQtr1 = "1쿼터"
        const val gameQtr2 = "2쿼터"
        const val gameQtr3 = "3쿼터"
        const val gameQtr4 = "4쿼터"
        const val gameOt1 = "연장 1쿼터"
        const val gameOt2 = "연장 2쿼터"
        const val gameOt3 = "연장 3쿼터"

        val conferenceCategory = listOf("서부", "동부")
        // TODO: 나중에 데이터 추가되면 카테고리 추가
//        val teamStandingsCategories = listOf("게임차", "승률", "승", "패", "경기수", "홈성적", "원정성적", "경기당 득점", "경기당 득실마진", "경기당 도움", "경기당 리바운드", "야투 성공률", "3점 성공률", "자유투 성공률", "경기당 블록", "경기당 스틸", "경기당 턴오버", "경기당 파울")
        val teamStandingsCategories = listOf("게임차", "승률", "승", "패", "경기수", "경기당 득점", "경기당 득실마진", "경기당 도움", "경기당 리바운드", "야투 성공률", "3점 성공률", "자유투 성공률", "경기당 블록", "경기당 스틸", "경기당 턴오버", "경기당 파울")

        val playerStandingsAttackCategories = listOf("경기당 득점", "경기당 도움", "경기당 공격리바운드", "경기당 야투 시도", "경기당 야투 성공", "야투 성공률", "경기당 3점 시도", "경기당 3점 성공", "3점 성공률", "경기당 자유투 시도", "경기당 자유투 성공", "자유투 성공률")
        val playerStandingsDefendCategories = listOf("경기당 수비리바운드", "경기당 블록", "경기당 스틸")
        val playerStandingsCommonCategories = listOf("경기당 리바운드", "경기당 턴오버", "경기당 파울", "경기당 파울 유도", "경기당 피블록", "경기당 득실마진", "경기수", "경기당 출전시간", "출전 경기 승", "출전 경기 패", "출전 경기 승률", "트리플더블", "더블더블")
        val playerStandingsSecondCategories = playerStandingsAttackCategories + playerStandingsDefendCategories + playerStandingsCommonCategories
    }
}