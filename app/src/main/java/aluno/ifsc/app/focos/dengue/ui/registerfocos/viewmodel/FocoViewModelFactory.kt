package aluno.ifsc.app.focos.dengue.ui.registerfocos.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FocoViewModelFactory (var activity: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FocoViewModel::class.java)) {
            return FocoViewModel(
                activity = activity
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}