package aluno.ifsc.app.focos.dengue.model.user

import android.content.Context
import aluno.ifsc.app.focos.dengue.resources.user.UserResponse
import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.StringUtil
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class User : Serializable {
    var _id: Long? = null
    var cpf: String? = null
    var birthDay: Date? = null
    var name: String? = null
    var email: String? = null
    var phone: String? = null
    var imageUser: String? = null

    object UserMappper {

        fun transform(it: UserResponse.Login): User {
            val user =  User()
            user._id = it._id
            user.cpf = it.cpf
            user.birthDay = it.birthDay
            user.name = it.name
            user.email = it.email
            user.phone = it.phone

            user.imageUser = it.imageUser
            return user
        }

        fun transform(list: MutableList<UserResponse.Login>): MutableList<User> {
            val listRes = ArrayList<User>()
            list.forEach {
                listRes.add(transform(it))
            }
            return listRes
        }
    }

    object UserShared {
        fun clear(context : Context) {
            var cpfRemember = SharedPreferencesUtil.get(context, KeyPrefs.USER_REMEMBER_CPF, "")
            SharedPreferencesUtil.clear(context)
            SharedPreferencesUtil.put(context, KeyPrefs.USER_REMEMBER_CPF, cpfRemember)
        }

        fun save(context : Context, t : User) {
            SharedPreferencesUtil.put(context, KeyPrefs.USER_ID, t._id)
            SharedPreferencesUtil.put(context, KeyPrefs.USER_CPF, t.cpf)
            SharedPreferencesUtil.put(context, KeyPrefs.USER_NAME, t.name)
            SharedPreferencesUtil.put(context, KeyPrefs.USER_BIRTH_DAY, StringUtil.data(t.birthDay!!, "dd/MM/yyyy"))


            if(t.email != null) {
                SharedPreferencesUtil.put(context, KeyPrefs.USER_EMAIL, t.email)
            }

            if(t.phone != null) {
                SharedPreferencesUtil.put(context, KeyPrefs.USER_PHONE, t.phone)
            }

            if(t.imageUser != null) {
                SharedPreferencesUtil.put(context, KeyPrefs.USER_PHOTO, t.imageUser)
            }
        }

        fun load(context: Context) : User? {
            val cpf = SharedPreferencesUtil.get(context, KeyPrefs.USER_CPF, "")
            if(cpf.isEmpty()) {
                return null
            }

            val _id = SharedPreferencesUtil.get(context, KeyPrefs.USER_ID, 0L)
            val name = SharedPreferencesUtil.get(context, KeyPrefs.USER_NAME, "Não informado")
            val birthday = SharedPreferencesUtil.get(context, KeyPrefs.USER_BIRTH_DAY, StringUtil.data(Date(), "dd/MM/yyyy"))


            val email =  SharedPreferencesUtil.get(context, KeyPrefs.USER_EMAIL, "Não informado")
            val phone = SharedPreferencesUtil.get(context, KeyPrefs.USER_PHONE, "Não informado")
            var image = SharedPreferencesUtil.get(context, KeyPrefs.USER_PHOTO, "")

            val user = User()
            user.cpf = cpf
            user._id = _id
            user.name = name
            user.email = email
            user.birthDay = StringUtil.dataStringToDate(birthday)
            user.phone = phone
            user.imageUser = image
            return user
        }
    }
}