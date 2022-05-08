package com.junkfood.seal.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.junkfood.seal.BaseApplication.Companion.context
import com.junkfood.seal.R
import com.junkfood.seal.util.DownloadUtil
import com.junkfood.seal.util.FileUtil.openFile
import com.junkfood.seal.util.PreferenceUtil
import com.junkfood.seal.util.TextUtil
import com.yausername.youtubedl_android.mapper.VideoInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadViewModel : ViewModel() {

    private val _progress = MutableLiveData<Float>().apply {
        value = 0f
    }
    val isDownloading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val progress: LiveData<Float> = _progress
    val url: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val videoTitle: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val videoThumbnailUrl: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }

    fun startDownloadVideo() {
        with(url.value) {
            if (isNullOrBlank()) TextUtil.makeToast(context.getString(R.string.url_empty))
            else {
                viewModelScope.launch(Dispatchers.IO) {
                    val videoInfo: VideoInfo =
                        DownloadUtil.fetchVideoInfo(this@with) ?: return@launch
                    withContext(Dispatchers.Main) {
                        _progress.value = 0f
                        isDownloading.value = true
                        videoTitle.value = videoInfo.title
                        videoThumbnailUrl.value = videoInfo.thumbnail
                    }
                    val downloadResult = DownloadUtil.downloadVideo(this@with, videoInfo)
                    { fl: Float, _: Long, _: String -> _progress.postValue(fl) }
                    //isDownloading.postValue(false)
                    if (downloadResult.resultCode != DownloadUtil.ResultCode.EXCEPTION)
                        withContext(Dispatchers.Main) {
                            _progress.value = 100f
                            if (PreferenceUtil.getValue("open_when_finish")) openFile(downloadResult)
                        }
                }
            }
        }
    }

}