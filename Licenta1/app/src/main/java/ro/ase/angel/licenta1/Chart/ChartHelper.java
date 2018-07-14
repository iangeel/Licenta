package ro.ase.angel.licenta1.Chart;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import ro.ase.angel.licenta1.R;

/**
 * Created by ANGEL on 6/18/2018.
 */

public class ChartHelper implements OnChartValueSelectedListener {

    private LineChart lineChart;

    public ChartHelper(LineChart lineChart)  {
        this.lineChart = lineChart;
        this.lineChart.setOnChartValueSelectedListener(this);

        lineChart.setNoDataText("No data for chart...");

        //permite touch
        this.lineChart.setTouchEnabled(true);

        //permite interactiune de tip scale si drag
        this.lineChart.setDragEnabled(true);
        this.lineChart.setScaleEnabled(true);
        this.lineChart.setDrawGridBackground(false);
        this.lineChart.setPinchZoom(true);

        //background alternativ
       // this.lineChart.setBackgroundColor(Color.WHITE);
        this.lineChart.setBorderColor(Color.BLACK);

        LineData lineData = new LineData();
        lineData.setValueTextColor(Color.WHITE);

        this.lineChart.setData(lineData);

        //legenda graficului
        Legend legend = this.lineChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setTypeface(Typeface.MONOSPACE);
        legend.setTextColor(Color.BLACK);

        XAxis xAxis = this.lineChart.getXAxis();
        xAxis.setTypeface(Typeface.MONOSPACE);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);

        YAxis leftAxis = this.lineChart.getAxisLeft();
        leftAxis.setTypeface(Typeface.MONOSPACE);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = this.lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    public void setChart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public void addEntry(Float value) {
        LineData lineData = this.lineChart.getData();

        if(lineData != null) {
            ILineDataSet iLineDataSet = lineData.getDataSetByIndex(0);

            if(iLineDataSet == null) {
                iLineDataSet = createNewSet();
                lineData.addDataSet(iLineDataSet);
            }

            lineData.addEntry(new Entry(iLineDataSet.getEntryCount(),value),0);
            Log.w("CHART_add_entry", iLineDataSet.getEntryForIndex(iLineDataSet.getEntryCount()-1).toString());

            lineData.notifyDataChanged();

            // anunta graficul ca valorile au fost updatate
            this.lineChart.notifyDataSetChanged();

            // limiteaza numarul de valori care pot fi vizualizate
            this.lineChart.setVisibleXRangeMaximum(10);


            // se muta la ultima valoare introdusa
            this.lineChart.moveViewTo(iLineDataSet.getEntryCount()-1, lineData.getYMax(), YAxis.AxisDependency.LEFT);


        }
    }

    private LineDataSet createNewSet() {
        LineDataSet set = new LineDataSet(null, "Pulse values");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.GREEN);
        set.setValueTextColor(Color.GREEN);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("CHART_entry", "Selected: " + e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("CHART_entry", "Fail");
    }
}
