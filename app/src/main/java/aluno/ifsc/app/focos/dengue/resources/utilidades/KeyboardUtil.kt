package aluno.ifsc.app.focos.dengue.resources.utilidades

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import java.util.*

class KeyboardUtil private constructor(act: Activity, private var mCallback: SoftKeyboardToggleListener?) : ViewTreeObserver.OnGlobalLayoutListener {

    private val mRootView: View = (act.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    private var mPrevValue: Boolean? = null
    private val mScreenDensity: Float

    init {
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = act.resources.displayMetrics.density
    }

    override fun onGlobalLayout() {
        val r = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)

        val heightDiff = mRootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (mCallback != null && (mPrevValue == null || isVisible != mPrevValue)) {
            mPrevValue = isVisible
            mCallback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        mCallback = null
        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    public interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    companion object {

        private const val MAGIC_NUMBER = 200
        private val sListenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtil>()

        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)

            sListenerMap[listener] =
                KeyboardUtil(act, listener)
        }

        fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k = sListenerMap[listener]
                k?.removeListener()

                sListenerMap.remove(listener)
            }
        }

        fun removeAllKeyboardToggleListeners() {
            for (l in sListenerMap.keys)
                sListenerMap[l]?.removeListener()

            sListenerMap.clear()
        }

        fun toggleKeyboardVisibility(context: Context) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun forceShowKeyboard(aView: View) {
            val inputMethodManager = aView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        fun forceCloseKeyboard(aView: View) {
            val inputMethodManager = aView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(aView.windowToken, 0)
        }
    }
}

