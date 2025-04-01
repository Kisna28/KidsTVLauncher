package com.example.kidstvlauncher

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
class HomeFragment : BrowseSupportFragment(), OnItemViewClickedListener {
    private lateinit var rowsAdapter: ArrayObjectAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        setupUI()
        loadApps()
        onItemViewClickedListener = this

    }


    private fun setupUI() {
        title = "Kids TV Launcher"
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false


        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        adapter = rowsAdapter
    }

    private fun loadApps() {
        val packageManager = requireActivity().packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        Log.d("LauncherDebug", "$installedApps")

        val approvedApps = listOf(
            "com.google.android.youtube.tv",
            "com.android.vending",
            "com.google.android.play.games"
        )

        val filteredApps = installedApps.filter {
            it.packageName in approvedApps
        }.map { AppInfo(it, packageManager) }

        if (filteredApps.isEmpty()) {
            Log.e("LauncherDebug", "No apps found for the adapter!")
            return
        }

        val listRowAdapter = ArrayObjectAdapter(AppCardPresenter())
        filteredApps.forEach { app -> listRowAdapter.add(app) }

        val header = HeaderItem(0, "Kids Apps")
        rowsAdapter.add(ListRow(header, listRowAdapter))
    }

     @SuppressLint("ResourceType")
     fun showPinDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_pin, null)
        val pinInput = dialogView.findViewById<EditText>(R.id.pinInput)

        val builder = AlertDialog.Builder(requireContext()).apply {
            setView(dialogView)
            setCancelable(false) // Prevent dialog from closing without PIN
        }

        val dialog = builder.create()
        dialog.show()

        dialogView.findViewById<Button>(R.id.btnOk).setOnClickListener {
            val enteredPin = pinInput.text.toString()
            val savedPin = sharedPreferences.getString("PIN", "1234") // Default PIN

            if (enteredPin == savedPin) {
                requireActivity().finish() // Close the app if PIN is correct
            } else {
                Toast.makeText(requireContext(), "Incorrect PIN", Toast.LENGTH_SHORT).show()
                pinInput.text.clear() // Clear input on incorrect PIN
            }
        }

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss() // Close dialog on Cancel
        }
    }


    override fun onItemClicked(
        itemViewHolder: Presenter.ViewHolder?,
        item: Any?,
        rowViewHolder: RowPresenter.ViewHolder?,
        row: Row?
    ) {
        if (item is AppInfo) {
            launchApp(item.packageName)
        }
    }

    private fun launchApp(packageName: String) {
        val launchIntent = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(launchIntent)
        } else {
            Log.e("LauncherDebug", "Unable to launch app: $packageName")
            Toast.makeText(requireContext(), "Unable to launch app", Toast.LENGTH_SHORT).show()
        }
    }
}
