package aluno.ifsc.app.focos.dengue.resources.user

import aluno.ifsc.app.focos.dengue.resources.Api
import aluno.ifsc.app.focos.dengue.resources.generics.RetrofitImpl
import aluno.ifsc.app.focos.dengue.resources.generics.BaseResponse

class UserService {

    fun login(request : UserRequest.Login) : BaseResponse<UserResponse.Login> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.User::class.java)
        val objectCall = api.login(request)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao realizar a requisição")
        }
    }

    fun register(request : UserRequest.Register) : BaseResponse<UserResponse.Login> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.User::class.java)
        val objectCall = api.register(request)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao realizar o registro do usuário")
        }
    }

    fun update(request : UserRequest.Register) : BaseResponse<UserResponse.Login> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.User::class.java)
        val objectCall = api.update(request)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao atualizar o registro")
        }
    }

    fun sendEmail(request : UserRequest.Email) : BaseResponse<Boolean> {
        val lRetrofit = RetrofitImpl().buildRetrofit()
        val api = lRetrofit.create(Api.User::class.java)
        val objectCall = api.sendEmail(request)
        val execute = objectCall.execute()
        val body = execute.body()
        if(body != null) {
            return body
        } else {
            throw Exception("Erro ao enviar o email")
        }
    }
}