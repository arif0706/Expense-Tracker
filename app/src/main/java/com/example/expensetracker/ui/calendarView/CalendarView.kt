package com.example.expensetracker.ui.calendarView

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.expensetracker.R
import com.example.expensetracker.ui.Util
import kotlinx.android.synthetic.main.calendar_view.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

 class CalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val sdfGone=SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH)
    private val sdfVisible=SimpleDateFormat("MMMM yyyy",Locale.ENGLISH)
    private val cal= Calendar.getInstance(Locale.ENGLISH)
    private val currentDate=Calendar.getInstance(Locale.ENGLISH)
    private lateinit var adapter: CalendarAdapter
    private val dates=ArrayList<Date>()
    private val calendarList=ArrayList<CalendarDateModel>()
    private var dateClickListener: DateClickListener? = null
    private var totalAmountHashMap:HashMap<String,Long> = hashMapOf()


    private var view:View
    private var adb:AlertDialog.Builder
    private var dialog: Dialog

    init {
        val inflater=context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view=inflater.inflate(R.layout.calendar_view,this)
        adb=AlertDialog.Builder(context)

        dialog=adb.setView(view).create()

        setUpAdapter()
        setUpClickListener()
    }

    fun show(){

        dialog.show()

    }
    private fun updateCalendar(){
        val calendarList = ArrayList<CalendarDateModel>()
        tv_date_month.text=sdfVisible.format(cal.time)
        val monthCalendar=cal.clone() as Calendar
        val maxDaysInMonth=cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH,1)

        while(dates.size<maxDaysInMonth){
            dates.add(monthCalendar.time)

            val totalAmount=totalAmountHashMap[Util.dateToReadable(monthCalendar.time)]
            var totalAmountInTermsOfK= totalAmount?.let { Util.getKValue(it) }
            if(totalAmountInTermsOfK==null){
                totalAmountInTermsOfK="0"
            }
            when {
                monthCalendar.time<currentDate.time -> {
                    calendarList.add(CalendarDateModel(monthCalendar.time, isPastDate = true,totalAmount = totalAmountInTermsOfK.toString()))
                }
                monthCalendar.time == currentDate.time -> {
                    calendarList.add(CalendarDateModel(monthCalendar.time, isSelected = true, isPastDate = true, isCurrentDate = true,totalAmount = totalAmountInTermsOfK.toString()))
                    recycler_view.scrollToPosition(calendarList.size-3)
                }
            }
            monthCalendar.add(Calendar.DAY_OF_MONTH,1)
        }

        this.calendarList.clear()
        this.calendarList.addAll(calendarList)
        adapter.setData(calendarList)

        tv_date_month.setOnClickListener{

            if(recycler_view.visibility==View.VISIBLE) {
               viewVisibilities(GONE)
            }
            else {
                viewVisibilities(VISIBLE)
            }
        }


    }

    private fun viewVisibilities(visibility: Int){
        recycler_view.visibility=visibility
        iv_calendar_next.visibility=visibility
        iv_calendar_previous.visibility=visibility
        if(visibility==GONE){
            tv_date_month.text=sdfGone.format(cal.time)
        }
        else{
            tv_date_month.text=sdfVisible.format(cal.time)
        }
    }

    private fun setUpAdapter(){
        val snapHelper=LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler_view)
        adapter=CalendarAdapter ( {calendarModel,position ->
            calendarList.forEachIndexed{index,calendarModel ->
                calendarModel.isSelected=index==position
                adapter.setData(calendarList)
            }
            cal.time=calendarModel.date
            tv_date_month.text=sdfVisible.format(cal.time)
            dateClickListener?.onDateClick(calendarModel.totalDate)
            dialog.dismiss()
            },{visibility   -> viewVisibilities(visibility) })
        recycler_view.layoutManager=GridLayoutManager(context,7)
        recycler_view.adapter=adapter

    }

    private fun setUpClickListener() {

        iv_calendar_next.setOnClickListener{

            if(cal.timeInMillis < currentDate.timeInMillis) {
                cal.add(Calendar.MONTH, 1)
                updateCalendar()
            }

        }

        iv_calendar_previous.setOnClickListener{
            cal.add(Calendar.MONTH,-1)
            if(cal == currentDate)
                updateCalendar()
            else
                updateCalendar()
        }

    }

    fun setTotalAmounts(map: HashMap<String, Long>){
        totalAmountHashMap=map
        updateCalendar()

    }

    fun setDateClickListener(dateClickListener: DateClickListener){
        this.dateClickListener=dateClickListener

    }


    interface DateClickListener{
        fun onDateClick(date:String)
    }

}