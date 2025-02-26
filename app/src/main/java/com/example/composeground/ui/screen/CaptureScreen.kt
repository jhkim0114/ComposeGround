package com.example.composeground.ui.screen

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeground.ui.theme.ComposeGroundTheme
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

@Composable
fun CaptureScreen() {
    ComposeGroundTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            val context = LocalContext.current
            val rootView = LocalView.current
            var captureAreaCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).onGloballyPositioned {
                    captureAreaCoordinates = it
                }
            ) {
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
                    Spacer(modifier = Modifier.width(width = 12.dp).fillMaxHeight().background(color = Color.DarkGray))

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "title1", fontSize = 54.sp)
                        Text(
                            text = "title2",
                            fontSize = 30.sp,
                            modifier = Modifier.background(Color.Red).fillMaxHeight(),
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
                        captureAreaCoordinates?.let { coords ->
                            // 캡처할 영역의 위치와 크기 계산 (윈도우 기준)
                            val position = coords.positionInWindow()
                            val size = coords.size
                            val bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(bitmap)
                            // 캔버스를 캡처 영역에 맞게 이동
                            canvas.translate(-position.x, -position.y)
                            // 루트 뷰 전체를 캔버스에 그리면, 지정한 영역만 Bitmap에 그려짐
                            rootView.draw(canvas)
                            capturedBitmap = bitmap

                            // Bitmap을 스크린샷 저장 폴더에 파일로 저장
                            saveBitmapToGallery(context, bitmap, "screenshot_${System.currentTimeMillis()}")
                        }
                    },
                    modifier = Modifier.wrapContentWidth().align(Alignment.End)
                ) {
                    Text("button")
                }
            }
        }
    }
}

/**
 * Bitmap을 기기에 저장하는 함수.
 * Android Q(API 29) 이상과 그 이하에서 모두 동작하도록 MediaStore를 사용.
 */
fun saveBitmapToGallery(context: Context, bitmap: Bitmap, displayName: String) {
    val fos: OutputStream?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // API 29 이상: MediaStore를 통해 상대경로로 저장
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName.png")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + "Screenshots"
            )
        }
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { resolver.openOutputStream(it) }
    } else {
        // API 28 이하: 외부 저장소의 절대 경로 사용 (WRITE_EXTERNAL_STORAGE 권한 필요)
        val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() +
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