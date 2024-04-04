package com.fit.stepbystep.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fit.stepbystep.R
import com.fit.stepbystep.databinding.ActivityHistoryBinding
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.material.bottomappbar.BottomAppBar
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val fitnessOptions = FitnessOptions.builder()
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .build()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Set the values from shared preferences
         */
        binding.heartButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        /**
         * Set the values from shared preferences
         */
        binding.profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val currentDate = LocalDate.now()
        val dayOfMonth = currentDate.dayOfMonth
        val month = currentDate.monthValue
        val year = currentDate.year

        /**
         * Set the values from shared preferences
         */
        binding.dateTime.text = "Tarih: $dayOfMonth/$month/$year"

        /**
         * Set the values from shared preferences
         */
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                this,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this),
                fitnessOptions
            )
        } else {
            accessHeartRate()
            accessRunData()
            accessCalorieData()
            accessSleepData()
        }
    }

    /**
     * Access heart rate data
     */
    private fun accessHeartRate() {
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.time.time

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Fitness.getHistoryClient(this, it)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val entries = mutableListOf<Entry>()
                    val dataSets = response.dataSets
                    for (dataSet in dataSets) {
                        for (dataPoint in dataSet.dataPoints) {
                            val value = dataPoint.getValue(Field.FIELD_BPM).asFloat()
                            val timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS)
                            entries.add(Entry(timestamp.toFloat(), value))
                        }
                    }
                    drawLineChart(entries)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "There was a problem reading the data.", e)
                }
        }
    }

    /**
     * Access step data
     */
    private fun accessRunData() {
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.time.time

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Fitness.getHistoryClient(this, it)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val entries = mutableListOf<BarEntry>()
                    val dataSets = response.dataSets
                    for (dataSet in dataSets) {
                        for (dataPoint in dataSet.dataPoints) {
                            val value = dataPoint.getValue(Field.FIELD_STEPS).asInt()
                            val timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS)
                            entries.add(BarEntry(timestamp.toFloat(), value.toFloat()))
                            Log.d(TAG, "Data point: $value")
                        }
                    }
                    drawBarChart(entries)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "There was a problem reading the data.", e)
                }
        }
    }

    /**
     * Access calorie data
     */
    private fun accessCalorieData() {
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.time.time

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Fitness.getHistoryClient(this, it)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val entries = mutableListOf<BarEntry>()
                    val dataSets = response.dataSets
                    for (dataSet in dataSets) {
                        for (dataPoint in dataSet.dataPoints) {
                            val value = dataPoint.getValue(Field.FIELD_CALORIES).asFloat()
                            val timestamp = dataPoint.getTimestamp(TimeUnit.MILLISECONDS)
                            entries.add(BarEntry(timestamp.toFloat(), value))
                            Log.e(TAG, "Data point: $value")
                        }
                    }
                    drawCalorieChart(entries)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "There was a problem reading the data.", e)
                }
        }
    }

    /**
     *  Access sleep data
     */
    private fun accessSleepData() {
        val now = Date()
        val endTime = now.time
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val startTime = calendar.time.time

        val readRequest = DataReadRequest.Builder()
            .read(DataType.TYPE_ACTIVITY_SEGMENT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        GoogleSignIn.getLastSignedInAccount(this)?.let {
            Fitness.getHistoryClient(this, it)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val dataSets = response.dataSets
                    val entries = mutableListOf<PieEntry>()

                    for (dataSet in dataSets) {
                        for (dataPoint in dataSet.dataPoints) {
                            val startTimes = dataPoint.getStartTime(TimeUnit.MILLISECONDS)
                            val endTimes = dataPoint.getEndTime(TimeUnit.MILLISECONDS)
                            val sleepDuration = (endTimes - startTimes) / (1000 * 60 * 60).toFloat()
                            entries.add(PieEntry(sleepDuration))
                            Log.d(TAG, "Sleep start time: $startTime, Sleep end time: $endTime")
                        }
                    }
                    drawPieChart(entries)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "There was a problem reading the data.", e)
                }
        }
    }

    /**
     * Draw line chart for heart rate data
     */
    private fun drawLineChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "Kalp Atış Hızı")
        val lineData = LineData(dataSet)
        val description = binding.heartChart.description
        description.text = ""

        if(entries.isEmpty()){
            description.text = "Veri Bulunamadı"
        }else{
            description.text = "Kalp Atış Hızı (BPM)"
        }

        binding.heartChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            axisRight.isEnabled = false
            data = lineData
            invalidate()
        }
    }

    /**
     * Draw bar chart for step data
     */
    private fun drawBarChart(entries: List<BarEntry>) {
        val dataSet = BarDataSet(entries, "Adım Sayısı")
        val barData = BarData(dataSet)
        val description = binding.runChart.description
        description.text = ""
        if(entries.isEmpty()){
            description.text = "Veri Bulunamadı"
        }else {
            description.text = "Adım Sayısı"
        }

        binding.runChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            data = barData
            invalidate()
        }
    }

    /**
     * Draw bar chart for calorie data
     */
    private fun drawCalorieChart(entries: List<BarEntry>) {
        val dataSet = BarDataSet(entries, "Yakılan Kalori")
        val barData = BarData(dataSet)
        val description = binding.calorieChart.description
        description.text = ""
        if(entries.isEmpty()){
            description.text = "Veri Bulunamadı"
        }else {
            description.text = "Yakılan Kalori"
        }

        binding.calorieChart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisRight.isEnabled = false
            data = barData
            invalidate()
        }
    }

    /**
     * Draw pie chart for sleep data
     */
    private fun drawPieChart(entries: List<PieEntry>) {
        val dataSet = PieDataSet(entries, "Uyku Süresi")
        val pieData = PieData(dataSet)
        binding.sleepChart.data = pieData

        val description = binding.sleepChart.description
        if (entries.isEmpty()) {
            description.text = "Veri Bulunamadı"
        } else {
            description.text = "Uyku Süresi"
        }

        binding.sleepChart.invalidate()
    }

    /**
     * Companion object to hold constants
     */
    companion object {
        private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1
        private const val TAG = "HistoryActivity"
        private const val MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 2
    }
}