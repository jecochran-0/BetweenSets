package com.cs407.betweensets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ExerciseAdapter : ListAdapter<NoteSummary, ExerciseAdapter.ExerciseViewHolder>(NOTE_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val noteSummary = getItem(position)
        if (noteSummary != null) {
            holder.bind(noteSummary)
        }
    }

    class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val exerciseTitle: TextView = itemView.findViewById(R.id.exerciseName)
        private val exerciseSets: TextView = itemView.findViewById(R.id.exerciseSets)
        private val exerciseReps: TextView = itemView.findViewById(R.id.exerciseReps)
        private val exerciseDate: TextView = itemView.findViewById(R.id.exerciseDate)

        fun bind(noteSummary: NoteSummary) {
            exerciseTitle.text = noteSummary.noteTitle
            exerciseSets.text = "Sets: ${noteSummary.noteSets}"
            exerciseReps.text = "Reps: ${noteSummary.noteReps}"

            val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            exerciseDate.text = dateFormatter.format(noteSummary.lastEdited)
        }
    }

    companion object {
        private val NOTE_COMPARATOR = object : DiffUtil.ItemCallback<NoteSummary>() {
            override fun areItemsTheSame(oldItem: NoteSummary, newItem: NoteSummary): Boolean =
                oldItem.noteId == newItem.noteId

            override fun areContentsTheSame(oldItem: NoteSummary, newItem: NoteSummary): Boolean =
                oldItem == newItem
        }
    }
}

