package com.moare.android.features.userprofile.display.store

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.geometry.Offset
import com.moare.android.core.store.BaseStore
import com.moare.android.core.util.AWSUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

sealed interface UserProfileImageEditAction {
    data object GoBack : UserProfileImageEditAction
    data class Complete(
        val context: Context,
        val scale: Float,
        val offset: Offset,
        val cropSizePx: Int
    ) : UserProfileImageEditAction
}

sealed interface UserProfileImageEditDelegate {
    data class Pop(val key: String? = null, val file: File? = null) : UserProfileImageEditDelegate
}

class UserProfileImageEditStore @AssistedInject constructor(
    @Assisted val uri: Uri,
    @Assisted private val userId: String,
    @Assisted val emitToParent: (UserProfileImageEditDelegate) -> Unit
) : BaseStore<UserProfileImageEditAction>() {


    @AssistedFactory
    interface Factory {
        fun create(
            uri: Uri,
            userId: String,
            emitToParent: (UserProfileImageEditDelegate) -> Unit
        ) : UserProfileImageEditStore
    }

    override fun send(action: UserProfileImageEditAction) {
        when (action) {
            is UserProfileImageEditAction.GoBack -> goBack()
            is UserProfileImageEditAction.Complete -> complete(action.context, action.scale, action.offset, action.cropSizePx)
        }
    }

    private fun goBack() {

    }

    private fun complete(
        context: Context,
        scale: Float,
        offset: Offset,
        cropSizePx: Int
    ) {
        // 1) 원본 bitmap 로딩
        val originalBitmap = loadBitmapFromUri(context)

        // 2) crop rect 계산
        val rect = computeCropRect(originalBitmap, scale, offset, cropSizePx)

        // 3) 크롭
        val cropped = cropBitmap(originalBitmap, rect)

        // 4) 파일 저장
        val file = saveBitmapToCacheFile(context, cropped)

        // 5) 업로드
        AWSUtils.uploadImage(context, file, "temp/$userId/${UUID.randomUUID()}.jpg",
            onProgress = {

            }, onComplete = { result ->
                result.onSuccess {
                    emitToParent(UserProfileImageEditDelegate.Pop(it, file))
                }.onFailure {
                    emitToParent(UserProfileImageEditDelegate.Pop())
                }
            }
        )
    }

    private fun loadBitmapFromUri(context: Context): Bitmap {
        return if (Build.VERSION.SDK_INT >= 28) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }
    }

    private fun computeCropRect(
        bitmap: Bitmap,
        scale: Float,
        offset: Offset,
        cropSizePx: Int
    ): Rect {
        val bw = bitmap.width.toFloat()
        val bh = bitmap.height.toFloat()
        val box = cropSizePx.toFloat()

        // 1) ContentScale.Crop의 "기본 스케일" (박스를 cover하도록)
        val baseScale = maxOf(box / bw, box / bh)

        // 2) 최종 스케일 = 기본 * 사용자 줌
        val totalScale = baseScale * scale

        // 3) 최종 표시 크기(px)
        val displayedW = bw * totalScale
        val displayedH = bh * totalScale

        // 4) 박스(뷰)의 중심 + 사용자 offset이 적용된 이미지 중심
        val imageCenterX = box / 2f + offset.x
        val imageCenterY = box / 2f + offset.y

        // 5) 이미지의 top-left가 박스 좌표에서 어디인지
        val imageTopLeftX = imageCenterX - displayedW / 2f
        val imageTopLeftY = imageCenterY - displayedH / 2f

        // 6) 박스(0..box) 영역을 bitmap 좌표로 역변환
        val left = ((0f - imageTopLeftX) / totalScale).coerceIn(0f, bw)
        val top = ((0f - imageTopLeftY) / totalScale).coerceIn(0f, bh)
        val right = ((box - imageTopLeftX) / totalScale).coerceIn(0f, bw)
        val bottom = ((box - imageTopLeftY) / totalScale).coerceIn(0f, bh)

        val l = left.toInt()
        val t = top.toInt()
        val r = right.toInt()
        val b = bottom.toInt()

        return Rect(l, t, r, b)
    }

    private fun cropBitmap(bitmap: Bitmap, cropRect: Rect): Bitmap {
        val width = cropRect.width().coerceAtLeast(1)
        val height = cropRect.height().coerceAtLeast(1)

        return Bitmap.createBitmap(
            bitmap,
            cropRect.left,
            cropRect.top,
            width,
            height
        )
    }

    private fun saveBitmapToCacheFile(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90
    ): File {
        val file = File(context.cacheDir, "profile_crop_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { out ->
            bitmap.compress(format, quality, out)
        }
        return file
    }
}