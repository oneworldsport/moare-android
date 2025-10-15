package com.moare.android.features.search.models.responsemodels.football

import com.moare.android.features.search.models.Keyword
import com.moare.android.features.search.models.models.football.FBTeam
import com.moare.android.features.search.models.models.football.FBTeamForStandings
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class FBTeamStandingsResponseModel(
    val standings: FBTeamStandingsSource
)

// UEFA리그의 경우 team data의 statistics로 순위를 표현할수가 없어서, 백엔드에서 football api를 통해 받아온 데이터를 response해준다.
@Serializable(with = FBTeamStandingsSourceSerializer::class)
sealed class FBTeamStandingsSource {
    @Serializable
    data class Db(val teams: List<FBTeam>) : FBTeamStandingsSource()

    @Serializable
    data class External(val teams: List<FBTeamForStandings>) : FBTeamStandingsSource()
}

object FBTeamStandingsSourceSerializer : KSerializer<FBTeamStandingsSource> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FBTeamStandingsSource")

    override fun deserialize(decoder: Decoder): FBTeamStandingsSource {
        val jsonDecoder = decoder as JsonDecoder
        val element = jsonDecoder.decodeJsonElement()
        val json = jsonDecoder.json

        runCatching {
            val dbTeams = json.decodeFromJsonElement<List<FBTeam>>(element)
            return FBTeamStandingsSource.Db(dbTeams)
        }

        runCatching {
            val externalTeams = json.decodeFromJsonElement<List<FBTeamForStandings>>(element)
            return FBTeamStandingsSource.External(externalTeams)
        }

        throw SerializationException("standings JSON을 Db나 External로 해석할 수 없습니다: $element")
    }

    override fun serialize(encoder: Encoder, value: FBTeamStandingsSource) {
        TODO("Not yet implemented")
    }
}

val FBTeamStandingsSource.isEmpty: Boolean
    get() = when (this) {
        is FBTeamStandingsSource.Db -> teams.isEmpty()
        is FBTeamStandingsSource.External -> teams.isEmpty()
    }
