package aluno.ifsc.app.focos.dengue.resources.focos

import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.generics.BaseResponse
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class FocoRepository {

    private val service: FocoService
        get() {
            return FocoService()
        }

    private fun register(request: FocoRequest.Register) : Single<BaseResponse<FocoResponse.Register>> {
        return Single.create {
            try {
                val response = service.register(request)
                when {
                    response.success!! -> {
                        it.onSuccess(response)
                    }
                    else -> it.onError(Exception(response.message))
                }
            } catch (ex : java.lang.Exception) {
                it.onError(Exception("Erro ao cadastrar o foco de dengue"))
            }
        }
    }

    fun register(request: FocoRequest.Register, observer: DisposableObserver<FocoResponse.Register>){
        register(request)
            .toObservable()
            .map { it.data }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun searchAllFocos(userId : Long) : Single<BaseResponse<MutableList<FocoResponse.Register>>> {
        return Single.create {
            try {
                val response = service.searchAll(userId)
                when {
                    response.success!! -> {
                        it.onSuccess(response)
                    }
                    else -> it.onError(Exception(response.message))
                }
            } catch (ex : java.lang.Exception) {
                it.onError(Exception("Erro ao buscar os focos de dengue"))
            }
        }
    }

    fun searchAllFocos(userId : Long, observer: DisposableObserver<MutableList<FocoResponse.Register>>){
        searchAllFocos(userId)
            .toObservable()
            .map { it.data }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun searchAllFocos() : Single<BaseResponse<MutableList<FocoResponse.Register>>> {
        return Single.create {
            try {
                val response = service.searchAll()
                when {
                    response.success!! -> {
                        it.onSuccess(response)
                    }
                    else -> it.onError(Exception(response.message))
                }
            } catch (ex : java.lang.Exception) {
                it.onError(Exception("Erro ao buscar os focos de dengue"))
            }
        }
    }

    fun searchAllFocos(observer: DisposableObserver<MutableList<FocoResponse.Register>>){
        searchAllFocos()
            .toObservable()
            .map { it.data }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }
}