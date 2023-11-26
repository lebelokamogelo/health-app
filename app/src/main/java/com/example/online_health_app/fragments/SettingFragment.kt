package com.example.online_health_app.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.online_health_app.adapters.SettingsAdapter
import com.example.online_health_app.auth.LoginActivity
import com.example.online_health_app.data.Item
import com.example.online_health_app.databinding.FragmentSettingBinding
import com.example.online_health_app.screens.ChangePasswordActivity
import com.example.online_health_app.screens.EmergencyActivity
import com.example.online_health_app.screens.ProfileActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import es.dmoral.toasty.Toasty

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)

        val sharedPreferences =
            requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        binding.name.text = sharedPreferences.getString("name", "")
        binding.email.text = sharedPreferences.getString("email", "")



        binding.signOut.setOnClickListener {

            AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to Log out?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _: DialogInterface?, _: Int ->

                    Firebase.auth.signOut()
                    Toasty.success(requireContext(), "Successfully Logged out", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    activity?.finish()

                }
                .setNegativeButton("No", null)
                .show()
        }

        recyclerView = binding.RecycleView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = SettingsAdapter(
            listOf(
                Item("Edit Profile"),
                Item("Security"),
                Item("Emergency"),
            )
        )

        adapter.setOnItemClickListener(object : SettingsAdapter.OnItemClickListener{
            override fun onItemClick(item: Item) {
                when(item.text){
                    "Edit Profile" -> startActivity(Intent(context, ProfileActivity::class.java))
                    "Security" -> startActivity(Intent(context, ChangePasswordActivity::class.java))
                    "Emergency" -> startActivity(Intent(context, EmergencyActivity::class.java))
                }
            }

        })

        recyclerView.adapter = adapter


        // Inflate the layout for this fragment
        return binding.root
    }
}