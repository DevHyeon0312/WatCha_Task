package com.devhyeon.watchatask.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.databinding.ItemTrackBinding
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import kotlin.properties.Delegates

/**
 * 즐겨찾기 항목을 보여주기 위한 ListAdapter
 * 1. Create
 * 2. 리스트 변경에 따른 업데이트를 Delegates.observable 로 처리
 * 3. 클릭 이벤트는 interface 구현체에게 전달하여 외부에서 처리
 * */
class FavoriteListAdapter(val fragment : Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            //이미지
            Glide
                .with(fragment)
                .load(track.artworkUrl100)
                .into(viewDataBinding.ivTrackArt)
            //즐겨찾기 버튼 상태
            viewDataBinding.toggleStar.setOnCheckedChangeListener { buttonView, isChecked ->
                track.favorit = isChecked
            }
            //즐겨찾기 버튼 클릭
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