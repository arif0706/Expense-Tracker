package com.example.expensetracker.ui.expensesGraph

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core.extentions.fragmentArgs
import com.example.core.extentions.toBundle
import com.example.expensetracker.R
import com.example.expensetracker.ui.Util
import com.example.expensetracker.ui.expenses.ExpensesActivity
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_graph.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Parcelize
data class GraphFragmentArgs(
    val date:String,
):Parcelable

class GraphFragment : Fragment() {

    private val args: GraphFragmentArgs by fragmentArgs()
    companion object{
        val TAG=GraphFragment::class.java.name

        fun newInstance(
            date:String,
        ):GraphFragment{
            return GraphFragment().apply {
                arguments=GraphFragmentArgs(
                    date,
                ).toBundle()
            }
        }
    }

    private val viewModel:GraphFragmentViewModel by viewModel(parameters = { parametersOf(args)})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        container?.removeAllViews()
        return inflater.inflate(R.layout.fragment_graph,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner){
            if(it.isLoading){
                setEmptyGraph()
                pie_chart.centerText="Loading..."
                btn_view_transaction.visibility=View.GONE
            }
            else if(it.isEmpty){
                setEmptyGraph()
                pie_chart.centerText="No transactions"
                btn_view_transaction.visibility=View.GONE
            }else {
                setGraph(it.list,it.totalAmount)
                btn_view_transaction.visibility=View.VISIBLE
            }
        }

        viewModel.viewEvents.observe(viewLifecycleOwner){
            when(it){
                is GraphFragmentViewEvents.OpenExpensesFragment -> openExpenseFragment(it.category,it.date)
            }
        }

        btn_view_transaction.setOnClickListener{
            viewModel.handle(GraphFragmentViewActions.ViewTransactionButtonIsClicked)
        }
    }

    private fun openExpenseFragment(category: String, date: String) {
       ExpensesActivity.launch(requireContext(),date,category)
    }

    private fun setEmptyGraph() {

        Util.setEmptyGraph(pie_chart,requireContext())
        setLegendAdapter(arrayListOf(), PieDataSet(listOf(),""))

    }

    private fun setGraph(list: ArrayList<PieEntry>,totalAmount:String) {


        Util.prepareGraph(list,totalAmount,pie_chart,requireContext())
        setLegendAdapter(list,Util.getPieDataSet(list,requireContext()))


    }

    private fun setLegendAdapter(list: ArrayList<PieEntry>, pieDataSet: PieDataSet) {
        rv_legends.layoutManager=GridLayoutManager(requireContext(),2)
        val adapter=LegendAdapter()

        adapter.setData(list,pieDataSet)
        rv_legends.adapter=adapter

    }
}

