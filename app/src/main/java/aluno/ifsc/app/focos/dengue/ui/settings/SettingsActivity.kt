package aluno.ifsc.app.focos.dengue.ui.settings

import aluno.ifsc.app.focos.dengue.BuildConfig
import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.ui.login.LoginActivity
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.utilidades.*
import aluno.ifsc.app.focos.dengue.resources.utilidades.components.CustomItemUser
import aluno.ifsc.app.focos.dengue.ui.register.RegisterUserActivity
import net.cachapa.expandablelayout.ExpandableLayout

class SettingsActivity : BaseActivty() {

    var textViewName : TextView? = null
    var imageProfile : ImageView? = null
    var buttonExit :  Button? = null
    var buttonChangeInfos: Button? = null

    var customEmail : CustomItemUser? = null
    var customPhone : CustomItemUser? = null
    var customBirthday : CustomItemUser? = null
    var customVersionCode : CustomItemUser? = null

    var user : User? = null

    var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }


    override fun mapComponents() {
        super.mapComponents()
        setDisplayHomeAs(false)
        setTitleToolbar(getString(R.string.title_toolbar_my_profile))
        imageProfile = findViewById(R.id.img_view_user)
        buttonExit = findViewById(R.id.button_exit)
        buttonChangeInfos = findViewById(R.id.button_change_infos)
        recyclerView = findViewById(R.id.recycler_view)
        textViewName = findViewById(R.id.txt_view_user_name)

        customEmail = findViewById(R.id.custom_email)
        customPhone = findViewById(R.id.custom_phone)
        customBirthday = findViewById(R.id.custom_birth_day)

        customVersionCode = findViewById(R.id.custom_version_code)

        var CPF = SharedPreferencesUtil.get(this@SettingsActivity, KeyPrefs.USER_CPF, "")
        formatCPF(CPF)

        var name = SharedPreferencesUtil.get(this@SettingsActivity, KeyPrefs.USER_NAME, "")
        textViewName!!.text = "Olá\n$name"

        var image = SharedPreferencesUtil.get(this@SettingsActivity, KeyPrefs.USER_PHOTO, "")
        if(image.isNotEmpty()) {
            imageProfile?.setImageBitmap(ImageUtil.convertBase64ToBitmap(image))
        }

        showDisplayInfos()
    }

    fun formatCPF(cpf : String) : String {
        var part1 = cpf.substring(0, 3)
        var part2 = cpf.substring(3, 6)
        var part3 = cpf.substring(6, 9)
        var part4 = cpf.substring(9, 11)
        var cpfFormt = "{$part1}.{$part2}.{$part3}-{$part4}"
        return cpfFormt
    }

    fun showDisplayInfos() {
        user = User.UserShared.load(this@SettingsActivity)
        customEmail?.setTitle(user?.email!!)
        customPhone?.setTitle(user?.phone!!)
        customBirthday?.setTitle(StringUtil.data(user?.birthDay!!, "dd/MM/yyyy"))
        customVersionCode?.setTitle(BuildConfig.VERSION_NAME)
    }

    override fun mapActionComponents() {
        super.mapActionComponents()
        buttonExit?.setOnClickListener {
            User.UserShared.clear(this@SettingsActivity)
            val lIntent = Intent(this@SettingsActivity, LoginActivity::class.java)
            lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            lIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(lIntent)
            finish()
        }

        buttonChangeInfos?.setOnClickListener {
            ActivityUtil.Builder(applicationContext, RegisterUserActivity::class.java).build()
        }
    }
}