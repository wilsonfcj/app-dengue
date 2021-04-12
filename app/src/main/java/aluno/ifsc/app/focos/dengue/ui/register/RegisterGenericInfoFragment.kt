package aluno.ifsc.app.focos.dengue.ui.register

import aluno.ifsc.app.focos.dengue.BuildConfig
import aluno.ifsc.app.focos.dengue.R
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import aluno.ifsc.app.focos.dengue.model.user.User
import aluno.ifsc.app.focos.dengue.resources.utilidades.ActivityRequestCode
import aluno.ifsc.app.focos.dengue.resources.utilidades.EditTextMask
import aluno.ifsc.app.focos.dengue.resources.utilidades.ImageUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.StringUtil
import aluno.ifsc.app.focos.dengue.ui.login.afterTextChanged
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseFragment
import aluno.ifsc.app.focos.dengue.ui.register.viewmodel.RegisterViewModel
import aluno.ifsc.app.focos.dengue.ui.register.viewmodel.RegisterViewModelFactory
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RegisterGenericInfoFragment : BaseFragment() {

    var button : MaterialButton? = null
    var editTextName : EditText? = null
    var editTextCpf : EditText? = null
    var editTextPhone : EditText? = null
    var editTextEmail : EditText? = null
    var editTextBirthDay : EditText? = null


    var mImageViewPhotoProfile: ImageView? = null
    private lateinit var mCurrentPhotoPath: String

    var textInputName : TextInputLayout? = null
    var textInputCpf : TextInputLayout? = null
    var textInputPhone : TextInputLayout? = null
    var textInputEmail : TextInputLayout? = null
    var textInputBirthDay : TextInputLayout? = null
    var textInputEducationInstitution : TextInputLayout? = null
    var textInputUserType : TextInputLayout? = null
    var textInputRegistration : TextInputLayout? = null
    var textInputYearTick: TextInputLayout? = null

    var containerPicker : LinearLayout? = null
    var registerViewModel :  RegisterViewModel? = null

    private val calendarBirthday : Calendar = Calendar.getInstance()
    private val calendarDateOfIssue : Calendar = Calendar.getInstance()

    private var progressBar :  ProgressBar? = null

    var user : User? = null;
    var containerLayout: LinearLayout? = null
    var bitmap : Bitmap? = null
    var isRegister : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_info_generic, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ActivityRequestCode.GALLERY ->
                    data?.let {
                        bitmap = ImageUtil.gallery(activity!!, data.data)
                        bitmap?.let { mImageViewPhotoProfile?.setImageBitmap(bitmap) }
                    }
                ActivityRequestCode.CAMERA -> {
                    bitmap = ImageUtil.camera(File(mCurrentPhotoPath), mCurrentPhotoPath)
                    bitmap?.let { mImageViewPhotoProfile?.setImageBitmap(bitmap) }
                }
            }
        }
    }

    fun showDisplayEditUser() {
        user = User.UserShared.load(activity!!)
        user?.let {
            isRegister = false
            editTextCpf?.isEnabled = false
            editTextCpf?.setText(user!!.cpf)
            editTextName?.setText(user!!.name)
            editTextPhone?.setText(user!!.phone)
            editTextEmail?.setText(user!!.email)
            editTextBirthDay?.setText(StringUtil.data(user!!.birthDay!!, "dd/MM/yyyy"))
            if(user!!.imageUser!!.isNotEmpty()) {
                mImageViewPhotoProfile?.setImageBitmap(ImageUtil.convertBase64ToBitmap(user!!.imageUser!!))
            }

            enableButtonNext()
        }
    }

    override fun mapComponents() {
        mImageViewPhotoProfile = view?.findViewById(R.id.profile_image)
        editTextName = view?.findViewById(R.id.edit_text_name)
        editTextPhone = view?.findViewById(R.id.edit_text_cell_fone)
        editTextCpf = view?.findViewById(R.id.edit_text_cpf)
        editTextEmail = view?.findViewById(R.id.edit_text_email)
        editTextBirthDay = view?.findViewById(R.id.edit_text_birthday)


        button = view?.findViewById(R.id.button_cadastre)

        textInputName = view?.findViewById(R.id.text_input_name)
        textInputCpf = view?.findViewById(R.id.text_input_cpf)
        textInputPhone = view?.findViewById(R.id.text_input_cell_fone)
        textInputEmail = view?.findViewById(R.id.text_input_email)
        textInputBirthDay = view?.findViewById(R.id.text_input_birthday)
        containerPicker = view?.findViewById(R.id.container_picker)


        EditTextMask.addCpfMask(editTextCpf!!)
        EditTextMask.addTelefoneMask(editTextPhone!!)

        bitmap?.let {
            mImageViewPhotoProfile?.setImageBitmap(bitmap)
        }

        mImageViewPhotoProfile!!.setOnClickListener {
            checkPremission()
        }

        showDisplayEditUser()

        val items: MutableList<String> = ArrayList()
    }

    fun checkPremission() {
        val permissionStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionCamera = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(activity!!, permissionCamera) == PackageManager.PERMISSION_DENIED ||
            ContextCompat.checkSelfPermission(activity!!, permissionStorage) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity!!, arrayOf(permissionCamera, permissionStorage), 4)
        } else {
            //DONE: Solicitar da camera ou galeria
            var options = arrayOf("Galeria", "Câmera")
            if(bitmap != null) {
                options = arrayOf("Remover Imagem", "Galeria", "Câmera")
            }
            val builder = AlertDialog.Builder(activity!!)
            builder
                .setTitle("Selecione uma Opção")
                .setItems(options) { dialog, which ->
                    when {
                        options[which] == "Remover Imagem" -> {
                            bitmap = null
                            mImageViewPhotoProfile?.setImageResource(R.drawable.default_image_user)
                        }
                        options[which] == "Galeria" -> {
                            showGalery()
                        }
                        options[which] == "Câmera" -> {
                            showCamera()
                        }
                        else -> dialog?.dismiss()
                    }
                }
            builder.show()
        }
    }

    fun showGalery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, ActivityRequestCode.GALLERY)
    }

    fun showCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = activity!!.getExternalFilesDir("external_files")
            val image = File.createTempFile(imageFileName, ".jpg", storageDir)
            mCurrentPhotoPath = image.absolutePath
            println(image.absolutePath)
            photoFile = image
        } catch (ex: IOException) {
            println(ex.message)
        }
        if (photoFile != null) {
            val photoURI: Uri? =
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                    Uri.fromFile(photoFile)
                } else {
                    FileProvider.getUriForFile(
                        activity!!,
                        BuildConfig.APPLICATION_ID + ".fileprovider", photoFile
                    )
                }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, ActivityRequestCode.CAMERA)
        }
    }

    override fun mapActionComponents() {
        button?.setOnClickListener {
            var bundle = Bundle()
            bundle.putSerializable("user", user)
            bundle.putBoolean("register", isRegister)
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val byteArray = stream.toByteArray()
                bundle.putByteArray("bitmapImage", byteArray)
            }
            view?.findNavController()!!.navigate(R.id.action_genericFragment_to_registerStudentFragment, bundle)
        }

        editTextName?.afterTextChanged {enableButtonNext()}
        editTextCpf?.afterTextChanged {enableButtonNext()}
        editTextPhone?.afterTextChanged {enableButtonNext()}
        editTextEmail?.afterTextChanged {enableButtonNext()}
        editTextBirthDay?.afterTextChanged {enableButtonNext()}

        editTextEmail?.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                showDialogBirthDay()
            }
            false
        }
        editTextBirthDay?.setOnClickListener {
            showDialogBirthDay()
        }
    }

    fun showDialogBirthDay() {
        var calendarMaxDate = Calendar.getInstance()
        SpinnerDatePickerDialogBuilder()
            .context(activity)
            .callback { _, year, monthOfYear, dayOfMonth ->
                calendarBirthday.set(Calendar.YEAR, year)
                calendarBirthday.set(Calendar.MONTH, monthOfYear)
                calendarBirthday.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editTextBirthDay?.setText(StringUtil.data("dd/MM/yyyy", Date(calendarBirthday.timeInMillis)))
                editTextBirthDay?.clearFocus()
            }
            .spinnerTheme(R.style.DatePickerSpinner)
            .defaultDate(calendarBirthday.get(Calendar.YEAR),
                calendarBirthday.get(Calendar.MONTH),
                calendarBirthday.get(Calendar.DAY_OF_MONTH))
            .maxDate(calendarMaxDate.get(Calendar.YEAR),
                calendarMaxDate.get(Calendar.MONTH),
                calendarMaxDate.get(Calendar.DAY_OF_MONTH))
            .build()
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapComponents()
        mapActionComponents()

        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory(activity!!)).get(RegisterViewModel::class.java)
    }

    fun enableButtonNext() {
        button?.isEnabled = validateFileds()
    }

    fun validateFileds() : Boolean {
        if(validateName().not()) { return false }
        if(validateCPF().not()) { return false }
        if(validatePhone().not()) { return false }
        if(validateEmail().not()) { return false }
        if(validateBirthaday().not()) { return false }

        user = User()
        user?.name = editTextName?.text.toString()
        user?.cpf = EditTextMask.removeCpfCnpjMask(editTextCpf?.text.toString())
        user?.phone = editTextPhone?.text.toString()
        user?.email = editTextEmail?.text.toString()
        user?.birthDay = calendarBirthday.time

        return true
    }

    fun validateName() : Boolean {
        if (editTextName!!.text.toString().isValidFullName()) {
            textInputName?.helperText = null
            return true
        }
        textInputName?.helperText = null
        textInputName?.helperText = getString(R.string.error_invalid_name)
        return false
    }

    fun validateCPF() : Boolean {
        var cpf = editTextCpf?.text.toString()
        if (!EditTextMask.removeCpfCnpjMask(cpf).isCpfCnpjValid()) {
            textInputCpf?.helperText = null
            textInputCpf?.helperText = getString(R.string.error_invalid_cpf)
            return false
        }
        textInputCpf?.helperText = null
        return true
    }

    fun validatePhone() : Boolean {
        if (editTextPhone!!.text.toString().isPhoneValid()) {
            textInputPhone?.helperText = null
            return true
        }
        textInputPhone?.helperText = null
        textInputPhone?.helperText = getString(R.string.error_invalid_phone)
        return false
    }

    fun validateEmail() : Boolean {
        var email = editTextEmail?.text.toString()
         if (email.isEmailValid()) {
             textInputEmail?.helperText = null
             return true
         }
        textInputEmail?.helperText = null
        textInputEmail?.helperText = getString(R.string.error_invalid_email)
        return false
    }

    fun validateBirthaday() : Boolean {
        var birthDay = editTextBirthDay?.text.toString()
        if (birthDay.isNotEmpty()) {
            textInputBirthDay?.helperText = null
            return true
        }
        textInputBirthDay?.helperText = null
        textInputBirthDay?.helperText = getString(R.string.error_invalid_birthday)
        return false
    }
}

fun String.isCpfCnpjValid(): Boolean {
    return this.isNotBlank() && (StringUtil.isCpfValid(this) || StringUtil.isCNPJValid(this))
}

fun String.isValidFullName(): Boolean {
    val moreThanOneName = this.split("\\s+".toRegex()).size != 1
    if (moreThanOneName) {
        return this.split("\\s+".toRegex())[1].isNotEmpty()
    } else {
        return false
    }
}

fun String.isPhoneValid(): Boolean {
    val regex = "^\\([0-9]{2}\\)[0-9]{5}-[0-9]{4}\$".toRegex()
    return this.matches(regex)
}

fun String.isEmailValid(): Boolean {
    return this.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}