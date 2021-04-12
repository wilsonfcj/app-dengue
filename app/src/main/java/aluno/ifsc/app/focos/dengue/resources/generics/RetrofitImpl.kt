package aluno.ifsc.app.focos.dengue.resources.generics

import aluno.ifsc.app.focos.dengue.BuildConfig
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RetrofitImpl(private val url: String = BuildConfig.WS_URL) {

    class DateDeserializer : JsonDeserializer<Date> {
        @Throws(JsonParseException::class)
        override fun deserialize(
            element: JsonElement,
            arg1: Type,
            arg2: JsonDeserializationContext
        ): Date {
            val lDate = element.asString
            val lFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            return try {
                lFormat.parse(lDate)!!
            } catch (exp: ParseException) {
                val datereip = lDate.replace("/Date(", "").replace(")/", "")
                val timeInMillis = java.lang.Long.valueOf(datereip)
                val lCalendar = Calendar.getInstance()
                lCalendar.timeInMillis = timeInMillis
                var startTimeMillis =
                    lCalendar[Calendar.HOUR_OF_DAY] * 1000 * 60 * 60 + (lCalendar[Calendar.MINUTE] * 1000 * 60).toLong()
                startTimeMillis -= getTimeOffset(startTimeMillis).toLong()
                Date(startTimeMillis)
                Date(timeInMillis)
            }
        }
    }

    fun buildRetrofit(): Retrofit { //        final Gson lGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();
        val lGson =
            GsonBuilder().registerTypeAdapter(Date::class.java, DateDeserializer())
                .create()
        val lInterceptor = HttpLoggingInterceptor()
        lInterceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

        val lBuild = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(lInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(lGson))
            .client(lBuild)
            .build()
    }

    companion object {
        private fun getTimeOffset(time: Long): Int {
            val tz = TimeZone.getDefault()
            return tz.getOffset(time)
        }

        fun getParams(aRequest: Any?): Map<String, String> {
            val lJson = Gson().toJson(aRequest)
            //      TODO  Estava transformando todos os tipos de dados em String e com outras tipagens dava problema, por isso a alteração.
            val lType = object :
                TypeToken<Map<String?, String?>?>() {}.type
            return Gson().fromJson(
                lJson,
                lType
            )
        }
    }
}