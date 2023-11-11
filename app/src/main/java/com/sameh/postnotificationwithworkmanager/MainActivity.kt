package com.sameh.postnotificationwithworkmanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.sameh.postnotificationwithworkmanager.databinding.ActivityMainBinding
import com.sameh.postnotificationwithworkmanager.worker.NotificationWorker.Companion.MESSAGE_KEY
import com.sameh.postnotificationwithworkmanager.worker.NotificationWorker.Companion.TITLE_KEY
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPostNotificationsPermission()
        handleUiData()
    }

    private fun handleUiData() {
        val title = intent.getStringExtra(TITLE_KEY)
        val message = intent.getStringExtra(MESSAGE_KEY)

        title.toString().toLogD()
        message.toString().toLogD()

        binding.apply {
            title?.let { tvPostTitle.text = it }
            message?.let { tvPostBody.text = it }
            if (title.isNullOrEmpty() || message.isNullOrEmpty()) {
                tvAppDec.visibility = View.VISIBLE
            } else {
                tvAppDec.visibility = View.GONE
            }
        }
    }

    private fun checkPostNotificationsPermission() {
        // Sets up permissions request launcher.
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    if (!it) {
                        Snackbar.make(
                            findViewById<View>(android.R.id.content).rootView,
                            getString(R.string.please_grant_notification_permission),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}