package com.example.rocketreserver

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import kotlinx.coroutines.channels.Channel
import java.text.SimpleDateFormat
import java.util.*

class LaunchListFragment : Fragment() {
    private lateinit var binding: LaunchListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LaunchListFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projects = mutableListOf<GetAllProjectsQuery.Item>()
        val adapter = LaunchListAdapter(projects)
        binding.launches.layoutManager = LinearLayoutManager(requireContext())
        binding.launches.adapter = adapter

        lifecycleScope.launchWhenResumed {
            val response = try {
                apolloClient(requireContext()).query(GetAllProjectsQuery()).execute()
            } catch (e: Exception) {
                Log.d("LaunchList", "Failure", e)
                return@launchWhenResumed
            }

            val newProjects: List<GetAllProjectsQuery.Item>? =
                response.data?.getProjects?.items?.filterNotNull()

            lifecycleScope.launchWhenResumed {
                if (newProjects != null) {
                    projects.addAll(newProjects)
                    adapter.notifyDataSetChanged()
                }
            }

        }

        val channel = Channel<Unit>(Channel.CONFLATED)

        channel.trySend(Unit)
//        adapter.onEndOfListReached = {
//            channel.trySend(Unit)
//        }
        adapter.onItemClicked = { project ->
            findNavController().navigate(
                LaunchListFragmentDirections.openLaunchDetails(project.id!!)
            )
        }

        lifecycleScope.launchWhenResumed {
            var cursor: String? = null
//            for (item in channel) {
//                val response = try {
//                    apolloClient(requireContext()).query(LaunchListQuery(Optional.Present(cursor))).execute()
//                } catch (e: Exception) {
//                    Log.d("LaunchList", "Failure", e)
//                    return@launchWhenResumed
//                }
//
//                val newLaunches = response.data?.launches?.launches?.filterNotNull()
//                if (newLaunches != null) {
//                    launches.addAll(newLaunches)
//                    adapter.notifyDataSetChanged()
//                }
//
//                cursor = response.data?.launches?.cursor
//                if (response.data?.launches?.hasMore != true) {
//                    break
//                }
//            }
//
//            adapter.onEndOfListReached = null
//            channel.close()
        }

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

        binding.fab.setOnClickListener {
            val date = Date()
            lifecycleScope.launchWhenResumed {

                val response = try {
                    apolloClient(requireContext()).mutation(
                        AddProjectMutation(
                            "David test $date",
                            Optional.Present("Mutation test from Android client"),
                            formatter.format(date)
                        )
                    ).execute()
                } catch (e: Exception) {
                    Log.e("SHITSHOW", "Error", e)
                    return@launchWhenResumed
                }

                if (response.hasErrors()) {
                    Log.e(
                        "SHITSHOW",
                        "Couldn't create project: ${response.errors?.get(0)?.message}"
                    )
                    return@launchWhenResumed
                }

                val msg = "Created project ID: ${response.data?.createProjectById?.id}"
                Log.d("SHITSHOW", msg)
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
