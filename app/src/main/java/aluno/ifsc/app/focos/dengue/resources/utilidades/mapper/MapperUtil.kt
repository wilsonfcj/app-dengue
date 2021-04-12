package aluno.ifsc.app.focos.dengue.resources.utilidades.mapper

import java.util.*

abstract class MapperUtil<From, To> {
    protected abstract fun transform(aObject: From?): To

    fun transform(aFromList: MutableList<From>): MutableList<To>? {
        val lList: MutableList<To> = ArrayList()
        for (lFrom in aFromList) {
            lList.add(transform(lFrom))
        }
        return lList
    }

}