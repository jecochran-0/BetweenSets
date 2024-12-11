package com.cs407.betweensets

import android.graphics.Color
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

    private val selectedExercises = mutableSetOf<NoteSummary>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise, parent, false)
        return ExerciseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val noteSummary = getItem(position)
        if (noteSummary != null) {
            holder.bind(noteSummary)

            // Change the background color based on selection
            holder.itemView.setBackgroundColor(
                if (selectedExercises.contains(noteSummary)) Color.GRAY else Color.WHITE
            )

            holder.itemView.setOnClickListener {
                if (selectedExercises.contains(noteSummary)) {
                    selectedExercises.remove(noteSummary)
                } else {
                    selectedExercises.add(noteSummary)
                }
                // Refresh the view to show the updated selection state
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    fun getSelectedExercises(): List<NoteSummary> {
        return selectedExercises.toList()
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

