package aluno.ifsc.app.focos.dengue.resources.utilidades

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.annotation.NonNull

class SharedPreferencesUtil {

    companion object {

        private fun getSharedPreferences(contex: Context?): SharedPreferences? {
            val sharedPreferences = contex?.getSharedPreferences(contex.packageName, MODE_PRIVATE)
            return if (sharedPreferences != null) {
                sharedPreferences
            } else {
                null
            }
        }

        @JvmStatic
        fun <T> get(context: Context?, @NonNull key: String, @NonNull defaultValue: T): T {
            if (context == null) {
                return defaultValue
            }
            val sharedPreferences = getSharedPreferences(context) ?: return defaultValue
            when (defaultValue) {
                is Boolean -> return sharedPreferences.getBoolean(key, defaultValue) as T
                    ?: return defaultValue
                is Int -> return sharedPreferences.getInt(key, defaultValue) as T
                    ?: return defaultValue
                is String -> return sharedPreferences.getString(key, defaultValue) as T
                    ?: return defaultValue
                is Float -> return sharedPreferences.getFloat(key, defaultValue) as T
                    ?: return defaultValue
                is Long -> return sharedPreferences.getLong(key, defaultValue) as T
                    ?: return defaultValue
                else -> return defaultValue
            }
        }

        @JvmStatic
        fun <T> put(context: Context?, @NonNull key: String, @NonNull value: T) {
            if (context == null) {
                return
            }
            val sharedPreferences = getSharedPreferences(context) ?: return
            when (value) {
                is Boolean -> sharedPreferences.edit().putBoolean(key, value).apply()
                is Int -> sharedPreferences.edit().putInt(key, value).apply()
                is String -> sharedPreferences.edit().putString(key, value).apply()
                is Float -> sharedPreferences.edit().putFloat(key, value).apply()
                is Long -> sharedPreferences.edit().putLong(key, value).apply()
            }
        }

        fun remove(context: Context?, key: String) {
            if (context == null) {
                return
            }
            val sharedPreferences = getSharedPreferences(context) ?: return
            if (contains(context, key)) {
                sharedPreferences.edit().remove(key).apply()
            }
        }

        fun contains(context: Context?, key: String): Boolean {
            if (context == null) {
                return false
            }
            val sharedPreferences = getSharedPreferences(context) ?: return false
            return sharedPreferences.contains(key)
        }

        fun clear(context: Context?) {
            if (context == null) {
                return
            }
            getSharedPreferences(context)?.edit()?.clear()?.apply()
        }


    }
}

class KeyPrefs {
    companion object {
        const val USER_ID = "idUsuario"
        const val USER_CPF = "cpfUsuario"
        const val USER_REMEMBER_CPF = "lembrarCPFUsuario"
        const val USER_PASSWORD = "senha"
        const val USER_BIRTH_DAY= "birthDay"
        const val USER_NAME = "nomeUsuario"
        const val USER_EMAIL = "emailUsuario"
        const val USER_PHONE = "foneUsuario"
        const val EDUCATION_INSTITUITION_ID = "idInstituicao"
        const val EDUCATION_INSTITUITION_NOME = "nomeInstituicao"
        const val USER_PHOTO = "fotoUsuario"
        const val USER_TYPE = "tipoUsuario"
        const val USER_REGISTRATION = "matricula"
        const val USER_YEAR_JOIN = "anoIngresso"
    }
}
