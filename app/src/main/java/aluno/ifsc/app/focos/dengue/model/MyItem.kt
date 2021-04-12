package aluno.ifsc.app.focos.dengue.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(
    row : Int,
    latLng : LatLng,
    title: String,
    snippet: String
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String
    private val row: Int

    override fun getPosition(): LatLng {
        return position
    }

    fun getRow(): Int {
        return row
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    init {
        position = latLng
        this.title = title
        this.snippet = snippet
        this.row = row
    }
}