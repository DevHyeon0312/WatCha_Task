package com.devhyeon.watchatask.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.databinding.ItemTrackBinding
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import kotlin.properties.Delegates

class TrackListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mPostList: List<ITunesTrack> by Delegates.observable(emptyList()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemQuestionBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), R.layout.item_track, parent, false
        )
        return TrackListViewHolder(itemQuestionBinding)
    }

    override fun getItemCount(): Int = if (mPostList.isNullOrEmpty()) 0 else mPostList.size

    private fun getItem(position: Int): ITunesTrack = mPostList[position]

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TrackListViewHolder).onBind(getItem(position))
    }

    private inner class TrackListViewHolder(private val viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun onBind(track: ITunesTrack) {
            (viewDataBinding as ItemTrackBinding).track = track
            viewDataBinding.toggleStar.setOnCheckedChangeListener { buttonView, isChecked -> track.favorit = isChecked }
        }
    }
}
