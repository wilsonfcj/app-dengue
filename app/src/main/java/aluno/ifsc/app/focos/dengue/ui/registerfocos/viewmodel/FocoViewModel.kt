package aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel

import aluno.ifsc.app.focos.dengue.R
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.BaseView
import aluno.ifsc.app.focos.dengue.resources.focos.FocoRepository
import aluno.ifsc.app.focos.dengue.resources.focos.FocoRequest
import aluno.ifsc.app.focos.dengue.resources.focos.FocoResponse
import aluno.ifsc.app.focos.dengue.resources.user.UserRepository
import aluno.ifsc.app.focos.dengue.resources.utilidades.ConnectionUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import io.reactivex.observers.DisposableObserver

class FocoViewModel (var activity: Context) : ViewModel() {

    var repository : FocoRepository = FocoRepository()

    private val _registerView = MutableLiveData<BaseView<FocoResponse.Register>>()
    var registerView : LiveData<BaseView<FocoResponse.Register>> = _registerView

    private val _loadAllView = MutableLiveData<BaseView<MutableList<FocoResponse.Register>>>()
    var loadAllView : LiveData<BaseView<MutableList<FocoResponse.Register>>> = _loadAllView

    private val _loadAllByIdView = MutableLiveData<BaseView<MutableList<FocoResponse.Register>>>()
    var loadAllByIdView : LiveData<BaseView<MutableList<FocoResponse.Register>>> = _loadAllByIdView

    fun register(request: FocoRequest.Register) {
        repository.register(request, object : DisposableObserver<FocoResponse.Register>() {
            override fun onComplete() {

            }

            override fun onNext(t: FocoResponse.Register) {
                _registerView.value = BaseView(success = t, error = false, message = "Foco registrado com sucesso")
            }

            override fun onError(e: Throwable) {
                var msm = e.message
                if (ConnectionUtil.isNetworkAvailable(activity).not()) {
                    msm = activity.getString(R.string.error_conection)
                }
                _registerView.value = BaseView(success = null, error = true, message = msm)
            }
        })
    }

    fun searchAllFocos() {
        repository.searchAllFocos( object : DisposableObserver<MutableList<FocoResponse.Register>>() {
            override fun onComplete() {

            }

            override fun onNext(t: MutableList<FocoResponse.Register>) {
                _loadAllView.value = BaseView(success = t, error = false, message = "Focos consultados com sucesso")
            }

            override fun onError(e: Throwable) {
                var msm = e.message
                if (ConnectionUtil.isNetworkAvailable(activity).not()) {
                    msm = activity.getString(R.string.error_conection)
                }
                _loadAllView.value = BaseView(success = null, error = true, message = msm)
            }
        })
    }


    fun searchAllFocos(userId : Long) {
        repository.searchAllFocos(userId, object : DisposableObserver<MutableList<FocoResponse.Register>>() {
            override fun onComplete() {}

            override fun onNext(t: MutableList<FocoResponse.Register>) {
                _loadAllByIdView.value = BaseView(success = t, error = false, message = "Focos consultados com sucesso")
            }

            override fun onError(e: Throwable) {
                var msm = e.message
                if (ConnectionUtil.isNetworkAvailable(activity).not()) {
                    msm = activity.getString(R.string.error_conection)
                }
                _loadAllByIdView.value = BaseView(success = null, error = true, message = msm)
            }
        })
    }
}