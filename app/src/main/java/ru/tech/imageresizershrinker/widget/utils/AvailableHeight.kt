package ru.tech.imageresizershrinker.widget.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.observeAsState
import com.t8rin.modalsheet.FullscreenPopup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun availableHeight(
    expanded: Boolean,
    imageState: Int
): Dp {
    var fullHeight by remember(
        LocalConfiguration.current,
        LocalLifecycleOwner.current.lifecycle.observeAsState()
    ) { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    if (fullHeight == 0.dp) {
        FullscreenPopup {
            Column {
                TopAppBar(
                    title = { Text(" ") },
                    colors = TopAppBarDefaults.topAppBarColors(Color.Transparent)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .onSizeChanged {
                            with(density) {
                                fullHeight = it.height.toDp()
                            }
                        }
                )
                BottomAppBar(
                    containerColor = Color.Transparent,
                    floatingActionButton = {},
                    actions = {}
                )
            }
        }
    }

    return animateDpAsState(
        targetValue = fullHeight.times(
            when {
                expanded || imageState.isExpanded() -> 1f
                imageState == 3 -> 0.7f
                imageState == 2 -> 0.5f
                imageState == 1 -> 0.35f
                else -> 0.2f
            }
        )
    ).value
}

fun Int.isExpanded() = this == 4

fun middleImageState() = 2