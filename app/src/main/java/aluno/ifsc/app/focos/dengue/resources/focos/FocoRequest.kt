package aluno.ifsc.app.focos.dengue.resources.focos

import com.google.gson.annotations.SerializedName
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.utilidades.StringUtil
import java.util.*
import kotlin.collections.ArrayList

sealed class FocoRequest {

    open class Register(
        @SerializedName("Descricao")
        var descricao : String? = null,

        @SerializedName("Endereco")
        var  endereco : String? = null,

        @SerializedName("Latitude")
        var latitude : Double? = null,

        @SerializedName("Longitude")
        var longitude : Double? = null,

        @SerializedName("Imagem")
        var image: String? = null,

        @SerializedName("DataCadastro")
        var dataCadastro : String? = null,

        @SerializedName("IdUsuario")
        var idUsuario : Long? = null
    )
}