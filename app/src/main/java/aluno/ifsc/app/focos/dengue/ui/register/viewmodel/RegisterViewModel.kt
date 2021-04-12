package aluno.ifsc.app.focos.dengue.ui.register.viewmodel

import aluno.ifsc.app.focos.dengue.R
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.BaseView
import aluno.ifsc.app.focos.dengue.resources.user.UserRepository
import aluno.ifsc.app.focos.dengue.resources.utilidades.ConnectionUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import io.reactivex.observers.DisposableObserver

class RegisterViewModel (var activity: Context) : ViewModel() {

    var repository1 : UserRepository = UserRepository()
    val _registerUserView = MutableLiveData<BaseView<User>>()
    var registerUserView : LiveData<BaseView<User>> = _registerUserView
    val _updateUserView = MutableLiveData<BaseView<User>>()
    var updateUserView : LiveData<BaseView<User>> = _updateUserView

    fun registerUser(user : User, password : String) {
        repository1.register(user, password, object : DisposableObserver<User>() {
            override fun onComplete() {

            }

            override fun onNext(t: User) {
                User.UserShared.save(activity, t)
                SharedPreferencesUtil.put(activity, KeyPrefs.USER_REMEMBER_CPF, t.cpf)
                _registerUserView.value = BaseView(success = t, error = false, message = "Usuário cadastrado com sucesso")
            }

            override fun onError(e: Throwable) {
                var msm = e.message
                if (ConnectionUtil.isNetworkAvailable(activity).not()) {
                    msm = activity.getString(R.string.error_conection)
                }
                _registerUserView.value = BaseView(success = null, error = true, message = msm)
            }
        })
    }

    fun updateUser(user : User, password : String) {
        repository1.update(user, password, object : DisposableObserver<User>() {
            override fun onComplete() {

            }

            override fun onNext(t: User) {
                User.UserShared.save(activity, t)
                _updateUserView.value = BaseView(success = t, error = false, message = "Usuário alterado com sucesso")
            }

            override fun onError(e: Throwable) {
                var msm = e.message
                if (ConnectionUtil.isNetworkAvailable(activity).not()) {
                    msm = activity.getString(R.string.error_conection)
                }
                _updateUserView.value = BaseView(success = null, error = true, message = msm)
            }
        })
    }

}