package com.example.expensetracker.ui.expenses

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.core.extentions.fragmentArgs
import com.example.core.extentions.toBundle
import com.example.expensetracker.R
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.full_screen_image.*


@Parcelize
data class FullImageDialogArgs(
    val url:String,
    val desc:String
):Parcelable
class FullImageDialog:DialogFragment() {

    private val args:FullImageDialogArgs by fragmentArgs()
    companion object{
        private const val TAG="FullImageDialog"

        fun display(fragmentManager: FragmentManager,url:String,desc: String){
            return FullImageDialog().apply {
                arguments=FullImageDialogArgs(url,desc).toBundle()
            }.show(fragmentManager,TAG)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.full_screen_image,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext()).load(args.url).into(iv_image)
        if(args.desc.isEmpty()){
            tv_desc.visibility=View.GONE
        }
        else{

            tv_desc.visibility=View.VISIBLE
            tv_desc.text=args.desc
        }

        toolbar.setNavigationOnClickListener{
            dismiss()
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog=dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setWindowAnimations(R.style.AppTheme_Slide)
        dialog?.window?.statusBarColor=resources.getColor(R.color.green_700)
    }

}