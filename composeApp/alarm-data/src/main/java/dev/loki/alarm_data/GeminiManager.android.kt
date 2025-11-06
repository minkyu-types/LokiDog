package dev.loki.alarm_data

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

actual class GeminiManager {

    private lateinit var model: GenerativeModel
    private var maxAlarmCount = 10

    companion object {
        const val VERSION_GEMINI = "gemini-2.5-flash"
    }

    @Serializable
    data class AlarmCreationResponse(
        val alarmGroup: AlarmGroupData,
        val alarms: List<AlarmData>
    )

    @Serializable
    data class AlarmGroupData(
        val title: String,
        val description: String,
        val repeatDays: List<String>, // ["MONDAY", "TUESDAY", ...]
        val isActivated: Boolean = true
    )

    @Serializable
    data class AlarmData(
        val time: String, // "HH:mm" 형식 (예: "07:30")
        val memo: String,
        val isActivated: Boolean = true
    )

    init {
        initializeGemini()
    }

    private fun initializeGemini() {
        model = Firebase.ai(backend = GenerativeBackend.googleAI())
            .generativeModel(
                modelName = VERSION_GEMINI,
                generationConfig = generationConfig {
                    responseMimeType = "application/json"
                    responseSchema = Schema.obj(
                        properties = mapOf(
                            "alarmGroup" to Schema.obj(
                                properties = mapOf(
                                    "title" to Schema.string(),
                                    "description" to Schema.string(),
                                    "repeatDays" to Schema.array(
                                        items = Schema.string()
                                    ),
                                    "isActivated" to Schema.boolean()
                                )
                            ),
                            "alarms" to Schema.array(
                                items = Schema.obj(
                                    properties = mapOf(
                                        "time" to Schema.string(),
                                        "isActivated" to Schema.boolean()
                                    )
                                )
                            )
                        )
                    )
                }
            )
    }

    suspend fun createAlarmGroupFromPrompt(userRequest: String): AlarmCreationResponse? = withContext(Dispatchers.IO) {
        if (!userRequest.contains("알람")) return@withContext null

        val prompt = """
            사용자 입력: "$userRequest"
            
            위 요청을 분석해서 알람 그룹 1개와 알람 N개를 생성해줘
            
            규칙:
            - alarmGroup은 1개만 생성
            - alarms는 최대 ${maxAlarmCount}개까지만 생성(생성되는 alarm 갯수가 더 많을 경우, 시간이 빠른 순으로 ${maxAlarmCount}개만)
            - time은 HH:mm 형식, 24시간제 적용 (예시: "06:30", "18:30")
            - repeatDays는 ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"] 중에서 최소 1개 이상 선택
            - isActivated는 boolean 타입(true or false)
            - 사용자 입력에 "간격"이라는 단어가 있을 시, 기준 시간으로부터 간격에 맞게 알람을 생성해줘(기준 시간이 없을 시 00:00을 기준으로)
            - 알람 목록에는 같은 시간을 가진 알람들이 존재할 수 있음
            
            예시:
            {
                "alarmGroup: {
                    "title": "기상 알람",
                    "description": "평일 아침 기상용 알람",
                    "repeatDays": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"],
                    "isActivated": true
                },
                "alarms": [
                    {"time": "06:00", "isActivated": true},
                    {"time": "06:05", "isActivated": true},
                    {"time": "06:10", "isActivated": true},
                    {"time": "06:15", "isActivated": true},
                    {"time": "06:20", "isActivated": true},
                ]
            }
        """.trimIndent()
        val response = model.generateContent(prompt)
        return@withContext Json.decodeFromString<AlarmCreationResponse>(response.text ?: "{}")
    }
}