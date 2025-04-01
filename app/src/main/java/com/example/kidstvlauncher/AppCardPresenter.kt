package com.example.kidstvlauncher

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.leanback.widget.Presenter

class AppCardPresenter : Presenter() {
    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        val context = parent?.context ?: throw IllegalStateException("Parent is null")
        val view = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val appInfo = item as? AppInfo ?: return
        Log.d("LauncherDebug", "Binding app: ${appInfo.label}") // ✅ Debug log

        val appIcon = viewHolder.view.findViewById<ImageView>(R.id.app_icon)
        val appName = viewHolder.view.findViewById<TextView>(R.id.app_name)

        appIcon.setImageDrawable(appInfo.icon)
        appName.text = appInfo.label
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        // Clean up resources if needed
    }
}