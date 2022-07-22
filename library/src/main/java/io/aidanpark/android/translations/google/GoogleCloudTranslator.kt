package io.aidanpark.android.translations.google

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.translate.Translate
import com.google.api.services.translate.TranslateRequest
import com.google.api.services.translate.TranslateRequestInitializer
import io.aidanpark.android.translations.TransRequest
import io.aidanpark.android.translations.TransResponse
import io.aidanpark.android.translations.Translator
import java.io.IOException


/**
 * Translation function using Google cloud translation service.
 *
 * Parameter 'google_cloud_translation_api_key' used as API KEY, must be not exposed.
 *
 * @version 1.0
 */
class GoogleCloudTranslator(
    private val applicationName: String,
    private val packageName: String,
    private val signature: String,
    private val google_cloud_translation_api_key: String
) : Translator {

    companion object {
        private val supportedLanguageCodes = arrayOf(
            "af", // 아프리칸스어
            "sq", // 알바니아어
            "am", // 암하라어
            "ar", // 아랍어
            "hy", // 아르메니아어
            "az", // 아제르바이잔어
            "eu", // 바스크어
            "be", // 벨라루스어
            "bn", // 벵골어
            "bs", // 보스니아어
            "bg", // 불가리아어
            "ca", // 카탈루냐어
            "ceb", // 세부아노어 ISO-639-2
            "zh-CN", // 중국어(간체) BCP-47
            "zh-TW", // 중국어(번체) BCP-47
            "co", // 코르시카어
            "hr", // 크로아티아어
            "cs", // 체코어
            "da", // 덴마크어
            "nl", // 네덜란드어
            "en", // 영어
            "eo", // 에스페란토
            "et", // 에스토니아어
            "fi", // 핀란드어
            "fr", // 프랑스어
            "fy", // 프리지아어
            "gl", // 갈리시아어
            "ka", // 조지아어
            "de", // 독일어
            "el", // 그리스어
            "gu", // 구자라트어
            "ht", // 아이티 크리올어
            "ha", // 하우사어
            "haw", // 하와이어 ISO-639-2
            "he", // 히브리어 iw
            "hi", // 힌디어
            "hmn", // 몽어 ISO-639-
            "hu", // 헝가리어
            "is", // 아이슬란드어
            "ig", // 이그보어
            "id", // 인도네시아어
            "ga", // 아일랜드
            "it", // 이탈리아어
            "ja", // 일본어
            "jv", // 자바어
            "kn", // 칸나다어
            "kk", // 카자흐어
            "km", // 크메르어
            "rw", // 키냐르완다어
            "ko", // 한국어
            "ku", // 쿠르드어
            "ky", // 키르기스어
            "lo", // 라오어
            "la", // 라틴어
            "lv", // 라트비아어
            "lt", // 리투아니아어
            "lb", // 룩셈부르크어
            "mk", // 마케도니아어
            "mg", // 마다가스카르어
            "ms", // 말레이어
            "ml", // 말라얄람어
            "mt", // 몰타어
            "mi", // 마오리어
            "mr", // 마라타어
            "mn", // 몽골어
            "my", // 미얀마어(버마어)
            "ne", // 네팔어
            "no", // 노르웨이어
            "ny", // 니안자어(치츄어)
            "or", // 오리야어
            "ps", // 파슈토어
            "fa", // 페르시아어
            "pl", // 폴란드어
            "pt", // 포르투갈어(포르투갈, 브라질)
            "pa", // 펀자브어
            "ro", // 루마니아어
            "ru", // 러시아어
            "sm", // 사모아어
            "gd", // 스코틀랜드 게일어
            "sr", // 세르비아어
            "st", // 세소토어
            "sn", // 쇼나어
            "sd", // 신디어
            "si", // 스리랑카어(싱할라어)
            "sk", // 슬로바키아어
            "sl", // 슬로베니아어
            "so", // 소말리어
            "es", // 스페인어
            "su", // 순다어
            "sw", // 스와힐리어
            "sv", // 스웨덴어
            "tl", // 타갈로그어(필리핀어)
            "tg", // 타지크어
            "ta", // 타밀어
            "tt", // 타타르어
            "te", // 텔루구어
            "th", // 태국어
            "tr", // 터키어
            "tk", // 투르크멘어
            "uk", // 우크라이나어
            "ur", // 우르두어
            "ug", // 위구르어
            "uz", // 우즈베크
            "vi", // 베트남어
            "cy", // 웨일즈어
            "xh", // 코사어
            "yi", // 이디시어
            "yo", // 요루바어
            "zu", // 줄루어
        )
    }

    override val supportedSourceLanguageCodes: List<String> by lazy {
        mutableListOf(*supportedLanguageCodes)
            .apply { add(0, "auto") }
            .toList()
    }

    override val supportedTargetLanguageCodes: List<String> by lazy {
        listOf(*supportedLanguageCodes)
    }

    private val cloudTranslate: com.google.api.services.translate.Translate by lazy {
        val translateRequestInitializer: TranslateRequestInitializer = object : TranslateRequestInitializer(google_cloud_translation_api_key) {
            @Throws(IOException::class)
            override fun initializeTranslateRequest(translateRequest: TranslateRequest<*>) {
                super.initializeTranslateRequest(translateRequest)
                translateRequest.requestHeaders["X-Android-Package"] = packageName
                translateRequest.requestHeaders["X-Android-Cert"] = signature
            }
        }

        val cloudTranslateBuilder = Translate.Builder(NetHttpTransport(), GsonFactory.getDefaultInstance(), null)
        cloudTranslateBuilder.setTranslateRequestInitializer(translateRequestInitializer)
        cloudTranslateBuilder.applicationName = applicationName
        cloudTranslateBuilder.build()
    }

    override suspend fun translate(transRequest: TransRequest, onCompleted: (transResponse: TransResponse) -> Unit) {
        val transResponse = TransResponse(transRequest)

        try {
            val requestList = cloudTranslate.Translations().list(
                listOf(transRequest.sourceText),  //Pass in list of strings to be translated
                transRequest.targetLanguageCode //Target language
            )

            val response = requestList.execute()
            for (translationsResource in response.translations) {
                transResponse.detectedLanguageCode = translationsResource.detectedSourceLanguage
                transResponse.transText = translationsResource.translatedText
                onCompleted(transResponse)
                return
            }

            transResponse.exception = Exception("TranslationsListResponse empty")
            onCompleted(transResponse)
        } catch (e: Exception) {
            transResponse.exception = e
            onCompleted(transResponse)
        }
    }

}