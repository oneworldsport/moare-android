package com.moare.android

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.stringPreferencesKey
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.auth.CognitoCredentialsProvider
import com.amazonaws.regions.Regions
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

            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = "nba_player",
                s3Key = "name_dictionary/nba_player_name_dictionary.json",
                eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
            )

            AWSUtils.checkNameDictionary(
                context = this@MyApplication,
                category = "nba_team",
                s3Key = "name_dictionary/nba_team_name_dictionary.json",
                eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
            )
        }

        // STUDY: Hilt를 통해 AppModule에 싱글톤으로 선언된 AmazonTranslateClient를 object EnNameTranslationUtils에서 사용하기 위해 EnNameTranslationUtils에 초기화하는 과정
        EnNameTranslationUtils.init(this)
    }
}