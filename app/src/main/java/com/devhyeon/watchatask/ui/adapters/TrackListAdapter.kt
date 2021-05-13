package com.devhyeon.watchatask.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devhyeon.watchatask.R
import com.devhyeon.watchatask.databinding.ItemTrackBinding
import com.devhyeon.watchatask.network.itunes.data.ITunesTrack
import com.devhyeon.watchatask.utils.Status
import kotlin.properties.Delegates

/**
 * 검색 항목을 보여주기 위한 ListAdapter
 * 1. Create
 * 2. 리스트 변경에 따른 업데이트는 addItem 에서 수동으로 처리
 * 3. 클릭 이벤트는 interface 구현체에게 전달하여 외부에서 처리
 * */
class TrackListAdapter constructor(val fragment : Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var trackList: MutableList<ITunesTrack> = mutableListOf()


    /** 스크롤 감지 LiveData */
    private val _scrollState = MutableLiveData<Status<Boolean>>()
    val scrollState: LiveData<Status<Boolean>> get() = _scrollState
    fun scrollStateRun() {
        _scrollState.value = Status.Run()
    }

    private var mListener: OnToggleClickListener? = null

    fun setOnToggleClickListener(listener: OnToggleClickListener?) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemQuestionBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context), R.layout.item_track, parent, false
        )
        return TrackListViewHolder(itemQuestionBinding)
    }

    override fun getItemCount(): Int = if (trackList.isNullOrEmpty()) 0 else trackList.size

    private fun getItem(position: Int): ITunesTrack = trackList[position]


    fun addItem(item: ITunesTrack) {
        trackList.add(item)
        //add 되는 아이템만 변경해주는 것이 효율적. notifyDataSetChanged() 는 Item 하나를 교체할때도 전체를 Update 하므로 비효율적
        notifyItemChanged(itemCount)
    }
    fun addItem(list: List<ITunesTrack>) {
        trackList.addAll(list)
        notifyDataSetChanged()
    }
    fun setList(list: List<ITunesTrack>) {
        trackList.clear()
        trackList.addAll(list)
        notifyDataSetChanged()
    }
    fun clear() {
        trackList.clear()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TrackListViewHolder).onBind(getItem(position))

        //스크롤 감지
        if(position == trackList.size-1) {
            if(_scrollState.value is Status.Run ) {
                _scrollState.value = (Status.Success(true))
            }
        }
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
                mListener!!.onToggleClick(it, track)
            }
        }
    }
}
