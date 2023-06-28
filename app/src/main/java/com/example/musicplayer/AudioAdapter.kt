package com.example.musicplayer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class AudioAdapter(private val context: Context, private val audioList: List<AudioItem>) :
    BaseAdapter() {

    override fun getCount(): Int {
        return audioList.size
    }

    override fun getItem(position: Int): Any {
        return audioList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_audio, parent, false)
            holder = ViewHolder()
            holder.titleTextView = view.findViewById(R.id.titleTextView)
            holder.artistTextView = view.findViewById(R.id.artistTextView)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val audioItem = audioList[position]
        holder.titleTextView?.text = audioItem.title
        holder.artistTextView?.text = audioItem.artist

        return view!!
    }

    private class ViewHolder {
        var titleTextView: TextView? = null
        var artistTextView: TextView? = null
    }
}
