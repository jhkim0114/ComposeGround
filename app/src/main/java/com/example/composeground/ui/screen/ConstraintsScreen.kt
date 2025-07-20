package com.example.composeground.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.Dimension

@Preview(showBackground = true)
@Composable
fun ConstraintsScreen(
    modifier: Modifier = Modifier,
    text1: String = "text1text1text1text1text1text1",
    text2: String? = "text12"
) {

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        val (image1, text1Ref, text2Ref, spacer, image2) = createRefs()

        // Image1
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color.Red)
                .constrainAs(image1) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        // Text1 (최대한 확장하지만, spacer가 먼저 줄어듦)
        Text(
            text = text1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(text1Ref) {
                    start.linkTo(image1.end, margin = 8.dp)
                    end.linkTo(if (text2 != null) text2Ref.start else spacer.start, margin = 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.percent(0f)
                }
        )

        // Text2 (nullable)
        if (text2 != null) {
            Text(
                text = text2,
                modifier = Modifier.constrainAs(text2Ref) {
                    start.linkTo(text1Ref.end, margin = 8.dp)
                    end.linkTo(spacer.start, margin = 8.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
            )
        }

        // Spacer (남는 공간 차지, 줄어들 수 있음)
        Box(
            modifier = Modifier
                .constrainAs(spacer) {
                    start.linkTo(if (text2 != null) text2Ref.end else text1Ref.end)
                    end.linkTo(image2.start, margin = 8.dp)
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        // Image2
        Box(
            modifier = Modifier
                .clickable(
                    onClickLabel = "asdf"
                ) {  }
                .semantics(
                    mergeDescendants = true
                ) {
                    onClick(
                        label = "",
                        action = {
                            false
                        }
                    )
                    heading()
                }
                .semantics {
                    stateDescription = "어쩌구 저쩌구"
                }
                .toggleable(
                    value = true,
                    onValueChange = { _ ->
                        // viewModel값 변경
                    },
                    role = Role.Checkbox
                )
                .size(24.dp)
                .background(Color.Blue)
                .constrainAs(image2) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdaptiveTextRow(
    text1: String = "text 1",
    text2: String? = "text 2"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이미지1
        Box(
            Modifier
                .size(24.dp)
                .background(Color.Red)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 텍스트1 (우선순위 낮음)
        Text(
            text = text1,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f, fill = false)
        )

        // 텍스트2 (nullable)
        if (text2 != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text2,
                maxLines = 1,
                modifier = Modifier.wrapContentWidth()
            )
        }

        // Spacer (여유 공간 채우기)
        Spacer(modifier = Modifier.weight(1f))

        // 이미지2
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            Modifier
                .size(24.dp)
                .background(Color.Blue)
        )
    }
}
