package aluno.ifsc.app.focos.dengue.resources.utilidades

import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import com.github.rtoshiro.util.format.SimpleMaskFormatter
import com.github.rtoshiro.util.format.text.MaskTextWatcher

object EditTextMask {
    val CPF_MASK = "NNN.NNN.NNN-NN"
    val CNPJ_MASK = "NN.NNN.NNN/NNNN-NN"
    private val PLACA_MASK = "UUU-NNNN"
    private val CEP_MASK = "NNNNN-NNN"
    private val AGENCIA_DV = "NNNN-NN"
    private val TELEFONE11_MASK = "(NN) NNNN-NNNNN"
    private val TELEFONE12_MASK = "(NN)NNNN-NNNNN"
    private val TELEFONE13_MASK = "(NN)NNNNN-NNNN"
    private val TELEFONE14_MASK = "(NN) NNNNN-NNNN"

    fun addCpfMask(editText: EditText) {
        val smf = SimpleMaskFormatter(CPF_MASK)
        val mtw = MaskTextWatcher(editText, smf)
        editText.addTextChangedListener(mtw)
    }

    fun addCnpjMask(editText: EditText) {
        val smf = SimpleMaskFormatter(CNPJ_MASK)
        val mtw = MaskTextWatcher(editText, smf)
        editText.addTextChangedListener(mtw)
    }

    fun addPlacaMask(editText: EditText) {
        val smf = SimpleMaskFormatter(PLACA_MASK)
        val mtw = MaskTextWatcher(editText, smf)
        editText.addTextChangedListener(mtw)
        editText.filters = arrayOf<InputFilter>(InputFilter.AllCaps())
        editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    fun addCepMask(editText: EditText) {
        val smf = SimpleMaskFormatter(CEP_MASK)
        val mtw = MaskTextWatcher(editText, smf)
        editText.addTextChangedListener(mtw)
    }

    fun addAgenciaMask(editText: EditText) {
        val smf = SimpleMaskFormatter(AGENCIA_DV)
        val mtw = MaskTextWatcher(editText, smf)
        editText.addTextChangedListener(mtw)
    }

    fun addTelefoneMask(editText: EditText) {
        val smf13 = SimpleMaskFormatter(TELEFONE13_MASK)
        val mtw13 = MaskTextWatcher(editText, smf13)
        editText.addTextChangedListener(mtw13)
    }

    fun unmask(s: String): String {
        return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "").replace("[/]".toRegex(), "")
            .replace("[(]".toRegex(), "").replace("[)]".toRegex(), "").replace("[:]".toRegex(), "")
            .replace("[ ]".toRegex(), "")
    }

    fun formatCepMask(textoParaFormatar: String): String {
        val smf = SimpleMaskFormatter(CEP_MASK)
        return smf.format(textoParaFormatar)
    }

    fun formatCpfMask(textoParaFormatar: String): String {
        val smf = SimpleMaskFormatter(CPF_MASK)
        return smf.format(textoParaFormatar)
    }

    fun formatCnpjMask(textoParaFormatar: String): String {
        val smf = SimpleMaskFormatter(CNPJ_MASK)
        return smf.format(textoParaFormatar)
    }

    fun formatCpfCnpjMask(cpfCnpj: String): String {
        return if (cpfCnpj.length > 11) {
            formatCnpjMask(cpfCnpj)
        } else {
            formatCpfMask(cpfCnpj)
        }
    }

    @JvmOverloads
    fun formatTelefoneMask(textoParaFormatar: String, lWithSpace: Boolean = false): String {
        var textoParaFormatar = textoParaFormatar
        val smf: SimpleMaskFormatter
        textoParaFormatar = unmask(textoParaFormatar)
        if (textoParaFormatar.length < 11) {
            if (lWithSpace) {
                smf = SimpleMaskFormatter(TELEFONE11_MASK)
            } else {
                smf = SimpleMaskFormatter(TELEFONE12_MASK)
            }
        } else {
            if (lWithSpace) {
                smf = SimpleMaskFormatter(TELEFONE14_MASK)
            } else {
                smf = SimpleMaskFormatter(TELEFONE13_MASK)
            }
        }

        return smf.format(textoParaFormatar)
    }

    fun addCpfCnpjMask(aEditText: EditText?) {

        val lCpfMask = SimpleMaskFormatter(CPF_MASK)
        val lCpfWatcher = MaskTextWatcher(aEditText, lCpfMask)

        val lCnpjMask = SimpleMaskFormatter(CNPJ_MASK)
        val lCnpjWatcher = MaskTextWatcher(aEditText, lCnpjMask)

        aEditText!!.removeTextChangedListener(lCpfWatcher)
        aEditText.removeTextChangedListener(lCnpjWatcher)

        aEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) { /* do nothing*/
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                aEditText.removeTextChangedListener(lCnpjWatcher)
                aEditText.removeTextChangedListener(lCpfWatcher)
                if (aEditText.text.length > 14) {
                    aEditText.addTextChangedListener(lCnpjWatcher)
                } else {
                    aEditText.addTextChangedListener(lCpfWatcher)
                }
            }

            override fun afterTextChanged(editable: Editable) { /* do nothing */
            }
        })
    }

    fun removeCpfCnpjMask(aMask: String): String {
        return aMask.replace(".", "").replace("/", "").replace("-", "").trim { it <= ' ' }
    }
}

fun String.maskCpfCnpj(): String{
    return if (this.length <= 11) {
        val smf = SimpleMaskFormatter(EditTextMask.CPF_MASK)
        smf.format(this)
    } else {
        val smf = SimpleMaskFormatter(EditTextMask.CNPJ_MASK)
        smf.format(this)
    }
}