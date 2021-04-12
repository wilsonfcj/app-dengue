package aluno.ifsc.app.focos.dengue.ui.registerfocos

import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.resources.focos.FocoRequest
import aluno.ifsc.app.focos.dengue.resources.utilidades.*
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import aluno.ifsc.app.focos.dengue.ui.login.afterTextChanged
import aluno.ifsc.app.focos.dengue.ui.register.toBase64
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModel
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModelFactory
import aluno.ifsc.app.focos.dengue.ui.sharelocation.ActivityShareLocation
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import net.cachapa.expandablelayout.ExpandableLayout
import java.io.File
import java.util.*

class RegisteFocosActivity : BaseActivty(),  ImageUtil.Create {

    var button : MaterialButton? = null
    var editTextDescription : EditText? = null
    var textInputDescription : TextInputLayout? = null
    var address: String? = null
    var cidade: String? = null
    var location: LatLng? = null
    var relativeLayout : RelativeLayout? = null
    var linearLayout : LinearLayout? = null
    var imageView : ImageView? = null
    var imageViewMap : ImageView? = null
    var base64 : String? = null
    var bitmap: Bitmap? = null
    var expandableLocation : ExpandableLayout? = null
    var editTextAddress : EditText? = null
    var textInputAddress : TextInputLayout? = null

    private var viewModel :  FocoViewModel? = null
    var focoResquest : FocoRequest.Register? = null

    private lateinit var mCurrentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_focos_generic)
        setTitleToolbar(getString(R.string.title_toolbar_register_foco))
    }

    override fun mapComponents() {
        super.mapComponents()
        editTextDescription = findViewById(R.id.edit_text_description)
        textInputDescription = findViewById(R.id.text_input_description)
        relativeLayout = findViewById(R.id.rl_buttons_description)
        linearLayout = findViewById(R.id.containner_add)
        button = findViewById(R.id.button_cadastre)
        imageView = findViewById(R.id.book_img_id)
        imageViewMap = findViewById(R.id.imageView_tmbl_map)
        expandableLocation = findViewById(R.id.expandable_location)
        editTextAddress = findViewById(R.id.edit_text_address)
    }

    override fun mapActionComponents() {
        super.mapActionComponents()
        editTextDescription?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                showSharedLocation()
            }
            false
        }

        editTextAddress?.setOnClickListener {
            showSharedLocation()
        }

        button?.setOnClickListener {
            showLoading("Cadastrando informações")
            updateRequest()
            viewModel!!.register(focoResquest!!)
        }

        editTextDescription?.afterTextChanged { enableButtonNext() }
        linearLayout?.setOnClickListener { ImageUtil.checkPremission(this@RegisteFocosActivity, this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ActivityRequestCode.CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    camera()
                }
            }

            ActivityRequestCode.SHARELOCATION -> {
                if (resultCode != RESULT_OK) {
                    return
                }
                if (data != null) {
                    val lLatitude = data.getDoubleExtra("latitude", 0.0)
                    val lLongitude = data.getDoubleExtra("longitude", 0.0)
                    location = LatLng(lLatitude, lLongitude)
                    address = data.getStringExtra("endereco")
                    cidade = data.getStringExtra("cidade")
                    setImageLocation()
                }
            }
        }
    }

    fun setImageLocation() {
        editTextAddress!!.setText(address)
        enableButtonNext()
    }

    fun camera() {
        addImage(mCurrentPhotoPath)
    }

    fun enableButtonNext() {
        button?.isEnabled = validateFileds()
    }

    fun addImage(aCurrentPath : String) {
        bitmap =  ImageUtil.camera(File(aCurrentPath), aCurrentPath)
        base64 = bitmap.toBase64()
        imageView!!.setImageBitmap(bitmap)
        imageView!!.visibility = View.VISIBLE
    }

    fun validateDescription() : Boolean {
        if (editTextDescription!!.text.toString().isNotEmpty()) {
            textInputDescription?.helperText = null
            return true
        }
        textInputDescription?.helperText = null
        textInputDescription?.helperText = getString(R.string.error_invalid_name_description)
        return false
    }

    fun validateLocation() : Boolean {
        if (location != null) {
            return true
        }
        return false
    }


    fun validateFileds() : Boolean {
        if(validateDescription().not()) { return false }
        if(validateLocation().not()) { return false }
        updateRequest()
        return true
    }

    fun updateRequest() {
        val userId = SharedPreferencesUtil.get(this@RegisteFocosActivity, KeyPrefs.USER_ID, 0L)
        focoResquest = FocoRequest.Register()
        focoResquest?.let {
            it.dataCadastro = StringUtil.data(Date(), "yyyy-MM-dd'T'HH:mm:ss")
            it.idUsuario = userId
            it.latitude = location?.latitude
            it.longitude = location?.longitude
            it.descricao = editTextDescription?.text.toString()
            it.endereco = editTextAddress?.text.toString()
            base64 = bitmap.toBase64()
            it.image = base64
        }
    }

    private fun showSharedLocation() {
        val lIntent = Intent(this, ActivityShareLocation::class.java)
        val lBundle = Bundle()
        lIntent.putExtras(lBundle)
        location?.let {
            lBundle.putDouble("latitude", location!!.latitude)
            lBundle.putDouble("longitude", location!!.longitude)
        }
        startActivityForResult(lIntent,  ActivityRequestCode.SHARELOCATION)
    }

    override fun absolutePath(aPath: String?) {
        mCurrentPhotoPath = aPath!!
    }

    override fun createRestListener() {
        super.createRestListener()
        viewModel = ViewModelProvider(this, FocoViewModelFactory(this@RegisteFocosActivity)).get(FocoViewModel::class.java)
        viewModel!!.registerView.observe(this, androidx.lifecycle.Observer {
            hideLoading()
            if(it.error!!.not()) {
                Toast.makeText(this@RegisteFocosActivity, "Possível foco de dengue registrado com sucesso", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this@RegisteFocosActivity, it.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}