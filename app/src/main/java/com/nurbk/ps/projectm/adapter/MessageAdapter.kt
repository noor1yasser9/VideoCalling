package com.nurbk.ps.projectm.adapter

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth
import com.nurbk.ps.projectm.App.Companion.getProxy
import com.nurbk.ps.projectm.R
import com.nurbk.ps.projectm.databinding.*
import com.nurbk.ps.projectm.model.Message
import com.nurbk.ps.projectm.others.*
import com.nurbk.ps.projectm.utils.Utility.setAudioTimeMmSs


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
            item.txtMessage.text = message.text
            item.txtTime.text =
                android.text.format.DateFormat.format("hh:mm a", message.timestamp)
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
            item.txtTime.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
            item.seekBar.setOnSeekBarChangeListener(
                getSeekBarChangeListener(
                    msgAdapterListener,
                    message
                )
            )


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
    }

    inner class ImageSenderViewHolder(val item: ItemMessageRightImageBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtSenderTimeMessage.text =
                android.text.format.DateFormat.format("hh:mm a", message.timestamp)
            getImage(itemView.context, message.photoUrl, item.imageSenderMessage)
        }
    }

    inner class VideoSenderViewHolder(val item: ItemMessageRightVideoBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtTime.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
            val proxy: HttpProxyCacheServer = getProxy(item.root.context)
            val proxyUrl = proxy.getProxyUrl(message.photoUrl)
            item.videoView.start(proxyUrl)
            item.videoView.setOnClickListener {
                if (item.videoView.isPlaying)
                    item.videoView.pause()
                else
                    item.videoView.play()
            }

        }
    }

    class TextRecipientViewHolder(val item: ItemMessageLeftChatBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
            item.txtMessageEnd.text = message.text
            item.txtTimeEnd.text =
                android.text.format.DateFormat.format("hh:mm a", message.timestamp)
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
//            item.setVariable(2, item)
//            item.setVariable(2, msgAdapterListener)
            item.executePendingBindings()
            setAudioTimeMmSs(item.timeSeekBarTextView, message.audioDuration)
            item.txtTime.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)

            item.seekBar.setOnSeekBarChangeListener(
                getSeekBarChangeListener(
                    msgAdapterListener,
                    message
                )
            )

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
            item.txtRecipientTimeMessage.text =
                android.text.format.DateFormat.format("hh:mm a", message.timestamp)
            getImage(itemView.context, message.photoUrl, item.imageRecipientMessage)
        }
    }

    inner class VideoRecipientViewHolder(val item: ItemMessageLeftVideoBinding) :
        RecyclerView.ViewHolder(item.root) {
        fun onBind(message: Message) {
//            item.andExoPlayerView
//                .setSource(message.photoUrl)
            item.videoView.start(message.photoUrl);

            item.txtTime.text = android.text.format.DateFormat.format("hh:mm a", message.timestamp)
//            item.videoplayer.setUp(
//                message.photoUrl,
//                JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL,
//                message.name
//            )
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
        }
        return super.getItemViewType(position)
    }


}