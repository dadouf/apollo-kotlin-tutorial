package com.example.rocketreserver

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rocketreserver.databinding.LaunchItemBinding

class LaunchListAdapter(
    private val projects: List<GetAllProjectsQuery.Item>
) :
    RecyclerView.Adapter<LaunchListAdapter.ViewHolder>() {

    var onEndOfListReached: (() -> Unit)? = null
    var onItemClicked: ((GetAllProjectsQuery.Item) -> Unit)? = null

    class ViewHolder(val binding: LaunchItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LaunchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.binding.missionName.text = project.name
        holder.binding.site.text = project.description
//        holder.binding.missionPatch.load(project.mission?.missionPatch) {
//            placeholder(R.drawable.ic_placeholder)
//        }

        if (position == projects.size - 1) {
            onEndOfListReached?.invoke()
        }

        holder.binding.root.setOnClickListener {
            onItemClicked?.invoke(project)
        }
    }
}