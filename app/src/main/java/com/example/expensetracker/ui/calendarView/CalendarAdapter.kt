package com.example.expensetracker.ui.calendarView

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.R
import java.lang.RuntimeException

class CalendarAdapter(private val listener:(calendarDateViewModel:CalendarDateModel,position:Int)->Unit,
                      private val visibilityCallBack:(visibility:Int)->Unit):
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val list=ArrayList<CalendarDateModel>()

    companion object{
        private const val TYPE_ITEM=1
        private const val TYPE_FOOTER=0
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        if(viewType== TYPE_ITEM){
            val view=
                LayoutInflater.from(parent.context).inflate(R.layout.row_calendar_item,null,false)
            val lp=RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT)
            view.layoutParams=lp
            return MyViewHolder(view)
        }
        else if(viewType== TYPE_FOOTER){
            val view=LayoutInflater.from(parent.context).inflate(R.layout.calendar_last_item,null,false)
            return LastItemViewHolder(view)

        }
        throw RuntimeException("There is no Type that matches $viewType ")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(getItemViewType(position)){
            TYPE_ITEM -> (holder as MyViewHolder).bind(list[position])
            TYPE_FOOTER -> (holder as LastItemViewHolder).bind()
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }

    override fun getItemViewType(position: Int): Int {
        return if(position == list.size && position > 7){
            TYPE_FOOTER
        }else
            TYPE_ITEM
    }

    inner class LastItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bind(){
            itemView.setOnClickListener{
                visibilityCallBack(View.GONE)
            }
        }
    }
    inner class MyViewHolder(view: View):RecyclerView.ViewHolder(view){

        fun bind(calendarDateViewModel: CalendarDateModel){
            val calendarDay=itemView.findViewById<TextView>(R.id.tv_calendar_day)
            val calendarDate=itemView.findViewById<TextView>(R.id.tv_calendar_date)
            val cardView=itemView.findViewById<CardView>(R.id.card_calendar)
            val totalAmount=itemView.findViewById<TextView>(R.id.tv_total_amount)



            if(calendarDateViewModel.isSelected){
                calendarDate.setTextColor(ContextCompat.getColor(itemView.context,R.color.white))

                calendarDay.setTextColor(ContextCompat.getColor(itemView.context,R.color.white))

                cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context,R.color.green_500))
            }
            else{
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )

                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                if(!calendarDateViewModel.isPastDate) {
                    cardView.setCardBackgroundColor(Color.GRAY)
                    cardView.isEnabled=false
                }
                else {
                    cardView.setCardBackgroundColor(Color.WHITE)
                    cardView.isEnabled=true
                }

            }

            calendarDay.text = calendarDateViewModel.calendarDay
            calendarDate.text = calendarDateViewModel.calendarDate
            totalAmount.text=calendarDateViewModel.totalAmount

            cardView.setOnClickListener {
                listener.invoke(calendarDateViewModel, adapterPosition)
            }

        }



    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(calendarList: ArrayList<CalendarDateModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}