package aluno.ifsc.app.focos.dengue.resources.utilidades.components

import aluno.ifsc.app.focos.dengue.R
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ImageView
import android.widget.TextView
import aluno.ifsc.app.focos.dengue.resources.utilidades.MapElement

class CustomItemUser : LinearLayout, MapElement {

    internal lateinit var view: View
    internal var icon : ImageView? = null
    internal var title : TextView? = null
    internal var label : TextView? = null
    internal var container : RelativeLayout? = null


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    private fun initViews(context: Context, attrs: AttributeSet?) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_option_my_cadastre, this)
        val parametrosLayout = context.obtainStyledAttributes(attrs, R.styleable.customitenuser, 0, 0)
        try {
            mapComponents()
            val titleText = parametrosLayout.getString(R.styleable.customitenuser_nameItem)
            val labelText = parametrosLayout.getString(R.styleable.customitenuser_labelItem)
            val srcIcon = parametrosLayout.getResourceId(R.styleable.customitenuser_iconSrcItem, R.drawable.ic_app)
            title?.text = titleText
            label?.text = labelText
            icon?.setImageResource(srcIcon)
        } finally {
            parametrosLayout.recycle()
        }
    }

    override fun mapActionComponents() {

    }

    override fun mapComponents() {
        icon = view.findViewById(R.id.image_view)
        title = view.findViewById(R.id.text_view)
        label = view.findViewById(R.id.text_view_label)
        container = view.findViewById(R.id.container)
    }

    fun setTitle(valueRes : Int) {
        title?.text = resources.getString(valueRes)
    }

    fun setTitle(value : String) {
        title?.text = value
    }

    fun setLabel(valueRes : Int) {
        label?.text = resources.getString(valueRes)
    }

    fun setLabel(value : String) {
        label?.text = value
    }

    fun setIcon(value : Int) {
        icon?.setImageResource(value)
    }
}
