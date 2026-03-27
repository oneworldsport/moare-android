package com.moare.android

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.regions.Regions
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.AWSUtils
import com.moare.android.core.util.EnNameTranslationUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // NOTE: test code를 실행할때는 s3가 모두 초기화(비동기 작업) 되기 전에 화면이 나와 사전이 비어있는 경우가 있음. 그래서 s3 초기화 작업이 모두 끝나면 다음 코드 진행.
            // TODO: 추후 runCatching으로 개선
            runBlocking {
                // cognito identityId 확인
//                val provider = CognitoCachingCredentialsProvider(
//                    applicationContext.applicationContext,
//                    "ap-northeast-2:efa201e1-412b-438a-927f-411cc4838469",
//                    Regions.AP_NORTHEAST_2
//                )
//
//                val identityId = withContext(Dispatchers.IO) { provider.identityId }
//                Log.d("AWS", "IdentityId=$identityId")

                launch {
                    AWSUtils.checkTrendingKeywords(
                        context = this@MyApplication,
                        s3Key = "trending_keywords/trending_keywords.json",
                        eTagKey = stringPreferencesKey("trendingKeywordsETag")
                    )
                }

                launch {
                    AWSUtils.checkAutoCompleteJson(this@MyApplication)
                }

                launch {
                    AWSUtils.checkNotice(
                        context = this@MyApplication,
                        s3Key = "notice/main_notice.json",
                        eTagKey = stringPreferencesKey("mainNoticeETag")
                    )
                }

                // dictionary
                launch {
                    AWSUtils.checkTournamentTeams(
                        context = this@MyApplication,
                        s3Key = "tournament/tournament_teams.json",
                        eTagKey = stringPreferencesKey("tournamentTeamsETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.FOOTBALL_TEAM_DIC,
                        s3Key = "name_dictionary/football_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("footballTeamNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.FOOTBALL_PLAYER_DIC,
                        s3Key = "name_dictionary/football_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("footballPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.NBA_PLAYER_DIC,
                        s3Key = "name_dictionary/nba_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.NBA_TEAM_DIC,
                        s3Key = "name_dictionary/nba_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.KBO_TEAM_DIC,
                        s3Key = "name_dictionary/kbo_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("kboTeamNameDictionaryETag")
                    )

                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.MLB_PLAYER_DIC,
                        s3Key = "name_dictionary/mlb_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("mlbPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.MLB_TEAM_DIC,
                        s3Key = "name_dictionary/mlb_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("mlbTeamNameDictionaryETag")
                    )
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                // process
                // 1. Trending Keywords
                // 2. AutoComplete
                // 3. Notice
                // 4. Dictionary
                launch {
                    AWSUtils.checkTrendingKeywords(
                        context = this@MyApplication,
                        s3Key = "trending_keywords/trending_keywords.json",
                        eTagKey = stringPreferencesKey("trendingKeywordsETag")
                    )
                }

                launch {
                    AWSUtils.checkAutoCompleteJson(this@MyApplication)
                }

                launch {
                    AWSUtils.checkNotice(
                        context = this@MyApplication,
                        s3Key = "notice/main_notice.json",
                        eTagKey = stringPreferencesKey("mainNoticeETag")
                    )
                }

                // dictionary
                launch {
                    AWSUtils.checkTournamentTeams(
                        context = this@MyApplication,
                        s3Key = "tournament/tournament_teams.json",
                        eTagKey = stringPreferencesKey("tournamentTeamsETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.FOOTBALL_TEAM_DIC,
                        s3Key = "name_dictionary/football_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("footballTeamNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.FOOTBALL_PLAYER_DIC,
                        s3Key = "name_dictionary/football_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("footballPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.NBA_PLAYER_DIC,
                        s3Key = "name_dictionary/nba_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.NBA_TEAM_DIC,
                        s3Key = "name_dictionary/nba_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.KBO_TEAM_DIC,
                        s3Key = "name_dictionary/kbo_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("kboTeamNameDictionaryETag")
                    )

                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.MLB_PLAYER_DIC,
                        s3Key = "name_dictionary/mlb_player_name_dictionary.json",
                        eTagKey = stringPreferencesKey("mlbPlayerNameDictionaryETag")
                    )
                }

                launch {
                    AWSUtils.checkNameDictionary(
                        context = this@MyApplication,
                        category = Constants.Keys.MLB_TEAM_DIC,
                        s3Key = "name_dictionary/mlb_team_name_dictionary.json",
                        eTagKey = stringPreferencesKey("mlbTeamNameDictionaryETag")
                    )
                }
            }
        }

        // STUDY: Hilt를 통해 AppModule에 싱글톤으로 선언된 AmazonTranslateClient를 object EnNameTranslationUtils에서 사용하기 위해 EnNameTranslationUtils에 초기화하는 과정
        EnNameTranslationUtils.init(this)
    }
}