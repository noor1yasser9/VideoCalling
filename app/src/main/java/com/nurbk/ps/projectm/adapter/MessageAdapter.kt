package com.nurbk.ps.projectm.adapter

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import com.facebook.share.internal.ShareConstants.VIDEO_URL
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.App.Companion.getProxy
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.*
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.Utility.setAudioTimeMmSs
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard


class MessageAdapter(
    private val msgAdapterListener: MsgAdapterListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun getImage(context: Context, text: String, imageView: ImageView) {
        Glide.with(context)
            .load(text)
            .placeholder(R.drawable.ic_image_bg)
            .into(imageView)
    }

    inner class TextSenderViewHolder(val item: ItemMessageRightChatBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            getReaction(item.root, item.root.context)
            item.txtMessage.text = message.text
            item.txtTime.text =
                message.getTime()
        }
    }


    inner class AudioSenderViewHolder(val item: ItemMessageRightRecordBinding) :
        RecyclerView.ViewHolder(item.root) {

        fun onBind(message: Message, msgAdapterListener: MsgAdapterListener) {
            item.message = message
            item.playButton.setOnClickListener {
                msgAdapterListener.onAudioClick(item.playButton, message)
            }
            item.setVariable(1, item)
            item.setVariable(1, msgAdapterListener)
            item.executePendingBindings()
            item.nameTextView.text = message.name
            setAudioTimeMmSs(item.timeSeekBarTextView, message.audioDuration)
            item.txtTime.text = message.getTime()
            item.seekBar.setOnSeekBarChangeListener(
                getSeekBarChangeListener(
                    msgAdapterListener,
                    message
                )
            )
            getReaction(item.root, item.root.context)

        }

        private fun getSeekBarChangeListener(
            msgAdapterListener: MsgAdapterListener,
            item: Message
        ): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    msgAdapterListener.onStartTrackingTouch(seekBar, item)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    msgAdapterListener.onStopTrackingTouch(seekBar, item)
                }
            }
        }

    }

    interface MsgAdapterListener {
        fun onAudioClick(view: View, message: Message)
        fun onStartTrackingTouch(seekBar: SeekBar, item: Message)
        fun onStopTrackingTouch(seekBar: SeekBar, item: Message)
        fun showPic(view: View, message: Message)
        fun showPdf(message: Message)
    }

    inner class ImageSenderViewHolder(val item: ItemMessageRightImageBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtSenderTimeMessage.text =
                message.getTime()
            getImage(itemView.context, message.photoUrl, item.imageSenderMessage)
            getReaction(item.root, item.root.context)
        }
    }

    inner class VideoSenderViewHolder(val item: ItemMessageRightVideoBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtTime.text = message.getTime()
            val proxy: HttpProxyCacheServer = getProxy(item.root.context)
            val proxyUrl = proxy.getProxyUrl(message.photoUrl)
            item.videoplayer.setUp(
                proxyUrl,
                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                message.name
            )
            getReaction(item.root, item.root.context)
        }
    }

    inner class MapSenderViewHolder(val item: ItemMessageRightLocationBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            val location = message.text.split(",")
            mapView(
                item.mapView, item.root.context,
                location[0].toDouble(),
                location[1].toDouble(),
                ""
            )
            item.mapView.onResume()
            getReaction(item.root, item.root.context)
        }
    }

    inner class FileSenderViewHolder(val item: ItemMessageRightFileBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.message = message
            item.txtTime.text = message.getTime()
            item.root.setOnClickListener {
                msgAdapterListener.showPdf(message)
            }
            getReaction(item.root, item.root.context)
        }
    }

    inner class TextRecipientViewHolder(val item: ItemMessageLeftChatBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtMessageEnd.text = message.text
            item.txtTimeEnd.text =
                message.getTime()
            getReaction(item.root, item.root.context)
        }
    }

    inner class AudioRecipientViewHolder(val item: ItemMessageLeftRecordBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.message = message
            item.playButton.setOnClickListener {
                msgAdapterListener.onAudioClick(item.playButton, message)
            }
            item.nameTextView.text = message.name
            item.executePendingBindings()
            setAudioTimeMmSs(item.timeSeekBarTextView, message.audioDuration)
            item.txtTime.text = message.getTime()

            item.seekBar.setOnSeekBarChangeListener(
                getSeekBarChangeListener(
                    msgAdapterListener,
                    message
                )
            )
            getReaction(item.root, item.root.context)
        }


        private fun getSeekBarChangeListener(
            msgAdapterListener: MsgAdapterListener,
            item: Message
        ): SeekBar.OnSeekBarChangeListener {
            return object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                    msgAdapterListener.onStartTrackingTouch(seekBar, item)
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    msgAdapterListener.onStopTrackingTouch(seekBar, item)
                }
            }
        }
    }

    inner class ImageRecipientViewHolder(val item: ItemMessageLeftImageBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtRecipientTimeMessage.text = message.getTime()
            getImage(itemView.context, message.photoUrl, item.imageRecipientMessage)
            getReaction(item.root, item.root.context)
        }
    }

    inner class VideoRecipientViewHolder(val item: ItemMessageLeftVideoBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            val proxy: HttpProxyCacheServer = getProxy(item.root.context)
            val proxyUrl = proxy.getProxyUrl(message.photoUrl)

            item.videoplayer.setUp(
                proxyUrl,
                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
                message.name
            )
            getReaction(item.root, item.root.context)
        }
    }

    inner class MapRecipientViewHolder(val item: ItemMessageLeftLocationBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            val location = message.text.split(",")
            mapView(
                item.mapView, item.root.context,
                location[0].toDouble(),
                location[1].toDouble(),
                ""
            )

            getReaction(item.root, item.root.context)
            item.mapView.onResume()
        }
    }

    inner class FileRecipientViewHolder(val item: ItemMessageLeftFileBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.message = message
            item.txtTime.text = message.getTime()
            item.root.setOnClickListener {
                msgAdapterListener.showPdf(message)
            }
            getReaction(item.root, item.root.context)
        }
    }

    var dataList = ArrayList<Message>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TEXT_SENDER -> {
                return TextSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_chat, parent, false
                    )

                )
            }
            IMAGE_SENDER -> {
                return ImageSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_image, parent, false
                    )
                )
            }
            AUDIO_SENDER -> {
                return AudioSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_record, parent, false
                    )
                )
            }
            VIDEO_SENDER -> {
                return VideoSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_video, parent, false
                    )
                )
            }

            MAP_SENDER -> {
                return MapSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_location, parent, false
                    )
                )
            }
            FILE_SENDER -> {
                return FileSenderViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_right_file, parent, false
                    )
                )
            }
            TEXT_RECIPIENT -> {
                return TextRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_chat, parent, false
                    )
                )
            }
            IMAGE_RECIPIENT -> {
                return ImageRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_image, parent, false
                    )
                )
            }
            AUDIO_RECIPIENT -> {
                return AudioRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_record, parent, false
                    )
                )
            }
            VIDEO_RECIPIENT -> {
                return VideoRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_video, parent, false
                    )
                )
            }
            MAP_RECIPIENT -> {
                return MapRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_location, parent, false
                    )
                )
            }
            FILE_RECIPIENT -> {
                return FileRecipientViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_message_left_file, parent, false
                    )
                )
            }
            else -> return TextSenderViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_message_right_chat,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val contact = dataList[position]
        when (getItemViewType(position)) {
            TEXT_SENDER -> {
                (holder as TextSenderViewHolder).onBind(contact)
            }
            IMAGE_SENDER -> {
                (holder as ImageSenderViewHolder).onBind(contact)

            }
            AUDIO_SENDER -> {
                (holder as AudioSenderViewHolder).onBind(contact, msgAdapterListener)
            }
            VIDEO_SENDER -> {
                (holder as VideoSenderViewHolder).onBind(contact)
            }
            MAP_SENDER -> {
                (holder as MapSenderViewHolder).onBind(contact)
            }
            FILE_SENDER -> {
                (holder as FileSenderViewHolder).onBind(contact)
            }
            TEXT_RECIPIENT -> {
                (holder as TextRecipientViewHolder).onBind(contact)

            }
            IMAGE_RECIPIENT -> {
                (holder as ImageRecipientViewHolder).onBind(contact)

            }
            AUDIO_RECIPIENT -> {
                (holder as AudioRecipientViewHolder).onBind(contact)

            }
            VIDEO_RECIPIENT -> {
                (holder as VideoRecipientViewHolder).onBind(contact)
            }
            MAP_RECIPIENT -> {
                (holder as MapRecipientViewHolder).onBind(contact)
            }
            FILE_RECIPIENT -> {
                (holder as FileRecipientViewHolder).onBind(contact)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = dataList[position]
        val auth = FirebaseAuth.getInstance()
        when (model.type) {
            TYPE_MESSAGE_TEXT -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    TEXT_SENDER
                } else {
                    TEXT_RECIPIENT
                }
            }
            TYPE_MESSAGE_RECORD -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    AUDIO_SENDER
                } else {
                    AUDIO_RECIPIENT
                }
            }
            TYPE_MESSAGE_IMAGE -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    IMAGE_SENDER
                } else {
                    IMAGE_RECIPIENT
                }
            }
            TYPE_MESSAGE_VIDEO -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    VIDEO_SENDER
                } else {
                    VIDEO_RECIPIENT
                }
            }
            TYPE_MESSAGE_MAP -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    MAP_SENDER
                } else {
                    MAP_RECIPIENT
                }
            }
            TYPE_MESSAGE_FILE -> {
                return if (model.senderId == auth.currentUser?.uid) {
                    FILE_SENDER
                } else {
                    FILE_RECIPIENT
                }
            }
        }
        return super.getItemViewType(position)
    }

    private var mMap: GoogleMap? = null

    private fun mapView(
        mapView: MapView, context: Context,
        lat: Double,
        lan: Double,
        name: String
    ) {
        mapView.apply {
            onCreate(null)
            getMapAsync { googleMap ->
                mMap = googleMap

                MapsInitializer.initialize(context)
                googleMap.uiSettings.isMapToolbarEnabled = false
                try {

                    stateTheme(context, googleMap)
                } catch (e: Resources.NotFoundException) {
                    e.printStackTrace()
                }
                val latLng = LatLng(lat, lan)
                mMap!!.addMarker(MarkerOptions().position(latLng).title(name))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                mMap!!.uiSettings.setAllGesturesEnabled(true)
                mMap!!.uiSettings.isZoomGesturesEnabled = true
                mMap!!.isTrafficEnabled = true
            }
        }
    }


    fun getReaction(view: View, context: Context) {
        val config = reactionConfig(context) {
            reactions {
                resId { R.drawable.ic_like }
                resId { R.drawable.ic_heart }
                resId { R.drawable.ic_lol }
                reaction { R.drawable.ic_like scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_heart scale ImageView.ScaleType.FIT_XY }
                reaction { R.drawable.ic_lol scale ImageView.ScaleType.FIT_XY }
            }
        }
        val popup = ReactionPopup(context, config) { position ->
            true.also {
                // position = -1 if no selection
            }
        }
        view.setOnTouchListener(popup)
    }

}