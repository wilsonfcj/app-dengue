package aluno.ifsc.app.focos.dengue.resources.utilidades

import aluno.ifsc.app.focos.dengue.R
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_dialog_ok.view.*

object DialogUtil {

    fun showDialogGenericMessage (context: Context, img : Int, titulo : String, msm : String, onClickListener : View.OnClickListener) {
        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_generic_msm, null)
        //AlertDialogBuilder
        var textViewTitle = mDialogView.findViewById<TextView>(R.id.text_title)
        var textViewMsm = mDialogView.findViewById<TextView>(R.id.text_msm)
        var imgView = mDialogView.findViewById<ImageView>(R.id.img)
        var button = mDialogView.findViewById<Button>(R.id.button_ok)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.create()
        mAlertDialog.setCancelable(false)

        if(titulo.isNotEmpty()) {
            textViewTitle.text = titulo
        } else {
            textViewTitle.visibility = View.GONE
        }

        textViewMsm.text = msm
        imgView.setImageResource(img)

        mAlertDialog.window!!.attributes.windowAnimations = R.style.StyledDialogs_DialogAnimationNormal

        button.setOnClickListener {
            onClickListener.onClick(it)
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
    }

    fun showDialogGenericMessageYesNo (context: Context, img : Int?, titulo : String, msm : String, onClickListener : View.OnClickListener?, onNoClickListener : View.OnClickListener?) : AlertDialog {
        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.dialog_generic_msm_yes_no, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.create()
        mAlertDialog.setCancelable(false)


        var textViewTitle = mDialogView.findViewById<TextView>(R.id.text_title)
        var textViewMsm = mDialogView.findViewById<TextView>(R.id.text_msm)
        var imgView = mDialogView.findViewById<ImageView>(R.id.img)
        var buttonYes = mDialogView.findViewById<Button>(R.id.button_yes)
        var buttonNo = mDialogView.findViewById<Button>(R.id.button_no)

        if(titulo.isNotEmpty()) {
            textViewTitle.text = titulo
        } else {
            textViewTitle.visibility = View.GONE
        }

        textViewTitle.text = titulo
        textViewMsm.text = msm
        if (img != null) {
            imgView.setImageResource(img)
        } else {
            imgView.visibility = View.GONE
        }
        mAlertDialog.window!!.attributes.windowAnimations = R.style.StyledDialogs_DialogAnimationNormal

        buttonYes.setOnClickListener {
            onClickListener?.onClick(it)
            mAlertDialog.dismiss()
        }

        buttonNo.setOnClickListener {
            onNoClickListener?.onClick(it)
            mAlertDialog.dismiss()
        }
        mAlertDialog.show()
        return mAlertDialog
    }



    fun showDialogGenericMessageOk (context: Context, img : Int?, titulo : String, msm : String, onYesClickListener : View.OnClickListener?) : androidx.appcompat.app.AlertDialog {
        val mDialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_dialog_ok, null)
        //AlertDialogBuilder
        val mBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.create()
        mAlertDialog.setCancelable(false)

        if(titulo.isNotEmpty()) {
            mDialogView.text_title.text = titulo
        } else {
            mDialogView.text_title.visibility = View.GONE
        }

        mDialogView.text_title.text = titulo
        mDialogView.text_msm.text = msm
        if (img != null) {
            mDialogView.img.setImageResource(img)
        } else {
            mDialogView.img.visibility = View.GONE
        }
        mAlertDialog.window!!.attributes.windowAnimations = R.style.StyledDialogs_DialogAnimationNormal

        mDialogView.button_yes.setOnClickListener {
            onYesClickListener?.onClick(it)
            mAlertDialog.dismiss()
        }

        return mAlertDialog
    }
}
