package com.moare.android.features.search.models

//import com.google.gson.JsonElement
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

//class DataModelDeserializer : JsonDeserializer<DataModel> {
//    override fun deserialize(
//        json: JsonElement,
//        typeOfT: Type,
//        context: JsonDeserializationContext
//    ): DataModel {
//        val jsonObject = json.asJsonObject
//        val dataType = jsonObject.get("dataType").asString
//
//        val data: SportDecodableModel = when (dataType) {
//            "fb_player_info" -> {
//                val responseModel = context.deserialize<FBPlayerInfoResponseModel>(
//                    jsonObject["data"],
//                    FBPlayerInfoResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbPlayerInfoConverter(responseModel)
//                SportDecodableModel.FBPlayerInfo(responseModel, displayModel)
//            }
//            "fb_player_standings" -> {
//                val responseModel = context.deserialize<FBPlayerStandingsResponseModel>(
//                    jsonObject["data"],
//                    FBPlayerStandingsResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbPlayerStandingsConverter(responseModel)
//                SportDecodableModel.FBPlayerStandings(responseModel, displayModel)
//            }
//            "fb_team_info" -> {
//                val responseModel = context.deserialize<FBTeamInfoResponseModel>(
//                    jsonObject["data"],
//                    FBTeamInfoResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbTeamInfoConverter(responseModel)
//                SportDecodableModel.FBTeamInfo(responseModel, displayModel)
//            }
//            "fb_team_standings" -> {
//                val responseModel = context.deserialize<FBTeamStandingsResponseModel>(
//                    jsonObject["data"],
//                    FBTeamStandingsResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbTeamStandingsConverter(responseModel)
//                SportDecodableModel.FBTeamStandings(responseModel, displayModel)
//            }
//            "fb_game_stats" -> {
//                val responseModel = context.deserialize<FBGameStatsResponseModel>(
//                    jsonObject["data"],
//                    FBGameStatsResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbGameStatsConverter(responseModel)
//                SportDecodableModel.FBGameStats(responseModel, displayModel)
//            }
//            "fb_game_schedule" -> {
//                val responseModel = context.deserialize<FBGamesScheduleResponseModel>(
//                    jsonObject["data"],
//                    FBGamesScheduleResponseModel::class.java
//                )
//                val displayModel = ModelConverter().fbGameScheduleConverter(responseModel)
//                SportDecodableModel.FBGamesSchedule(responseModel, displayModel)
//            }
//            else -> SportDecodableModel.Unknown
//        }
//
//        return DataModel(dataType, data)
//    }
//}

//object DataModelDeserializer : JsonContentPolymorphicSerializer<DataModel>(DataModel::class) {
//    private val modelConverter = ModelConverter()
//
//    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out DataModel> {
//        val dataType = element.jsonObject["dataType"]?.jsonPrimitive?.content
//            ?: throw SerializationException("Missing 'dataType' field in JSON")
//
//        return when (dataType) {
//            "fb_player_info" -> FBPlayerInfoDeserializer
//            "fb_player_standings" -> FBPlayerStandingsDeserializer
//            "fb_team_info" -> FBTeamInfoDeserializer
//            "fb_team_standings" -> FBTeamStandingsDeserializer
//            "fb_game_stats" -> FBGameStatsDeserializer
//            "fb_game_schedule" -> FBGameScheduleDeserializer
//            else -> throw SerializationException("Unknown dataType: $dataType")
//        }
//    }
//
//    private val FBPlayerInfoDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBPlayerInfo") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBPlayerInfo>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBPlayerInfoResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbPlayerInfoConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_player_info",
//                data = SportDecodableModel.FBPlayerInfo(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }
//
//    private val FBPlayerStandingsDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBPlayerStandings") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBPlayerStandings>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBPlayerStandingsResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbPlayerStandingsConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_player_standings",
//                data = SportDecodableModel.FBPlayerStandings(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }
//
//    private val FBTeamInfoDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBTeamInfo") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBTeamInfo>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBTeamInfoResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbTeamInfoConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_team_info",
//                data = SportDecodableModel.FBTeamInfo(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }
//
//    private val FBTeamStandingsDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBTeamStandings") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBTeamStandings>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBTeamStandingsResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbTeamStandingsConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_team_standings",
//                data = SportDecodableModel.FBTeamStandings(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }
//
//    private val FBGameStatsDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBGameStats") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBGameStats>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBGameStatsResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbGameStatsConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_game_stats",
//                data = SportDecodableModel.FBGameStats(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }
//
//    private val FBGameScheduleDeserializer = object : KSerializer<DataModel> {
//        override val descriptor = buildClassSerialDescriptor("FBGameSchedule") {
//            element<String>("dataType")
//            element<SportDecodableModel.FBGameSchedule>("data")
//        }
//
//        override fun deserialize(decoder: Decoder): DataModel {
//            val input = decoder as? JsonDecoder
//                ?: throw SerializationException("This class can only be deserialized using JSON")
//            val jsonObject = input.decodeJsonElement().jsonObject
//
//            val responseModel = Json.decodeFromJsonElement<FBGameScheduleResponseModel>(jsonObject["data"]!!)
//            val displayModel = modelConverter.fbGameScheduleConverter(responseModel)
//
//            return DataModel(
//                dataType = "fb_games_schedule",
//                data = SportDecodableModel.FBGameSchedule(responseModel, displayModel)
//            )
//        }
//
//        override fun serialize(encoder: Encoder, value: DataModel) {
//            throw SerializationException("Serialization is not supported")
//        }
//    }

//    private val FBGamesScheduleDeserializer = object : JsonTransformingSerializer<DataModel>(
//        DataModel.serializer()
//    ) {
//        override fun transformDeserialize(element: JsonElement): JsonElement {
//            val responseModel = Json.decodeFromJsonElement<FBGamesScheduleResponseModel>(element.jsonObject["data"]!!)
//            val displayModel = modelConverter.fbGameScheduleConverter(responseModel)
//            return JsonObject(
//                mapOf(
//                    "dataType" to Json.encodeToJsonElement("fb_game_schedule"),
//                    "responseModel" to Json.encodeToJsonElement(responseModel),
//                    "displayModel" to Json.encodeToJsonElement(displayModel)
//                )
//            )
//        }
//    }
//}

//object StatValueSerializer : JsonContentPolymorphicSerializer<StatValue>(StatValue::class) {
//    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out StatValue> {
//        return when {
//            element.jsonPrimitive.intOrNull != null -> StatValue.IntValue.serializer()
//            element.jsonPrimitive.doubleOrNull != null -> StatValue.DoubleValue.serializer()
//            element.jsonPrimitive.isString -> StatValue.StringValue.serializer()
//            element.jsonPrimitive.booleanOrNull != null -> StatValue.BoolValue.serializer()
//            else -> throw SerializationException("Unknown value type: $element")
//        }
//    }
//}

//object StatValueSerializer : JsonContentPolymorphicSerializer<StatValue>(StatValue::class) {
//    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out StatValue> {
//        val primitive = element as? JsonPrimitive
//            ?: throw SerializationException("StatValue must be a JsonPrimitive but was $element")
//
//        return when {
//            primitive.isString -> StatValue.StringValue.serializer()
//            primitive.intOrNull != null -> StatValue.IntValue.serializer()
//            else -> throw SerializationException("Unknown type for StatValue: $element")
//        }
//    }
//}

// 확장 함수
val JsonPrimitive.isString: Boolean
    get() = this.contentOrNull != null

val JsonPrimitive.intOrNull: Int?
    get() = this.content.toIntOrNull()

//object DataModelDeserializer : JsonTransformingSerializer<DataModel>(DataModel.serializer()) {
//    private val modelConverter = ModelConverter()
//
//    override fun transformDeserialize(element: JsonElement): JsonElement {
//        val jsonObject = element.jsonObject
//
//        val dataType = jsonObject["dataType"]?.jsonPrimitive?.content
//            ?: throw SerializationException("Missing 'dataType' field in JSON")
//
//        val data = jsonObject["data"]
//            ?: throw SerializationException("Missing 'data' field in JSON")
//
//        return when (dataType) {
//            "fb_player_info" -> {
//                val responseModel = Json.decodeFromJsonElement<FBPlayerInfoResponseModel>(data)
//                val displayModel = modelConverter.fbPlayerInfoConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBPlayerInfo(responseModel, displayModel))
//                    )
//                )
//            }
//            "fb_player_standings" -> {
//                val responseModel = Json.decodeFromJsonElement<FBPlayerStandingsResponseModel>(data)
//                val displayModel = modelConverter.fbPlayerStandingsConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBPlayerStandings(responseModel, displayModel))
//                    )
//                )
//            }
//            "fb_team_info" -> {
//                val responseModel = Json.decodeFromJsonElement<FBTeamInfoResponseModel>(data)
//                val displayModel = modelConverter.fbTeamInfoConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBTeamInfo(responseModel, displayModel))
//                    )
//                )
//            }
//            "fb_team_standings" -> {
//                val responseModel = Json.decodeFromJsonElement<FBTeamStandingsResponseModel>(data)
//                val displayModel = modelConverter.fbTeamStandingsConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBTeamStandings(responseModel, displayModel))
//                    )
//                )
//            }
//            "fb_game_stats" -> {
//                val responseModel = Json.decodeFromJsonElement<FBGameStatsResponseModel>(data)
//                val displayModel = modelConverter.fbGameStatsConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBGameStats(responseModel, displayModel))
//                    )
//                )
//            }
//            "fb_game_schedule" -> {
//                val responseModel = Json.decodeFromJsonElement<FBGamesScheduleResponseModel>(data)
//                val displayModel = modelConverter.fbGameScheduleConverter(responseModel)
//                JsonObject(
//                    mapOf(
//                        "dataType" to JsonPrimitive(dataType),
//                        "data" to Json.encodeToJsonElement(SportDecodableModel.FBGamesSchedule(responseModel, displayModel))
//                    )
//                )
//            }
//            else -> throw SerializationException("Unknown dataType: $dataType")
//        }
//    }
//}

//object SportDecodableModelDeserializer : KSerializer<SportDecodableModel> {
//    private val modelConverter = ModelConverter()
//
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("SportDecodableModel")
//
//    override fun deserialize(decoder: Decoder): SportDecodableModel {
//        val input = decoder as? JsonDecoder
//            ?: throw SerializationException("This class can only be deserialized using JSON")
//
//        // JSON 전체를 읽어옴
//        val jsonObject = input.decodeJsonElement().jsonObject
//
//        // `dataType`과 `data`를 가져옴
//        val dataType = jsonObject["dataType"]?.jsonPrimitive?.content
//            ?: throw SerializationException("Missing 'dataType' field in JSON")
//        val data = jsonObject["data"] ?: throw SerializationException("Missing 'data' field in JSON")
//
//        // dataType에 따라 적절한 서브클래스 생성
//        return when (dataType) {
//            "fb_player_info" -> {
//                val responseModel = Json.decodeFromJsonElement<FBPlayerInfoResponseModel>(data)
//                val displayModel = modelConverter.fbPlayerInfoConverter(responseModel)
//                SportDecodableModel.FBPlayerInfo(responseModel, displayModel)
//            }
//            "fb_player_standings" -> {
//                val responseModel = Json.decodeFromJsonElement<FBPlayerStandingsResponseModel>(data)
//                val displayModel = modelConverter.fbPlayerStandingsConverter(responseModel)
//                SportDecodableModel.FBPlayerStandings(responseModel, displayModel)
//            }
//            "fb_team_info" -> {
//                val responseModel = Json.decodeFromJsonElement<FBTeamInfoResponseModel>(data)
//                val displayModel = modelConverter.fbTeamInfoConverter(responseModel)
//                SportDecodableModel.FBTeamInfo(responseModel, displayModel)
//            }
//            "fb_team_standings" -> {
//                val responseModel = Json.decodeFromJsonElement<FBTeamStandingsResponseModel>(data)
//                val displayModel = modelConverter.fbTeamStandingsConverter(responseModel)
//                SportDecodableModel.FBTeamStandings(responseModel, displayModel)
//            }
//            "fb_game_stats" -> {
//                val responseModel = Json.decodeFromJsonElement<FBGameStatsResponseModel>(data)
//                val displayModel = modelConverter.fbGameStatsConverter(responseModel)
//                SportDecodableModel.FBGameStats(responseModel, displayModel)
//            }
//            "fb_game_schedule" -> {
//                val responseModel = Json.decodeFromJsonElement<FBGamesScheduleResponseModel>(data)
//                val displayModel = modelConverter.fbGameScheduleConverter(responseModel)
//                SportDecodableModel.FBGamesSchedule(responseModel, displayModel)
//            }
//            else -> SportDecodableModel.Unknown
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: SportDecodableModel) {
//        throw SerializationException("Serialization is not supported for this deserializer")
//    }
//}