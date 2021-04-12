package aluno.ifsc.app.focos.dengue.ui.register

import aluno.ifsc.app.focos.dengue.R
import android.os.Bundle
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import aluno.ifsc.app.focos.dengue.model.user.User


class RegisterUserActivity : BaseActivty() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
    }

    override fun mapComponents() {
        super.mapComponents()
        val user = User.UserShared.load(this@RegisterUserActivity)
        if(user == null) {
            setTitleToolbar(getString(R.string.title_toolbar_register))
        } else {
            setTitleToolbar(getString(R.string.title_toolbar_update))
        }
    }

    override fun mapActionComponents() {
        super.mapActionComponents()
    }
}