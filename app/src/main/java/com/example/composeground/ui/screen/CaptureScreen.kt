package com.example.composeground.ui.screen

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.doOnLayout
import com.example.composeground.ui.theme.ComposeGroundTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun CaptureScreen() {
    ComposeGroundTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ContentBox()
                Spacer(modifier = Modifier.height(height = 100.dp))
                HorizontalDivider(
                    modifier = Modifier
                        .height(20.dp)
                        .background(color = Color.Blue)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max)
                        .background(color = Color.Green),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "title1", fontSize = 54.sp)
                        Text(
                            text = "title2",
                            fontSize = 50.sp,
                            modifier = Modifier.background(Color.Red),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            )
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .width(width = 12.dp)
                            .fillMaxHeight()
                            .background(color = Color.DarkGray)
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "title1", fontSize = 54.sp)
                        Text(
                            text = "title2",
                            fontSize = 30.sp,
                            modifier = Modifier
                                .background(Color.Red)
                                .fillMaxHeight(),
                            style = TextStyle(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier
                        .height(20.dp)
                        .background(color = Color.Blue)
                )
                Spacer(modifier = Modifier.padding(top = 200.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val bitmap = createBitmapFromComposable(
                                context = context,
                                content = {
                                    ContentBox()
                                }
                            )

                            val uri = saveBitmapToCache(context, bitmap) ?: return@launch
                            val bitmap2 = uriToBitmap(context, uri) ?: return@launch
                            saveBitmapToGallery(
                                context,
                                bitmap2,
                                "screenshot_${System.currentTimeMillis()}"
                            )
                            deleteCachedBitmap(context, uri)
                        }
                    },
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.End)
                ) {
                    Text("button")
                }
            }
        }
    }
}

fun deleteCachedBitmap(context: Context, uri: Uri) {
    try {
        val file = File(uri.path ?: return)
        if (file.exists()) {
            file.delete()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri? {
    return try {
        val file = File(context.cacheDir, "captured_image.png")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()
        Uri.fromFile(file)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun ContentBox() {
    Column(
        modifier = Modifier
            .background(Color.Yellow)
            .padding(24.dp)
    ) {
        for (i in 1..20) {
            Text("이건 저장 되야 된다!$i")
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun createBitmapFromComposable(
    context: Context,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {
    val composeView = ComposeView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setContent {
            content()
        }
    }

    val decorView = (context as? Activity)?.window?.decorView as? FrameLayout
    decorView?.addView(composeView)

    suspendCancellableCoroutine { continuation ->
        composeView.doOnLayout {
            val bitmap = Bitmap.createBitmap(
                composeView.measuredWidth,
                composeView.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)

            composeView.draw(canvas)
            decorView?.removeView(composeView)
            continuation.resume(bitmap, onCancellation = null)
        }
    }
}

/**
 * Bitmap을 기기에 저장하는 함수.
 * Android Q(API 29) 이상과 그 이하에서 모두 동작하도록 MediaStore를 사용.
 */
suspend fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String) =
    withContext(Dispatchers.Main) {
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "Screenshots"
                )
            }

            val imageUri =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            val fos = imageUri?.let { resolver.openOutputStream(it) }
            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.close()
            }
        } else {
            // API 28 이하: 외부 저장소의 절대 경로 사용 (WRITE_EXTERNAL_STORAGE 권한 필요)
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() +
                        File.separator + "Screenshots"
            val file = File(imagesDir)
            if (!file.exists()) {
                file.mkdirs()
            }
            val image = File(file, "$displayName.png")
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

@Preview(showBackground = true)
@Composable
fun TestScreenPreview() {
    CaptureScreen()
}