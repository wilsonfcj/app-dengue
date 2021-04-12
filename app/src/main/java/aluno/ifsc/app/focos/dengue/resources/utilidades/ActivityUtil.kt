package aluno.ifsc.app.focos.dengue.resources.utilidades

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

import java.io.Serializable

class ActivityUtil private constructor(aBuilder: Builder) {

    private val mContextActivity: Context
    private val mClazz: Class<*>?
    private val mBundle: Bundle?
    private val mOnActivityResult: Boolean
    private val mRequestCode: Int

    class Builder(val mContextActivity: Context, aClazz: Class<*>) {
        var mClazz: Class<*>? = null
        var mBundle: Bundle? = null
        var mOnActivityResult: Boolean = false
        var mRequestCode: Int = 0

        private fun initBuilder(aClazz: Class<*>) {
            this.mBundle = Bundle()
            this.mClazz = aClazz
        }

        init {
            initBuilder(aClazz)
        }

        fun withBundle(aBundle: Bundle): Builder {
            mBundle = aBundle
            return this
        }

        fun putBundleInt(aKey: String, aValue: Int): Builder {
            mBundle!!.putInt(aKey, aValue)
            return this
        }

        fun putBundleLong(aKey: String, aValue: Long): Builder {
            mBundle!!.putLong(aKey, aValue)
            return this
        }

        fun putBundleString(aKey: String, aValue: String): Builder {
            mBundle!!.putString(aKey, aValue)
            return this
        }

        fun putBundleSerializable(aKey: String, aValue: Serializable): Builder {
            mBundle!!.putSerializable(aKey, aValue)
            return this
        }

        fun putBundleDouble(aKey: String, aValue: Double): Builder {
            mBundle!!.putDouble(aKey, aValue)
            return this
        }

        fun putBundleBoolean(aKey: String, aValue: Boolean): Builder {
            mBundle!!.putBoolean(aKey, aValue)
            return this
        }

        fun onActivityResult(aRequestCode: Int): Builder {
            mOnActivityResult = true
            mRequestCode = aRequestCode
            return this
        }

        fun build(): ActivityUtil {
            return ActivityUtil(this)
        }
    }

    init {
        this.mContextActivity = aBuilder.mContextActivity
        this.mBundle = aBuilder.mBundle
        this.mClazz = aBuilder.mClazz
        this.mRequestCode = aBuilder.mRequestCode
        this.mOnActivityResult = aBuilder.mOnActivityResult
        show()
    }

    private fun show() {
        val lIntent = Intent(mContextActivity, mClazz)

        lIntent.putExtras(mBundle!!)
        if (mOnActivityResult) {
            if (mContextActivity is Activity) {
                mContextActivity.startActivityForResult(lIntent, mRequestCode)
            } else {
                lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                mContextActivity.startActivity(lIntent)
            }
        }else {
            lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            mContextActivity.startActivity(lIntent)
        }
    }
}
