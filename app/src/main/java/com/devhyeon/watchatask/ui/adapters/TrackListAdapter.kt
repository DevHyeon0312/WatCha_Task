package com.devhyeon.watchatask.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.databinding.ItemTrackBinding
import com.devhyeon.watchatask.db.FavoriteViewModel
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import kotlin.properties.Delegates

class TrackListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var mPostList: MutableList<ITunesTrack> = mutableListOf()

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

    fun addItem(list: List<ITunesTrack>) {
        mPostList.clear()
        mPostList.addAll(list)
        notifyDataSetChanged()
    }

    private inner class TrackListViewHolder(private val viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun onBind(track: ITunesTrack) {
            (viewDataBinding as ItemTrackBinding).track = track
            viewDataBinding.toggleStar.setOnCheckedChangeListener { buttonView, isChecked ->
                track.favorit = isChecked
            }
            viewDataBinding.toggleStar.setOnClickListener {
                mListener!!.onItemClick(it, track)
            }
        }
    }

    private var mListener: OnItemClickListener? = null
    interface OnItemClickListener {
        fun onItemClick(v: View?, track: ITunesTrack)
    }
    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mListener = listener
    }
}
