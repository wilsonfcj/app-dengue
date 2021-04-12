package aluno.ifsc.app.focos.dengue.ui.registerfocos

import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.resources.focos.FocoResponse
import aluno.ifsc.app.focos.dengue.resources.utilidades.ConnectionUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.KeyPrefs
import aluno.ifsc.app.focos.dengue.resources.utilidades.SharedPreferencesUtil
import aluno.ifsc.app.focos.dengue.resources.utilidades.baseview.BaseActivty
import aluno.ifsc.app.focos.dengue.resources.utilidades.components.CustomLayoutMsm
import aluno.ifsc.app.focos.dengue.ui.register.adapter.FocoAdapter
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModel
import aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel.FocoViewModelFactory
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MyFocosActivity : BaseActivty() , FocoAdapter.Listener{

    private var recyclerView: RecyclerView? = null
    private var lisAdapter : FocoAdapter? = null
    private var viewModel :  FocoViewModel? = null
    private var focoList :  MutableList<FocoResponse.Register>? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    var userId : Long? = null
    var customLayoutMsm : CustomLayoutMsm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_focos_list)
        setTitleToolbar(getString(R.string.title_my_focos))
    }

    override fun mapComponents() {
        super.mapComponents()
        recyclerView = findViewById(R.id.recycler_view)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        customLayoutMsm = findViewById(R.id.layout_error)
    }

    override fun onResume() {
        super.onResume()
        loadFocos()
    }

    fun loadFocos() {
        if(ConnectionUtil.isNetworkAvailable(this@MyFocosActivity)) {
            swipeRefreshLayout!!.isRefreshing = true
            loadFocos(userId!!)
        }
    }

    override fun mapActionComponents() {
        super.mapActionComponents()
        userId = SharedPreferencesUtil.get(this@MyFocosActivity, KeyPrefs.USER_ID, 0L)
        showDisplayList()
        swipeRefreshLayout!!.setOnRefreshListener { loadFocos(userId!!) }
    }

    fun showDisplayList() {
        focoList = ArrayList()
        lisAdapter = FocoAdapter(focoList!!, this)
        val viewManager = LinearLayoutManager(this)
        recyclerView?.layoutManager = viewManager
        recyclerView?.adapter = lisAdapter
    }
//
    override fun createRestListener() {
        viewModel = ViewModelProvider(this, FocoViewModelFactory(this@MyFocosActivity)).get(FocoViewModel::class.java)
        viewModel!!.loadAllByIdView.observe(this, androidx.lifecycle.Observer {
            swipeRefreshLayout!!.isRefreshing = false
            if(it.error!!.not()) {
                if(it.success!!.isEmpty()) {
                    customLayoutMsm?.visibility = View.VISIBLE
                } else {
                    customLayoutMsm?.visibility = View.INVISIBLE
                }
                lisAdapter?.updateList(it.success)
            } else {
                Toast.makeText(this@MyFocosActivity, it.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun loadFocos(userId: Long) {
        viewModel!!.searchAllFocos(userId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                if (ConnectionUtil.isNetworkAvailable(this@MyFocosActivity).not()) {
                    Toast.makeText(this@MyFocosActivity, getString(R.string.error_conection), Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(this@MyFocosActivity, RegisteFocosActivity::class.java)
                    startActivity(intent)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(response: FocoResponse.Register) {

    }
}