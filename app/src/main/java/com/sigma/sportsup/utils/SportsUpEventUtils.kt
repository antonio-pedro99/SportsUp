package com.sigma.sportsup.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.sigma.sportsup.data.GameEvent

class SportsUpEventUtils {
    companion object {
        fun getEventStatus(currentPlayers: Int, numberOfPlayers: Int): String {
            return if (currentPlayers == numberOfPlayers) {
                "Full"
            } else {
                "Open"
            }
        }
        ///event is valid, check if all the fields are not null and the date is not in the past and the start time is not greater than the end time
        // for each case return a different error message
        @RequiresApi(Build.VERSION_CODES.O)
        fun isEventValid(
            event:GameEvent
        ): String {
            return when {

                event.game_event_name.isNullOrEmpty() -> {
                    "Please enter a name for the event"
                }
               event.name.isNullOrEmpty() -> {
                    "Select a valid event type"
                }
               event.venue.isNullOrEmpty() -> {
                    "Please enter a venue for the event"
                }
               event.start_time.isNullOrEmpty() -> {
                    "Please enter a start time for the event"
                }
               event. end_time.isNullOrEmpty() -> {
                    "Please enter an end time for the event"
                }
               event. date.isNullOrEmpty() -> {
                    "Please enter a date for the event"
                }
               event.number_of_players.toString().isEmpty()  || event.number_of_players!! <= 0 -> {
                    "Please enter the number of players for the event"
                }
                SportsUpTimeDateUtils.validateTime(event.start_time!!, event.end_time!!) -> {
                    "The start time cannot be greater than the end time or the duration is too long"
                }
                !SportsUpTimeDateUtils.validateDate(event.date!!) -> {
                    "The date cannot be in the past"
                }
                else -> {
                    "valid"
                }
            }
        }
    }
}