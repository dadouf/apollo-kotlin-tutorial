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
import com.amazonaws.util.DateUtils
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.example.rocketreserver.databinding.LaunchListFragmentBinding
import kotlinx.coroutines.channels.Channel
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

        apolloClient(requireContext()).query(GetAllProjectsQuery()).enqueue(object :
            GraphQLCall.Callback<GetAllProjectsQuery.Data>() {
            override fun onResponse(response: Response<GetAllProjectsQuery.Data>) {
                val newProjects: List<GetAllProjectsQuery.Item>? =
                    response.data()?.projects?.items()?.filterNotNull()

                lifecycleScope.launchWhenResumed {
                    if (newProjects != null) {
                        projects.addAll(newProjects)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(e: ApolloException) {
                Log.d("LaunchList", "Failure", e)
            }
        })

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

        binding.fab.setOnClickListener {
            val date = Date()
            apolloClient(requireContext()).mutate(
                AddProjectMutation(
                    "David test $date",
                    "Mutation test from Android client",
                    DateUtils.formatISO8601Date(date).substringBefore('T')
                )
            ).enqueue(object : GraphQLCall.Callback<AddProjectMutation.Data>() {
                override fun onResponse(response: Response<AddProjectMutation.Data>) {
                    if (response.hasErrors()) {
                        Log.e(
                            "SHITSHOW",
                            "Couldn't create project: ${response.errors()?.get(0)?.message()}"
                        )
                        return
                    }
                    
                    val msg = "Created project ID: ${response.data()?.createProjectById?.id}"
                    Log.d("SHITSHOW", msg)
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                }

                override fun onFailure(e: ApolloException) {
                    Log.e("SHITSHOW", "Error", e)
                }
            })
        }
    }
}
