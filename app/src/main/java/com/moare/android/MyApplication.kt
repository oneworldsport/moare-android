package com.moare.android

import android.app.Application
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.moare.android.core.constants.Constants
import com.moare.android.core.util.AWSUtils
import com.moare.android.core.util.EnNameTranslationUtils
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            // NOTE: test code를 실행할때는 s3가 모두 초기화(비동기 작업) 되기 전에 화면이 나와 사전이 비어있는 경우가 있음. 그래서 s3 초기화 작업이 모두 끝나면 다음 코드 진행.
            runBlocking {
                supervisorScope {
                    launch {
                        runCatching {
                            AWSUtils.checkTrendingKeywords(
                                context = this@MyApplication,
                                s3Key = "trending_keywords/trending_keywords.json",
                                eTagKey = stringPreferencesKey("trendingKeywordsETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkAutoCompleteJson(this@MyApplication)
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNotice(
                                context = this@MyApplication,
                                s3Key = "notice/main_notice.json",
                                eTagKey = stringPreferencesKey("mainNoticeETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    // dictionary
                    launch {
                        runCatching {
                            AWSUtils.checkTournamentTeams(
                                context = this@MyApplication,
                                category = Constants.Keys.TOURNAMENT_TEAMS,
                                s3Key = "tournament/tournament_teams.json",
                                eTagKey = stringPreferencesKey("tournamentTeamsETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.FOOTBALL_TEAM_DIC,
                                s3Key = "name_dictionary/football_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("footballTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.EPL_PLAYER_DIC,
                                s3Key = "name_dictionary/epl_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("eplPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.NBA_PLAYER_DIC,
                                s3Key = "name_dictionary/nba_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.NBA_TEAM_DIC,
                                s3Key = "name_dictionary/nba_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.KBO_TEAM_DIC,
                                s3Key = "name_dictionary/kbo_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("kboTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }

                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLB_PLAYER_DIC,
                                s3Key = "name_dictionary/mlb_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlbPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLB_TEAM_DIC,
                                s3Key = "name_dictionary/mlb_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlbTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.LALIGA_PLAYER_DIC,
                                s3Key = "name_dictionary/laliga_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("laligaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.BUNDESLIGA_PLAYER_DIC,
                                s3Key = "name_dictionary/bundesliga_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("bundesligaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.LIGUE1_PLAYER_DIC,
                                s3Key = "name_dictionary/ligue1_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("ligue1PlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.SERIEA_PLAYER_DIC,
                                s3Key = "name_dictionary/seriea_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("serieaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLS_PLAYER_DIC,
                                s3Key = "name_dictionary/mls_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlsPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }
                }
            }
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                // process
                // 1. Trending Keywords
                // 2. AutoComplete
                // 3. Notice
                // 4. Dictionary
                supervisorScope {
                    launch {
                        runCatching {
                            AWSUtils.checkTrendingKeywords(
                                context = this@MyApplication,
                                s3Key = "trending_keywords/trending_keywords.json",
                                eTagKey = stringPreferencesKey("trendingKeywordsETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkAutoCompleteJson(this@MyApplication)
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNotice(
                                context = this@MyApplication,
                                s3Key = "notice/main_notice.json",
                                eTagKey = stringPreferencesKey("mainNoticeETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    // dictionary
                    launch {
                        runCatching {
                            AWSUtils.checkTournamentTeams(
                                context = this@MyApplication,
                                category = Constants.Keys.TOURNAMENT_TEAMS,
                                s3Key = "tournament/tournament_teams.json",
                                eTagKey = stringPreferencesKey("tournamentTeamsETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.FOOTBALL_TEAM_DIC,
                                s3Key = "name_dictionary/football_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("footballTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.EPL_PLAYER_DIC,
                                s3Key = "name_dictionary/epl_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("eplPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.NBA_PLAYER_DIC,
                                s3Key = "name_dictionary/nba_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("nbaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.NBA_TEAM_DIC,
                                s3Key = "name_dictionary/nba_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("nbaTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.KBO_TEAM_DIC,
                                s3Key = "name_dictionary/kbo_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("kboTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }

                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLB_PLAYER_DIC,
                                s3Key = "name_dictionary/mlb_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlbPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLB_TEAM_DIC,
                                s3Key = "name_dictionary/mlb_team_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlbTeamNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.LALIGA_PLAYER_DIC,
                                s3Key = "name_dictionary/laliga_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("laligaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.BUNDESLIGA_PLAYER_DIC,
                                s3Key = "name_dictionary/bundesliga_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("bundesligaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.LIGUE1_PLAYER_DIC,
                                s3Key = "name_dictionary/ligue1_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("ligue1PlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.SERIEA_PLAYER_DIC,
                                s3Key = "name_dictionary/seriea_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("serieaPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }

                    launch {
                        runCatching {
                            AWSUtils.checkNameDictionary(
                                context = this@MyApplication,
                                category = Constants.Keys.MLS_PLAYER_DIC,
                                s3Key = "name_dictionary/mls_player_name_dictionary.json",
                                eTagKey = stringPreferencesKey("mlsPlayerNameDictionaryETag")
                            )
                        }.onFailure { Log.d("error", it.localizedMessage ?: "aws s3 error") }
                    }
                }
            }
        }

        // STUDY: Hilt를 통해 AppModule에 싱글톤으로 선언된 AmazonTranslateClient를 object EnNameTranslationUtils에서 사용하기 위해 EnNameTranslationUtils에 초기화하는 과정
        EnNameTranslationUtils.init(this)
    }
}