package com.noble.activity.myandroid.models

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.support.v4.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.noble.activity.myandroid.R

import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition.INSIDE_CHART

fun Context.addEntry(flag: Int, event: Float, mChart: LineChart?) {

    if (mChart == null) return

    val data = mChart.data
    if (data != null) {
        var set: ILineDataSet? = data.getDataSetByIndex(0)
        // set.addEntry(...); // can be called as well
        if (set == null) {
            set = createSet(flag)
            data.addDataSet(set)
        }
        //data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 80) + 10f), 0);
        data.addEntry(Entry(set.entryCount.toFloat(), event), 0)
        //data.notifyDataChanged();
        // let the chart know it's data has changed
        mChart.notifyDataSetChanged()
        // limit the number of visible entries
        mChart.setVisibleXRangeMaximum(50f)
        // mChart.setVisibleYRange(30, AxisDependency.LEFT);
        // move to the latest entry
        mChart.moveViewToX(data.entryCount.toFloat())
    }
}

private fun Context.createSet(flag: Int): LineDataSet {
    val set = LineDataSet(null, "")
    set.axisDependency = YAxis.AxisDependency.LEFT
    set.lineWidth = 2f
    // set.setFillDrawable(getResources().getDrawable(R.drawable.color));
    set.isHighlightEnabled = false
    set.setDrawValues(false)
    set.setDrawCircles(false)
    set.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
    set.cubicIntensity = 0.2f

    set.setDrawFilled(true)
    if (flag == 1)
        set.fillDrawable = ContextCompat.getDrawable(this, R.drawable.network_graph_gradient)
    if (flag == 2)
        set.fillDrawable = ContextCompat.getDrawable(this, R.drawable.cpu_graph_gradient)

    if (flag == 3) {
        set.setDrawFilled(true)
        set.fillDrawable = ContextCompat.getDrawable(this, R.drawable.cpu_graph_gradient)
    }
    return set
}


fun Context.initCupGraph(mChart: LineChart, flag: Int) {
    mChart.description.isEnabled = false
    mChart.setTouchEnabled(false)
    // enable scaling and dragging
    mChart.isDragEnabled = false
    mChart.animateY(3000, Easing.EasingOption.EaseInCubic)
    mChart.animateX(3000, Easing.EasingOption.EaseInCubic)
    // if disabled, scaling can be done on x- and y-axis separately
    mChart.isAutoScaleMinMaxEnabled = false
    mChart.setScaleEnabled(false)
    mChart.setDrawGridBackground(false)
    mChart.setPinchZoom(false)
    // set an alternative background color
    mChart.setBackgroundColor(Color.TRANSPARENT)
    mChart.setViewPortOffsets(10f, 15f, 10f, 15f)
    val data = LineData()
    data.setValueTextColor(Color.WHITE)
    mChart.data = data
    // get the legend (only possible after setting data)
    val l = mChart.legend
    // modify the legend ...
    l.form = Legend.LegendForm.NONE
    l.textColor = Color.WHITE
    val xl = mChart.xAxis
    xl.position = XAxis.XAxisPosition.BOTTOM
    xl.textColor = Color.BLACK
    xl.setDrawGridLines(true)
    xl.setAvoidFirstLastClipping(true)
    xl.isEnabled = false
    val rightAxis = mChart.axisLeft
    val leftAxis = mChart.axisRight
    if (flag == 3) {
        leftAxis.textColor = this.resources.getColor(R.color.graph_left_axis_text_color)
        leftAxis.gridColor = Color.parseColor("#77BCBDBE")
    } else if (flag == 4) {
        leftAxis.textColor = Color.parseColor("#78000000")
        leftAxis.gridColor = Color.parseColor("#77DEECF7")
    } else {
        leftAxis.textColor = Color.parseColor("#c9f0f1f1")
        leftAxis.gridColor = Color.parseColor("#77DEECF7")
    }
    leftAxis.axisLineColor = Color.TRANSPARENT
    leftAxis.setDrawGridLines(true)
    leftAxis.textSize = 10f
    rightAxis.axisMinimum = 0f
    leftAxis.axisMinimum = 0f
    if (flag == 2 || flag == 3) {
        leftAxis.axisMaximum = 100f
        rightAxis.axisMaximum = 100f
    }
    leftAxis.setLabelCount(3, true)
    leftAxis.granularity = 1f
    leftAxis.setPosition(INSIDE_CHART)
    if (flag == 1 || flag == 4) {
        leftAxis.valueFormatter = MyYAxisValueFormatter()
    } else {
        leftAxis.valueFormatter = MyYAxisValueFormatterCpu()
    }
    rightAxis.isEnabled = true
    rightAxis.textColor = Color.WHITE
    rightAxis.axisLineColor = Color.TRANSPARENT
    rightAxis.gridColor = this.resources.getColor(R.color.white_trans)
    rightAxis.setLabelCount(3, true)
    mChart.axisLeft.setDrawGridLines(false)
    mChart.xAxis.setDrawGridLines(false)
    mChart.setDrawBorders(false)
}

class MyYAxisValueFormatter internal constructor() : IAxisValueFormatter {

    @SuppressLint("DefaultLocale")
    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return if (value < 1023)
            value.toInt().toString() + "B/s"
        else if (value < 1048575)
            (value / 1024).toInt().toString() + "KB/s"
        else if (value < 1048575 * 1024)
//(float) is redundant but don't remove it
            String.format("%.1f", value / 1048576) + "MB/s"
        else
            2.toString() + "B/s"
    }
}

class MyYAxisValueFormatterCpu internal constructor() : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        return value.toInt().toString() + "%"
    }
}

fun Context.setupGradient(mChart: LineChart?, color1: Int, color2: Int) {
    if (mChart == null) return

    val paint = mChart.renderer.paintRender
    val height = mChart.width
    val linGrad = LinearGradient(
        0f, 0f, height.toFloat(), 0f,
        ContextCompat.getColor(this, color1),
        ContextCompat.getColor(this, color2),
        Shader.TileMode.REPEAT
    )
    paint.shader = linGrad
}