package com.moare.android.core.constants

object StringConstants {
    object Football {
        const val resultOpen = "결과 보기"
        const val resultHide = "결과 숨기기"
        const val gameNotStarted = "경기 전"
        const val gameFirstHalf = "전반전"
        const val gameHalftime = "전반 종료"
        const val gameSecondHalf = "후반전"
        const val gameFinished = "경기 종료"

        const val standingsFirstCategory = "순위"
        val statsFirstCategories = listOf("공격 지표", "수비 지표", "공통 지표")
        val playerStandingsSecondCategories = listOf("득점", "도움", "공격포인트", "슈팅", "유효슈팅", "키패스", "드리블 성공", "pk골", "태클 시도", "볼 경합 성공", "패스 시도", "파울", "경고", "퇴장", "경기수", "선발출전", "교체출전", "출전시간(분)", "평균평점")
        val playerStandingsAttackCategories = listOf("득점", "도움", "공격포인트", "슈팅", "유효슈팅", "키패스", "드리블 성공", "pk골")
        val playerStandingsDefendCategories = listOf("태클 시도", "볼 경합 성공")
        val playerStandingsEtcCategories = listOf("패스 시도", "파울", "경고", "퇴장", "경기수", "선발출전", "교체출전", "출전시간(분)", "평균평점")

        const val gameStatsFirstCategory = "선수 이름"
        // 보류: 세이브, 실점, 패널티 실패, 패널티 세이브
        val gameStatsSecondCategories = listOf("득점", "pk골", "도움", "슈팅", "유효슈팅", "키패스", "드리블 성공/시도(%)", "오프사이드", "태클 시도", "볼 경합 성공/시도(%)", "가로채기", "패스 시도", "얻은 파울", "파울", "경고", "퇴장", "출전시간(분)", "평점")
        val gameStatsAttackCategories = listOf("득점", "pk골", "도움", "슈팅", "유효슈팅", "키패스", "드리블 성공/시도(%)", "오프사이드")
        val gameStatsDefendCategories = listOf("태클 시도", "볼 경합 성공/시도(%)", "가로채기")
        val gameStatsEtcCategories = listOf("패스 시도", "얻은 파울", "파울", "경고", "퇴장", "출전시간(분)", "평점")
    }
}