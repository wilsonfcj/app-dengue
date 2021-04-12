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

class CustomLayoutMsm : LinearLayout, MapElement {

    internal lateinit var view: View
    internal var icon : ImageView? = null
    var title : TextView? = null
    internal var container : RelativeLayout? = null


    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initViews(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initViews(context, attrs)
    }

    private fun initViews(context: Context, attrs: AttributeSet?) {
        view = LayoutInflater.from(context).inflate(R.layout.layout_msm, this)
        val parametrosLayout = context.obtainStyledAttributes(attrs, R.styleable.custommessage, 0, 0)
        try {
            val titleColor = parametrosLayout.getResourceId(R.styleable.custommessage_titleColor, R.color.textSecondary)
            val titleText = parametrosLayout.getString(R.styleable.custommessage_title)
            val srcIcon = parametrosLayout.getResourceId(R.styleable.custommessage_iconSrc, R.drawable.ic_app)
            mapComponents()
            title?.text = titleText
            title?.setTextColor(resources.getColor(titleColor))
            icon?.setImageResource(srcIcon)
        } finally {
            parametrosLayout.recycle()
        }
    }

    override fun mapActionComponents() {

    }

    fun setMsm(msm : String) {
        title?.text = msm
    }

    override fun mapComponents() {
        icon = view.findViewById<ImageView>(R.id.image_view_msm)
        title = view.findViewById<TextView>(R.id.text_view_msm)
        container = view.findViewById<RelativeLayout>(R.id.container)
    }

}
