package com.sigma.sportsup.utils

import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class SportsUpTimeDateUtils {
    companion object {
        //validate start and end time formatted alog with their period of the date.
        //First convert the string into millis and compare if the start time is less than the end time
          fun validateTime(startTime: String, endTime: String): Boolean {

            val start = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(startTime)!!
            val end = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(endTime)!!

            Log.d("TIME", "Time: $start $end")
            return end.before(start) && durationBetweenTimes(startTime, endTime) >= 30 && durationBetweenTimes(startTime, endTime) <= 120
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun validateDate(startDate: String): Boolean {
            val start = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(startDate)!!
            return start.after(Date.from(Instant.now()))
        }

        private fun formatTime(time: String): String {
            val timeArray = time.split(":")
            val hour = timeArray[0].toInt()
            val minute = timeArray[1].toInt()
            val period = if (hour >= 12) "PM" else "AM"
            val formattedHour = if (hour > 12) hour - 12 else hour

            return "${if (formattedHour.toString().length > 2) formattedHour else "0$formattedHour"}:" +
                    "${if (minute.toString().length > 2) minute else "0$minute"} $period"
        }
        fun showTimePickerDialog(context:Context, parentFragmentManager: FragmentManager, editTime: EditText, title: String): String {
          editTime.setOnClickListener {
              val periodFormat = DateFormat.is24HourFormat(context)
              val timePicker = MaterialTimePicker.Builder()
                  .setTimeFormat(if (periodFormat) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H)
                  .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                  .setHour((System.currentTimeMillis() / 1000 / 60 / 60 / 24).toInt())
                  .setTitleText("Select Event $title")
                  .build()

              timePicker.addOnPositiveButtonClickListener {
                  val selectedTime = "${timePicker.hour}:${timePicker.minute}"
                  val formattedTime = formatTime(selectedTime)
                  editTime.setText(formattedTime)
              }

              timePicker.show(parentFragmentManager, "time")
          }
            return editTime.text.toString()
        }

        fun showDatePickerDialog(context:Context, parentFragmentManager: FragmentManager, edtDate: EditText): String {

            edtDate.setOnClickListener {
                val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Date")
                    .setSelection(System.currentTimeMillis())
                    .setSelection(
                        MaterialDatePicker
                            .todayInUtcMilliseconds()
                    )
                    .build()


                datePicker.addOnPositiveButtonClickListener {
                    val selectedDate = Date(it)
                    val formattedDate =
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate)
                    edtDate.setText(formattedDate)
                }

                datePicker.showNow(parentFragmentManager, "date")
            }

            return edtDate.text.toString()
        }

        private fun durationBetweenTimes(startTime: String, endTime: String): Int {
            val start = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(startTime)!!
            val end = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(endTime)!!
            val duration = end.time - start.time
            return (duration / 1000 / 60).toInt()
        }
    }
}