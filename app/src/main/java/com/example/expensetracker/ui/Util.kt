package com.example.expensetracker.ui

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.example.expensetracker.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round
import kotlin.math.sqrt

class Util {


    companion object {

        fun commaSeparateAmount(amount: String): String {

            val formatter = DecimalFormat("#,##,###")
            return try {
                formatter.format(amount.replace(",","").toLong())
            } catch (e:NumberFormatException){
                "error"
            }
        }


        fun commaLessAmount(amount: String):String{
            return amount.replace(",","")
        }


        fun getTodayDate(): String {
            val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.ENGLISH)
            return dateFormat.format(Date())
        }


        fun dateToReadable(date: Date):String {

            val dbFormat=SimpleDateFormat("ddMMyyyy",Locale.US)
            return dbFormat.format(date)
        }


        fun dateToUiFormat(date:String):String{

            val date = SimpleDateFormat("ddMMyyyy", Locale.US).parse(date)
            val uiFormat=SimpleDateFormat("dd MMMM yyyy", Locale.US)

            return uiFormat.format(date)
        }


        fun reduceBitmapSize(bitmap: Bitmap,MAX_SIZE:Int): Bitmap{

            val bitmapHeight:Int = bitmap.height
            val bitmapWidth:Int = bitmap.width
            val ratioSquare= ((bitmapHeight * bitmapWidth) / MAX_SIZE).toDouble()
            if(ratioSquare<=1)
                return bitmap
            val ratio= sqrt(ratioSquare)
            val requiredHeight= round(bitmapHeight/ratio) .toInt()
            val requiredWidth= round(bitmapWidth/ratio) .toInt()
            return Bitmap.createScaledBitmap(bitmap,requiredWidth,requiredHeight,true)

        }


        fun getBitmapFile(reducedBitmap: Bitmap):File{
            val file=File(File.separator + Environment.getExternalStorageDirectory() +"reduced_file")
            val bos=ByteArrayOutputStream()
            reducedBitmap.compress(Bitmap.CompressFormat.JPEG,0,bos)
            val bitmapData=bos.toByteArray()

            try {
                val fos= FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
                return file
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            return file
        }


        fun getKValue(number: Long): String {


            return when {
                (number/10000000)>=1 -> {
                    String.format("%.1f",(number/10000000.0)) + "Cr";
                }
                (number/100000)>=1 ->{
                    String.format("%.1f",(number/100000.0))+"L"
                }
                number/1000>=1 -> {
                    String.format("%.1f",(number/1000.0))+ "K"
                }
                else -> number.toString()
            }


        }


        fun appendRupee(string:String):String{
            return "₹ $string"
        }


        fun getPieDataSet(list:List<PieEntry>,context: Context):PieDataSet{
            val pieDataSet=PieDataSet(list,"")
            pieDataSet.setColors(

                context.resources.getColor(R.color.graph_one),
                context.resources.getColor(R.color.graph_two),
                context.resources.getColor(R.color.graph_three),
                context.resources.getColor(R.color.graph_four),
                context.resources.getColor(R.color.graph_five),
                context.resources.getColor(R.color.graph_six),
                context.resources.getColor(R.color.graph_seven),
            )
            pieDataSet.setDrawValues(false)

            return pieDataSet

        }


        fun prepareGraph(list:ArrayList<PieEntry>,totalAmount:String,pieChart: PieChart,context: Context):PieChart{


            pieChart.animateXY(1000,1000)
            val pieDataSet = getPieDataSet(list ,context)

            val pieData=PieData(pieDataSet)

            pieData.setValueTextSize(15f)

            pieData.setDrawValues(false)

            pieChart.data=pieData

            pieChart.centerText=totalAmount
            pieChart.setCenterTextSize(25f)

            pieChart.setEntryLabelTextSize(18f)
            pieChart.description.isEnabled=false

            pieChart.setDrawEntryLabels(false)

            pieChart.contentDescription=""
            pieChart.holeRadius=75f

            pieChart.legend.isEnabled=false

            return pieChart
        }


        fun setEmptyGraph(pieChart: PieChart,context: Context):PieChart{

            pieChart.animateXY(1000,1000)

            val pieDataSet=PieDataSet(arrayListOf(PieEntry(10f,"")),"")
            pieDataSet.setColors(context.resources.getColor(R.color.common_google_signin_btn_text_light_disabled))
            pieDataSet.setDrawValues(false)

            val pieData=PieData(pieDataSet)
            pieData.setDrawValues(false)


            pieChart.data=pieData
            pieChart.description.isEnabled=false
            pieChart.legend.isEnabled=false
            pieChart.holeRadius=75f
            pieChart.setCenterTextSize(20f)
            return pieChart
        }


        val categories:List<String> = listOf("All","Home","Travel","Food","Bills","Recharge","Shopping","Health")

    }



}
