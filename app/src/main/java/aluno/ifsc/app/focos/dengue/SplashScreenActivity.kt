package aluno.ifsc.app.focos.dengue

import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import aluno.ifsc.app.focos.dengue.ui.login.LoginActivity
import aluno.ifsc.app.focos.dengue.ui.main.MainActivity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.github.clans.fab.BuildConfig

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val lSplashAnimTime = 600

        Handler().postDelayed({
            Handler().postDelayed({
                    startActivity()
                }, 2000.toLong()
            )
        }, lSplashAnimTime.toLong())
    }

    fun startActivity() {
        var cpf = SharedPreferencesUtil.get(this@SplashScreenActivity, KeyPrefs.USER_CPF, "")
        if(cpf.isNotEmpty()) {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
        }
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        finish()
    }
}