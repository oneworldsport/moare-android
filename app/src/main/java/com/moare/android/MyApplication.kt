package com.moare.android

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.regions.Regions
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.AWSUtils
import com.moare.android.core.util.EnNameTranslationUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            // process
            // 1. Trending Keywords
            // 2. AutoComplete
            // 3. Notice
            // 4. Dictionary
            AWSUtils.checkTrendingKeywords(
                context = this@MyApplication,
                s3Key = "trending_keywords/trending_keywords.json",
                eTagKey = stringPreferencesKey("trendingKeywordsETag")
            )

            AWSUtils.checkAutoCompleteJson(this@MyApplication)

            AWSUtils.checkNotice(
                context = this@MyApplication,
                s3Key = "notice/main_notice.json",
                eTagKey = stringPreferencesKey("mainNoticeETag")
            )

            // dictionary
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.EPL_PLAYER_DIC,
                s3Key = "name_dictionary/epl_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("eplPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.EPL_TEAM_DIC,
                s3Key = "name_dictionary/epl_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("eplTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.NBA_PLAYER_DIC,
                s3Key = "name_dictionary/nba_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.NBA_TEAM_DIC,
                s3Key = "name_dictionary/nba_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.KBO_TEAM_DIC,
                s3Key = "name_dictionary/kbo_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("kboTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.MLB_PLAYER_DIC,
                s3Key = "name_dictionary/mlb_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("mlbPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.MLB_TEAM_DIC,
                s3Key = "name_dictionary/mlb_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("mlbTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.LALIGA_PLAYER_DIC,
                s3Key = "name_dictionary/laliga_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("laligaPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.LALIGA_TEAM_DIC,
                s3Key = "name_dictionary/laliga_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("laligaTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.BUNDESLIGA_PLAYER_DIC,
                s3Key = "name_dictionary/bundesliga_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("bundesligaPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.BUNDESLIGA_TEAM_DIC,
                s3Key = "name_dictionary/bundesliga_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("bundesligaTeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.LIGUE1_PLAYER_DIC,
                s3Key = "name_dictionary/ligue1_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("ligue1PlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.LIGUE1_TEAM_DIC,
                s3Key = "name_dictionary/ligue1_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("ligue1TeamNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.SERIEA_PLAYER_DIC,
                s3Key = "name_dictionary/seriea_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("serieaPlayerNameDictionaryETag")
            )
            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = Constants.Keys.SERIEA_TEAM_DIC,
                s3Key = "name_dictionary/seriea_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("serieaTeamNameDictionaryETag")
            )
        }

        // STUDY: Hilt를 통해 AppModule에 싱글톤으로 선언된 AmazonTranslateClient를 object EnNameTranslationUtils에서 사용하기 위해 EnNameTranslationUtils에 초기화하는 과정
        EnNameTranslationUtils.init(this)
    }
}