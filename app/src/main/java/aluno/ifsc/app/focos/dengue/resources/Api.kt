package aluno.ifsc.app.focos.dengue.resources


import aluno.ifsc.app.focos.dengue.resources.focos.FocoRequest
import aluno.ifsc.app.focos.dengue.resources.focos.FocoResponse
import aluno.ifsc.app.focos.dengue.resources.generics.BaseResponse
import aluno.ifsc.app.focos.dengue.resources.user.UserRequest
import aluno.ifsc.app.focos.dengue.resources.user.UserResponse
import retrofit2.Call
import retrofit2.http.*

interface Api {

    interface User {
        @POST("Login")
        fun login(@Body request : UserRequest.Login) : Call<BaseResponse<UserResponse.Login>>

        @POST("Cadastro")
        fun register(@Body request : UserRequest.Register) : Call<BaseResponse<UserResponse.Login>>

        @PUT("Alterar")
        fun update(@Body request : UserRequest.Register) : Call<BaseResponse<UserResponse.Login>>

        @POST("EnviarEmail")
        fun sendEmail(@Body request : UserRequest.Email) : Call<BaseResponse<Boolean>>
    }

    interface Focos {
        @POST("CadastrarFoco")
        fun register(@Body request : FocoRequest.Register) : Call<BaseResponse<FocoResponse.Register>>

        @GET("BuscarFocos")
        fun searchAllFocos() : Call<BaseResponse<MutableList<FocoResponse.Register>>>

        @GET("BuscarFocosPorId")
        fun searchAllFocosById(@Query("idUsuario") idUsuario : Long) : Call<BaseResponse<MutableList<FocoResponse.Register>>>
    }
}