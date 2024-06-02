package com.example.youtubedownloader.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.youtubedownloader.core.utils.FileUtils
import com.example.youtubedownloader.core.utils.UiUtils
import com.example.youtubedownloader.databinding.DialogVideoPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideoPlayerBottomFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogVideoPlayerBinding
    private lateinit var youtubePlayerVideo: YouTubePlayerView
    private val args: VideoPlayerBottomFragmentArgs by navArgs()
    private val videoId: String
        get() = args.videoId
            ?: throw IllegalArgumentException("Argument $VIDEO_ID required")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogVideoPlayerBinding.inflate(inflater, container, false)
        youtubePlayerVideo = binding.playerVideo
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupVideoPlayer()
    }

    private fun setupVideoPlayer() {
        //TODO ADD DEFAULT BACKGROUND
        //TODO ADD COPY LINK
        val link = FileUtils.convertToFileFormat(videoId)
        binding.bottomSheetLink.text = link
        binding.bottomSheetLink.setOnClickListener {
            UiUtils.copyYoutubeLink(link, requireContext())
        }
        viewLifecycleOwner.lifecycle.addObserver(youtubePlayerVideo)
        youtubePlayerVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0F)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        youtubePlayerVideo.release()
    }

    companion object {
        private const val VIDEO_ID = "videoId"
        const val TAG = "Video player"
    }

}