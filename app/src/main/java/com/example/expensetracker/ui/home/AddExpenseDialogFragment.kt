package com.example.expensetracker.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.core.extentions.fragmentArgs
import com.example.core.extentions.toBundle
import com.example.expensetracker.R
import com.example.expensetracker.models.ExpenseTransaction
import com.example.expensetracker.models.Memory
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.toast
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_add_expense_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddExpenseDialogFragment:DialogFragment() {

    private val args : MainActivityArgs by fragmentArgs()
    private val viewModel: MainActivityViewModel by viewModel(parameters = { parametersOf(args)})

    companion object{
        private const val TAG="AddExpenseDialog"

        fun display(fragmentManager: FragmentManager, currentDate: String?) {

            return AddExpenseDialogFragment().apply {
                arguments= currentDate?.let { MainActivityArgs(it).toBundle() }
            }.show(fragmentManager, TAG)
        }
        private const val PICK_IMAGE_REQUEST=515
        var filePath:Uri?=null
        var imageByteArray:ByteArray?=null
    }
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_add_expense_dialog_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener{
            dismiss()
        }

        toolbar.title="Add an expenditure"
        toolbar.inflateMenu(R.menu.menu_add_expense_dialog)
        toolbar.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.save -> {
                    savedClicked()

                }
            }
            true
        }

        btn_add_image.setOnClickListener{
            viewModel.handle(MainActivityViewActions.AddImageButtonClicked)
        }
        ib_image_close.setOnClickListener{
            viewModel.handle(MainActivityViewActions.CloseImageButtonClicked)
        }


        viewModel.viewState.observe(this){
            setCategories(it.categoryList)
        }
       viewModel.viewEvents.observe(this,{
           when(it){
               is MainActivityViewEvents.ShowModifiedAmount -> changeEditText(amount = it.amount)
               is MainActivityViewEvents.ShowToast -> toast(it.message)
               is MainActivityViewEvents.OpenGalleryAndPreview -> showFileChooser()
               is MainActivityViewEvents.DismissProgressDialog -> dismissProgressDialog()
               is MainActivityViewEvents.StartProgressDialog -> startDialog()
               is MainActivityViewEvents.CloseImage -> closeImagePreview()
               is MainActivityViewEvents.SetCategories -> setCategories(it.categoryList)
           }
       })
        et_amount.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                    et_amount.removeTextChangedListener(this)
                    viewModel.handle(MainActivityViewActions.TypingAmount(amount = et_amount.text.toString()))
                    et_amount.addTextChangedListener(this)
            }
        })
    }

    @SuppressLint("ResourceType")
    private fun setCategories(categoryList: List<String>) {
        categoryList.drop(1).forEach{
            val chip=Chip(requireContext())

            chip.text=it
            chip.tag=it.hashCode()

            chip.isCheckable=true

            chip.elevation=10f


            chip_group.addView(chip)

            chip.setChipBackgroundColorResource(R.drawable.chip_bg_state)

            chip.setCheckedIconResource(R.drawable.ich_chip_check)

        }

    }

    private fun closeImagePreview() {
        filePath =null
        rl_image_preview.visibility=View.GONE
    }

    private fun startDialog() {
        progressDialog= ProgressDialog(requireContext())
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Uploading...")
        progressDialog.show()

    }


    private fun savedClicked() {
        val memory=Memory(desc = et_memories_desc.text.toString().trim(), imageBytes = imageByteArray)

        val category= view?.findViewById<Chip>(chip_group.checkedChipId)?.text?.toString()

        val expense= ExpenseTransaction(amount = Util.commaLessAmount(et_amount.text.toString()),purpose = et_purpose.text.toString().trim(),memory = memory,category = category)
        viewModel.handle(MainActivityViewActions.SaveFromDialogClicked(expense = expense))
        imageByteArray =null
    }

    private fun dismissProgressDialog() {
        toast("Saved")
        progressDialog.dismiss()
        dismiss()

    }

    private fun showFileChooser() {
        val intent=Intent()
        intent.type = "image/*"
        intent.action=Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select picture"), PICK_IMAGE_REQUEST)

    }

    private fun changeEditText(amount: String) {

        et_amount.setText(amount)
        et_amount.setSelection(amount.length)
    }

    override fun onStart() {
        super.onStart()
        val dialog=dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setWindowAnimations(R.style.AppTheme_Slide)
        dialog?.window?.statusBarColor=resources.getColor(R.color.green_700)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.data!=null){
            filePath = data.data
            try{

                val bitmap=MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)

                val baos=ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG,25,baos)

                imageByteArray =baos.toByteArray()
                val reducedBitmap=Util.reduceBitmapSize(bitmap,240000)

                filePath =Util.getBitmapFile(reducedBitmap).toUri()
                iv_preview.visibility=View.VISIBLE


                iv_preview.setImageBitmap(bitmap)
                rl_image_preview.visibility=View.VISIBLE
            }
            catch (e:IOException){
                toast("image selection failed")
            }
        }
    }

}


