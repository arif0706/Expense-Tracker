package com.example.expensetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.expensetracker.R
import kotlinx.android.synthetic.main.fragment_add_expense_dialog_fragment.*

class AddExpenseDialogFragment:DialogFragment() {

    companion object{
        private const val TAG="AddExpenseDialog"

        fun display(fragmentManager: FragmentManager) {
            return AddExpenseDialogFragment().show(fragmentManager, TAG)
        }
    }


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
                R.id.save -> dismiss()
            }
            true
        }

    }

    override fun onStart() {
        super.onStart()
        val dialog=dialog
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setWindowAnimations(R.style.AppTheme_Slide)
    }

}