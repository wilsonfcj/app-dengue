package aluno.ifsc.app.focos.dengue.resources.focos

import com.google.gson.annotations.SerializedName
import java.util.*

sealed class FocoResponse {

    open class Register(
        @SerializedName(value = "Id")
        var id : Long,

        @SerializedName(value = "Descricao")
        var descricao : String,

        @SerializedName(value = "Endereco")
        var endereco : String,

        @SerializedName(value = "Latitude")
        var latitude : Double,

        @SerializedName(value = "Longitude")
        var longitude : Double,

        @SerializedName(value = "Imagem")
        var imagem : String,

        @SerializedName(value = "DataCadastro")
        var dataCadastro : Date,

        @SerializedName(value = "Usuario")
        var usuario : UsuarioRegisterResponse

    )

    open class UsuarioRegisterResponse (
        @SerializedName("IdUsuario")
        var id: Long? = null,

        @SerializedName("Nome")
        var nome: String? = null,

        @SerializedName("Email")
        var email: String? = null
    )
}