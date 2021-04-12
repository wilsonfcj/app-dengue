package aluno.ifsc.app.focos.dengue.ui.main

import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.model.MyItem
import aluno.ifsc.app.focos.dengue.resources.focos.FocoResponse
import aluno.ifsc.app.focos.dengue.resources.utilidades.*
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import aluno.ifsc.app.focos.dengue.ui.registerfocos.MyFocosActivity
import aluno.ifsc.app.focos.dengue.ui.registerfocos.RegisteFocosActivity
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModel
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModelFactory
import aluno.ifsc.app.focos.dengue.ui.settings.SettingsActivity
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.judemanutd.katexview.KatexView
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.Drawer
import net.cachapa.expandablelayout.ExpandableLayout
import java.io.Serializable


class MainActivity() : BaseActivty(), OnMapReadyCallback, Serializable{

    private var imageProfile : ImageView? = null
    private var katex_text : KatexView? = null
    private lateinit var menuHeader: AccountHeader
    private var menuDrawer: Drawer? = null
    var expandLayout : ExpandableLayout? = null
    private var viewModel :  FocoViewModel? = null

    var listFocos : MutableList<FocoResponse.Register>? = null

    private lateinit var mMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<MyItem>
    val brasil = LatLng(-14.235004, -51.92528)

    var textDate : TextView? = null
    var textAddress : TextView? = null
    var textUserRegister : TextView? = null
    var textDescription : TextView? = null
    var imgLocal : ImageView? = null
    var firstLoading : Boolean? = true

    var floatingActionMenu : FloatingActionMenu? = null
    var floatingActionMenuSuporte : FloatingActionButton? = null
    var floatingActionMenuNewFoco : FloatingActionButton? = null
    var floatingActionMenuMyFocos : FloatingActionButton? = null

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        if(expandLayout!!.state == ExpandableLayout.State.EXPANDED) {
            expandLayout!!.toggle()
        } else {
            super.onBackPressed()
        }
    }

    override fun createRestListener() {
        viewModel = ViewModelProvider(this, FocoViewModelFactory(this@MainActivity)).get(FocoViewModel::class.java)
        viewModel!!.loadAllView.observe(this, androidx.lifecycle.Observer {
            hideLoading()
            if(it.error!!) {
                if(ConnectionUtil.isNetworkAvailable(this@MainActivity)) {
                    DialogUtil.showDialogGenericMessageOk(this@MainActivity, R.drawable.ic_location_on,
                        "Aviso", it.message!!,null).show()
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(brasil, 4f))
            } else {
                createClusteringIntes(it.success!!)
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_LONG).show()
            }
            firstLoading = false
        })
    }

    fun createClusteringIntes(it : MutableList<FocoResponse.Register>) {
        listFocos = it
        val builder = LatLngBounds.Builder()
        var count = 0
        for (focos in it) {
            val foco = LatLng(focos!!.latitude!!, focos!!.longitude!!)
            builder.include(foco)
            val offsetItem = MyItem(count, foco, "Endereço", focos.endereco)
            clusterManager.addItem(offsetItem)
            count++
        }
        val bounds = builder.build()
        val padding = 15
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.animateCamera(cu)

        clusterManager.setOnClusterItemClickListener {
            showDisplayInfosMarker(it!!.getRow())
            if(expandLayout!!.state == ExpandableLayout.State.COLLAPSED) {
                expandLayout!!.toggle()
            }
            true
        }
    }

    private fun showDisplayInfosMarker(position : Int) {
        val item = listFocos?.get(position)
        item?.let {
            textDate?.text = StringUtil.data(it.dataCadastro, "dd/MM/yyyy HH:mm:ss")
            textAddress?.text = it.endereco
            textUserRegister?.text = it.usuario.nome
            textDescription?.text = "OBS.: ${it.descricao}"
            if(it.imagem.isNullOrEmpty()) {
                imgLocal?.setImageResource(R.drawable.ic_baseline_image_not_supported)
            } else {
                imgLocal?.setImageResource(R.drawable.ic_baseline_image)
            }
        }
    }
    override fun mapComponents() {
        super.mapComponents()

        setDisplayHomeAs(false)
        setTitleToolbar(getString(R.string.title_toolbar_dashboard))
        imageProfile = findViewById(R.id.profile_image)
        expandLayout = findViewById(R.id.expandable_location)

        textDate = findViewById(R.id.textView_date)
        textAddress = findViewById(R.id.textView_endereco)
        textUserRegister  = findViewById(R.id.txt_name)
        textDescription  = findViewById(R.id.txt_description)
        imgLocal = findViewById(R.id.imge_has_image)
        floatingActionMenu = findViewById(R.id.menu)
        floatingActionMenuSuporte  = findViewById(R.id.menu_item_support)
        floatingActionMenuNewFoco  = findViewById(R.id.menu_item_new_foco)
        floatingActionMenuMyFocos  = findViewById(R.id.menu_item_my_focos)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(ConnectionUtil.isNetworkAvailable(this@MainActivity).not()) {
            DialogUtil.showDialogGenericMessageOk(this@MainActivity, R.drawable.ic_signal_wifi_off,
                "Aviso", "Sem conexão com a internet, tente novamente mais tarde!",
                View.OnClickListener {
                    finish()
                }).show()
        }

        setImageProfile()
    }

    fun setImageProfile() {
        var image = SharedPreferencesUtil.get(this@MainActivity, KeyPrefs.USER_PHOTO, "")
        if(image.isNotEmpty()) {
            imageProfile?.setImageBitmap(ImageUtil.convertBase64ToBitmap(image))
        }
    }

    override fun onResume() {
        super.onResume()
        if(firstLoading!!.not()!!) {
            requestFocos()
        }
    }

    override fun mapActionComponents() {
        super.mapActionComponents()
        imageProfile?.setOnClickListener { v: View? ->
            val lPerfil = Intent(this, SettingsActivity::class.java)
            val lProfile = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    imageProfile!!,
                    getString(R.string.transition_avatar)
            )
            startActivity(lPerfil, lProfile.toBundle())
        }

        expandLayout?.setOnExpansionUpdateListener { expansionFraction, state ->
            if (ExpandableLayout.State.EXPANDING == state) {
                floatingActionMenu?.hideMenu(true)
            } else if (ExpandableLayout.State.COLLAPSED == state) {
                floatingActionMenu?.showMenu(true)
            }
        }

        floatingActionMenuMyFocos?.setOnClickListener {
            floatingActionMenu?.close(true)
            val lIntent = Intent(this, MyFocosActivity::class.java)
            startActivity(lIntent)
        }

        floatingActionMenuNewFoco?.setOnClickListener {
            floatingActionMenu?.close(true)
            val intent = Intent(this@MainActivity, RegisteFocosActivity::class.java)
            startActivity(intent)
        }

        floatingActionMenuSuporte?.setOnClickListener {
            floatingActionMenu?.close(true)
            Toast.makeText(this, "Em breve", Toast.LENGTH_LONG).show()
        }
    }

    fun addMarker(long : Double, lat : Double) {
        val foco = LatLng(lat, long)
        mMap.addMarker(
            MarkerOptions().position(foco)
        )
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.moveCamera(CameraUpdateFactory.newLatLng(brasil))
        clusterManager = ClusterManager(this@MainActivity, mMap)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        requestFocos()
    }

    fun requestFocos() {
        showLoading("Buscando informações")
        mMap.clear()
        clusterManager.clearItems()
        viewModel?.searchAllFocos()
    }
}
