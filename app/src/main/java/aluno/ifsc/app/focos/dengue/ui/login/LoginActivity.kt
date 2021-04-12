package aluno.ifsc.app.focos.dengue.ui.login

import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.ui.main.MainActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.ViewModelProvider
import androidx.transition.ArcMotion
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import com.google.android.material.textfield.TextInputLayout
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.utilidades.*
import aluno.ifsc.app.focos.dengue.ui.register.RegisterUserActivity
import com.ifsc.lages.sti.tcc.ui.login.viewmodel.LoginViewModel
import com.ifsc.lages.sti.tcc.ui.login.viewmodel.LoginViewModelFactory


class LoginActivity : BaseActivty() {

    private val TAG = "LoginActivity"

    private var mShowingKeyboard: Boolean = false
    private var login : Button? = null
    var loginUserHelper : TextInputLayout? = null
    var passwordHelper : TextInputLayout? = null
    var loginUser : EditText? = null
    var password : EditText? = null
    var chip : SwitchCompat? = null
    var containerRegister : FrameLayout? = null

    private var viewModel :  LoginViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun loginDataChanged(login: String, password: String) : Boolean {
        if (!login.isCpfCnpjValid()) {
            if (login.length <= 11) {
                loginUserHelper?.helperText = null
                loginUserHelper?.helperText = getString(R.string.error_login_invalido)
                return false
            }
        } else {
            loginUserHelper?.helperText = null
        }

        if (!isPasswordValid(password)) {
            passwordHelper?.helperText = null
            passwordHelper?.helperText =  getString(R.string.error_invalid_password)
            return false
        } else {
            passwordHelper?.helperText = null
        }
        return true
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank() && password.length > 5
    }

    fun String.isCpfCnpjValid(): Boolean {
        return this.isNotBlank() && (StringUtil.isCpfValid(this) || StringUtil.isCNPJValid(this))
    }

   override fun createRestListener() {
       viewModel = ViewModelProvider(this,
           LoginViewModelFactory(this@LoginActivity)
       ).get(LoginViewModel::class.java)
       viewModel!!.loginViewMonitoring.observe(this, androidx.lifecycle.Observer {
           hideLoading()
           if(it.error!!.not()) {
               setRememberCPF(it.success!!)
               SharedPreferencesUtil.put(applicationContext, KeyPrefs.USER_PASSWORD,  password?.text.toString())
               ActivityUtil.Builder(applicationContext, MainActivity::class.java).build()
               finish()
           } else {
               Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_LONG).show()
           }
       })
   }

    fun setRememberCPF(it : User) {
        if(chip!!.isChecked) {
            SharedPreferencesUtil.put(this@LoginActivity, KeyPrefs.USER_REMEMBER_CPF, it.cpf)
        } else {
            SharedPreferencesUtil.put(this@LoginActivity, KeyPrefs.USER_REMEMBER_CPF, "")
        }
    }

    fun getCpfRemember() {
        var cpf = SharedPreferencesUtil.get(this@LoginActivity, KeyPrefs.USER_REMEMBER_CPF, "")
        if(cpf.isNotEmpty()) {
            loginUser?.setText(cpf)
        }
    }

    override fun mapComponents() {
        super.mapComponents()
        login = findViewById(R.id.login)
        loginUserHelper = findViewById(R.id.containerUsername)
        loginUser = findViewById(R.id.username)
        passwordHelper = findViewById(R.id.containerPassword)
        password = findViewById(R.id.password)

//        TODO usar este botão
        chip = findViewById(R.id.chip)
        val chipTouch = findViewById<SwitchCompat>(R.id.chip2)
        containerRegister = findViewById<FrameLayout>(R.id.container_register)

        EditTextMask.addCpfMask(loginUser!!)
        getCpfRemember()

        password?.apply { transformationMethod = PasswordTransformationMethod() }

        findViewById<LinearLayout>(R.id.containerChipTouch).setOnClickListener {
            chipTouch.performClick()
        }

        chipTouch?.setOnCheckedChangeListener { _, _ -> }
        loginUser?.afterTextChanged {
            login?.isEnabled = loginDataChanged(
                loginUser?.text.toString().removeMask(),
                password?.text.toString()
            )
        }

        password?.afterTextChanged {
            login?.isEnabled = loginDataChanged(
                loginUser?.text.toString().removeMask(),
                password?.text.toString()
            )
        }

        password?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(login!!.isEnabled) {
                    eventLogin()
                }
            }
            false
        }
    }

    fun eventLogin() {
        showLoading("Autenticando usuário")
        viewModel?.login(loginUser?.text.toString().removeMask(), password?.text.toString())
    }

    fun eventRegister() {
        ActivityUtil.Builder(applicationContext, RegisterUserActivity::class.java).build()
    }

    override fun mapActionComponents() {
        containerRegister?.setOnClickListener {
            eventRegister()
        }

        login?.setOnClickListener {
            eventLogin()
        }

        KeyboardUtil.addKeyboardToggleListener(
            this,
            object : KeyboardUtil.SoftKeyboardToggleListener {

                override fun onToggleSoftKeyboard(isVisible: Boolean) {
                    mShowingKeyboard = isVisible
                    Log.v("LOGIN", "onToggleSoftKeyboard() $isVisible")

                    Handler().postDelayed(
                        {
                            val constraintLayout = findViewById<ConstraintLayout>(R.id.container)
                            val constraintSet = ConstraintSet()
                            constraintSet.clone(constraintLayout)
                            if (isVisible) {
                                constraintSet.constrainHeight(R.id.logoTip, 80)
                                constraintSet.constrainWidth(R.id.logoTip, 80)
                                constraintSet.setMargin(R.id.logoTip, ConstraintSet.TOP, 8)
                                constraintSet.setHorizontalBias(R.id.logoTip, 1.0f)
                            } else {
                                constraintSet.clone(this@LoginActivity,
                                    R.layout.activity_login
                                )
                            }
                            val transition = ChangeBounds()
                            transition.setPathMotion(ArcMotion())
                            transition.duration = 400
                            transition.interpolator = AccelerateDecelerateInterpolator()

                            TransitionManager.beginDelayedTransition(constraintLayout, transition)
                            constraintSet.applyTo(constraintLayout)
                        }, 400
                    )
                }
            })
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.beforeTextChanged(beforeTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            beforeTextChanged.invoke(s.toString())
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}


fun String.removeMask(): String {
    return EditTextMask.removeCpfCnpjMask(this)
}
