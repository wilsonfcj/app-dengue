package aluno.ifsc.app.focos.dengue.resources.generics

import com.google.gson.annotations.SerializedName

open class BaseResponse< Data : Any?>(
    @SerializedName("Sucesso")
    val success: Boolean? = null,
    @SerializedName("Mensagem")
    val message: String? = "",
    @SerializedName("Data")
    var data: Data? = null
)