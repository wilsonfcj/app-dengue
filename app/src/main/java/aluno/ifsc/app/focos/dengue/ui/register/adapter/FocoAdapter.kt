package aluno.ifsc.app.focos.dengue.ui.register.adapter

import aluno.ifsc.app.focos.dengue.R
import aluno.ifsc.app.focos.dengue.resources.focos.FocoResponse
import aluno.ifsc.app.focos.dengue.resources.utilidades.StringUtil
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item_foco.view.*

class FocoAdapter (private val dataset: MutableList<FocoResponse.Register>, private val aListener : Listener)  : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lView = LayoutInflater.from(parent.context) .inflate(R.layout.list_item_foco, parent, false)
        return ViewHolder(lView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataset[position]
        holder as ViewHolder
        holder.txtAddrsse.text = item.endereco
        holder.txtDate.text = StringUtil.data(item.dataCadastro!!, "dd/MM/yyyy")

        if(item.imagem.isNullOrEmpty()) {
            holder.imgeView.setImageResource(R.drawable.ic_baseline_image_not_supported)
        } else {
            holder.imgeView.setImageResource(R.drawable.ic_baseline_image)
        }

        with(holder.itemView) {
            tag = item
            setOnClickListener {
                aListener.onItemClick(item)
            }
        }
    }

    fun updateList(datasetUpdate: MutableList<FocoResponse.Register>) {
        dataset.clear()
        dataset.addAll(datasetUpdate)
        notifyDataSetChanged()
    }

    fun updateList(response: FocoResponse.Register) {
        dataset.add(response)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    interface Listener {
        fun onItemClick(response: FocoResponse.Register)
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txtAddrsse : TextView = itemView.textView_endereco
        val txtDate : TextView = itemView.textView_date
        var imgeView : ImageView = itemView.imge_has_image
    }

}