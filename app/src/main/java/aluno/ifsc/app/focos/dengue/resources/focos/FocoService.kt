package aluno.ifsc.app.focos.dengue.resources.focos

import aluno.ifsc.app.focos.dengue.resources.Api
import aluno.ifsc.app.focos.dengue.resources.generics.RetrofitImpl
import aluno.ifsc.app.focos.dengue.resources.generics.BaseResponse

class FocoService {

    fun register(request : FocoRequest.Register) : BaseResponse<FocoResponse.Register> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.Focos::class.java)
        val objectCall = api.register(request)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao realizar a requisição")
        }
    }


    fun searchAll() : BaseResponse<MutableList<FocoResponse.Register>> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.Focos::class.java)
        val objectCall = api.searchAllFocos()
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao realizar a requisição")
        }
    }

    fun searchAll(userId : Long) : BaseResponse<MutableList<FocoResponse.Register>> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.Focos::class.java)
        val objectCall = api.searchAllFocosById(userId)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao realizar a requisição")
        }
    }
}