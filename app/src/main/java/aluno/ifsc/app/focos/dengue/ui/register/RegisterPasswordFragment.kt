package aluno.ifsc.app.focos.dengue.ui.register

import aluno.ifsc.app.focos.dengue.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.BaseView
import aluno.ifsc.app.focos.dengue.resources.utilidades.ActivityUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseFragment
import aluno.ifsc.app.focos.dengue.ui.login.afterTextChanged
import aluno.ifsc.app.focos.dengue.ui.main.MainActivity
import aluno.ifsc.app.focos.dengue.ui.register.viewmodel.RegisterViewModel
import aluno.ifsc.app.focos.dengue.ui.register.viewmodel.RegisterViewModelFactory
import java.io.ByteArrayOutputStream

class RegisterPasswordFragment : BaseFragment() {

    var user : User? = null
    var byteArray : ByteArray? = null
    var bitmap : Bitmap? = null

    var editTextPassword : EditText? = null
    var editTextOldPassword : EditText? = null
    var editTextConfirmPasswod : EditText? = null

    var textInputPassword : TextInputLayout? = null
    var textInputOldPassword : TextInputLayout? = null
    var textInputConfirmPasswod : TextInputLayout? = null

    var registerViewModel :  RegisterViewModel? = null
    var button : Button? = null

    var isRegister : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getSerializable("user") as User?

            if(it.containsKey("bitmapImage")) {
                byteArray = it.getByteArray("bitmapImage") as ByteArray
                byteArray?.let {
                    var bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                    bitmap.let { user?.imageUser = bitmap.toBase64() }
                }
            } else {
                var photo = SharedPreferencesUtil.get(context, KeyPrefs.USER_PHOTO, "")
                user?.imageUser = photo
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapComponents()
        mapActionComponents()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_password, container, false)
    }

    override fun mapComponents() {

        editTextPassword = view?.findViewById(R.id.edit_text_password)
        editTextOldPassword = view?.findViewById(R.id.edit_text_old_password)
        editTextConfirmPasswod = view?.findViewById(R.id.edit_text_confirm_password)

        textInputPassword = view?.findViewById(R.id.text_input_password)
        textInputOldPassword = view?.findViewById(R.id.text_input_old_password)
        textInputConfirmPasswod = view?.findViewById(R.id.text_input_confirm_password)

        button = view?.findViewById(R.id.button_cadastre)
        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory(activity!!)).get(RegisterViewModel::class.java)
        registerViewModel!!.registerUserView.observe(this, androidx.lifecycle.Observer {
            finishConsult(it)
        })

        registerViewModel!!.updateUserView.observe(this, androidx.lifecycle.Observer {
            var password = editTextPassword?.text.toString()
            if(password.isNotEmpty()) {
                SharedPreferencesUtil.put(
                    activity,
                    KeyPrefs.USER_PASSWORD,
                    editTextPassword?.text.toString()
                )
            }
            finishConsult(it)
        })

        arguments?.let {
            if(it.containsKey("register")) {
                isRegister = it.getBoolean("register")
                if(isRegister.not()) {
                    button?.isEnabled = true
                    textInputOldPassword?.visibility = View.VISIBLE
                    textInputPassword?.isEnabled = false
                    textInputConfirmPasswod?.isEnabled = false
                    textInputPassword?.hint = getString(R.string.prompt_new_password)
                    button?.text = getString(R.string.prompt_update)
                }
            }
        }
    }

    fun finishConsult(it : BaseView<User>) {
        hideLoading()
        if(it.error!!.not()) {
            ActivityUtil.Builder(activity!!, MainActivity::class.java).build()
            activity!!.finish()
        } else {
            Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
        }
    }

    override fun mapActionComponents() {
        editTextOldPassword?.afterTextChanged {
            enableButtonNext()
        }

        editTextPassword?.afterTextChanged {
            enableButtonNext()
        }

        editTextConfirmPasswod?.afterTextChanged {
            enableButtonNext()
        }

        editTextConfirmPasswod?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(button!!.isEnabled) {
                    button!!.performClick()
                }
            }
            false
        }

        button?.setOnClickListener{
            registerUser()
        }
    }

    fun registerUser() {
        if(isRegister) {
            showLoading(getString(R.string.prompt_loading_register))
            registerViewModel?.registerUser(user!!, editTextPassword?.text.toString())
        } else {
            showLoading(getString(R.string.prompt_loading_update))
            registerViewModel?.updateUser(user!!, editTextPassword?.text.toString())
        }
    }

    fun enableButtonNext()  {
        if(isRegister) {
            button?.isEnabled = validateFileds()
        } else {
            if(editTextOldPassword?.text.toString().isNotEmpty()) {
                button?.isEnabled = false
                button?.isEnabled = validateFiledsUpdate()
            }
        }
    }

    fun enableEditText(boolean: Boolean) {
        textInputPassword?.isEnabled = boolean
        textInputConfirmPasswod?.isEnabled = boolean
    }

    fun validateFileds() : Boolean {
        if(validatePassword().not()) { return false }
        if(validateConfirmPassword().not()) { return false }
        return true
    }

    fun validateFiledsUpdate() : Boolean {
        if(validateOldPassword().not()) {
            enableEditText(false)
            return false
        }

        enableEditText(true)
        if(validatePassword().not()) {
            return false
        }

        if(validateConfirmPassword().not()) {
            return false
        }

        return true
    }

    fun validateOldPassword() : Boolean {
        var password = editTextOldPassword?.text.toString()
        val passwordOld = SharedPreferencesUtil.get(context, KeyPrefs.USER_PASSWORD, "")
        textInputOldPassword?.helperText = null
        if (password?.equals(passwordOld).not()) {
            textInputOldPassword?.helperText = getString(R.string.error_invalid_old_password)
            return false
        }
        return true
    }

    fun validatePassword() : Boolean {
        var password = editTextPassword?.text.toString()
        textInputPassword?.helperText = null
        if (password.length < 6) {
            textInputPassword?.helperText = getString(R.string.error_invalid_password)
            return false
        }
        return true
    }

    fun validateConfirmPassword() : Boolean {
        var password = editTextPassword?.text.toString()
        var passwordConfirm = editTextConfirmPasswod?.text.toString()
        textInputConfirmPasswod?.helperText = null
        if (password.isEmpty()) {
            textInputConfirmPasswod?.helperText = getString(R.string.error_invalid_password)
            return false
        } else if (passwordConfirm.equals(password).not()) {
            textInputConfirmPasswod?.helperText = getString(R.string.error_invalid_confirm_password)
            return false
        } else {
            return true
        }
    }
}

fun Bitmap?.toBase64(): String {
    val baos = ByteArrayOutputStream()
    this?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val toByteArray = baos.toByteArray()
    return Base64.encodeToString(toByteArray, Base64.DEFAULT)
}
