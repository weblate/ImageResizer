/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.feature.draw.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoFixHigh
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AutoFixNormal
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.TextFormat
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFilterButton
import ru.tech.imageresizershrinker.core.filters.presentation.widget.AddFiltersSheet
import ru.tech.imageresizershrinker.core.filters.presentation.widget.FilterItem
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Cube
import ru.tech.imageresizershrinker.core.resources.icons.Highlighter
import ru.tech.imageresizershrinker.core.resources.icons.Laser
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.SupportingButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ToggleGroupButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.FontResSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.draw.domain.DrawMode
import ru.tech.imageresizershrinker.feature.draw.domain.Pt
import ru.tech.imageresizershrinker.feature.draw.domain.coerceIn
import ru.tech.imageresizershrinker.feature.draw.domain.pt

@Composable
fun DrawModeSelector(
    modifier: Modifier,
    value: DrawMode,
    strokeWidth: Pt,
    onValueChange: (DrawMode) -> Unit
) {
    var isSheetVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .container(RoundedCornerShape(24.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToggleGroupButton(
            enabled = true,
            itemCount = DrawMode.entries.size,
            title = {
                Text(
                    text = stringResource(R.string.draw_mode),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                SupportingButton(
                    onClick = {
                        isSheetVisible = true
                    }
                )
            },
            selectedIndex = DrawMode.entries.indexOfFirst {
                value::class.isInstance(it)
            },
            buttonIcon = {},
            itemContent = {
                Icon(
                    imageVector = DrawMode.entries[it].getIcon(),
                    contentDescription = null
                )
            },
            indexChanged = {
                onValueChange(DrawMode.entries[it])
            }
        )

        AnimatedVisibility(
            visible = value is DrawMode.PathEffect.PrivacyBlur,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            BlurRadiusSelector(
                modifier = Modifier.padding(8.dp),
                value = (value as? DrawMode.PathEffect.PrivacyBlur)?.blurRadius ?: 0,
                valueRange = 5f..50f,
                onValueChange = {
                    onValueChange(DrawMode.PathEffect.PrivacyBlur(it))
                },
                color = MaterialTheme.colorScheme.surface
            )
        }

        AnimatedVisibility(
            visible = value is DrawMode.PathEffect.Pixelation,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            PixelSizeSelector(
                modifier = Modifier.padding(8.dp),
                value = (value as? DrawMode.PathEffect.Pixelation)?.pixelSize ?: 0f,
                onValueChange = {
                    onValueChange(DrawMode.PathEffect.Pixelation(it))
                },
                color = MaterialTheme.colorScheme.surface
            )
        }

        AnimatedVisibility(
            visible = value is DrawMode.Text,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                RoundedTextField(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .container(
                            shape = ContainerShapeDefaults.topShape,
                            color = MaterialTheme.colorScheme.surface
                        )
                        .padding(8.dp),
                    value = (value as? DrawMode.Text)?.text ?: "",
                    singleLine = false,
                    onValueChange = {
                        onValueChange(
                            (value as? DrawMode.Text)?.copy(
                                text = it
                            ) ?: value
                        )
                    },
                    label = {
                        Text(stringResource(R.string.text))
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                FontResSelector(
                    fontRes = (value as? DrawMode.Text)?.font ?: 0,
                    onValueChange = {
                        onValueChange(
                            (value as? DrawMode.Text)?.copy(
                                font = it.fontRes ?: 0
                            ) ?: value
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    shape = ContainerShapeDefaults.centerShape
                )
                Spacer(modifier = Modifier.height(4.dp))
                val isDashSizeControlVisible = (value as? DrawMode.Text)?.isRepeated == true
                PreferenceRowSwitch(
                    title = stringResource(R.string.repeat_text),
                    subtitle = stringResource(R.string.repeat_text_sub),
                    checked = (value as? DrawMode.Text)?.isRepeated == true,
                    onClick = {
                        onValueChange(
                            (value as? DrawMode.Text)?.copy(
                                isRepeated = it
                            ) ?: value
                        )
                    },
                    color = MaterialTheme.colorScheme.surface,
                    shape = animateShape(
                        if (isDashSizeControlVisible) ContainerShapeDefaults.centerShape
                        else ContainerShapeDefaults.bottomShape
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    resultModifier = Modifier.padding(16.dp),
                    applyHorizontalPadding = false
                )
                Spacer(
                    modifier = Modifier.height(
                        if (isDashSizeControlVisible) 4.dp else 8.dp
                    )
                )
                AnimatedVisibility(
                    visible = isDashSizeControlVisible,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    EnhancedSliderItem(
                        value = (value as? DrawMode.Text)?.repeatingInterval?.value ?: 0f,
                        title = stringResource(R.string.dash_size),
                        valueRange = 0f..100f,
                        internalStateTransformation = {
                            it.roundToTwoDigits()
                        },
                        onValueChange = {
                            onValueChange(
                                (value as? DrawMode.Text)?.copy(
                                    repeatingInterval = it.pt
                                ) ?: value
                            )
                        },
                        color = MaterialTheme.colorScheme.surface,
                        valueSuffix = " Pt",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp),
                        shape = ContainerShapeDefaults.bottomShape
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = value is DrawMode.Image,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                ImageSelector(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    value = (value as? DrawMode.Image)?.imageData ?: "",
                    onValueChange = {
                        onValueChange(
                            (value as? DrawMode.Image)?.copy(
                                imageData = it
                            ) ?: value
                        )
                    },
                    subtitle = stringResource(id = R.string.draw_image_sub),
                    shape = ContainerShapeDefaults.topShape,
                    color = MaterialTheme.colorScheme.surface
                )
                Spacer(modifier = Modifier.height(4.dp))
                val dashMinimum = -((strokeWidth.value * 0.9f) / 2).toInt().toFloat()
                LaunchedEffect(dashMinimum, value) {
                    if (value is DrawMode.Image && value.repeatingInterval < dashMinimum.pt) {
                        onValueChange(
                            (value as? DrawMode.Image)?.copy(
                                repeatingInterval = value.repeatingInterval.coerceIn(
                                    dashMinimum.pt,
                                    100.pt
                                )
                            ) ?: value
                        )
                    }
                }
                EnhancedSliderItem(
                    value = (value as? DrawMode.Image)?.repeatingInterval?.value ?: 0f,
                    title = stringResource(R.string.dash_size),
                    valueRange = dashMinimum..100f,
                    internalStateTransformation = {
                        it.roundToTwoDigits()
                    },
                    onValueChange = {
                        onValueChange(
                            (value as? DrawMode.Image)?.copy(
                                repeatingInterval = it.pt.coerceIn(dashMinimum.pt, 100.pt)
                            ) ?: value
                        )
                    },
                    color = MaterialTheme.colorScheme.surface,
                    valueSuffix = " Pt",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    shape = ContainerShapeDefaults.bottomShape
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        AnimatedVisibility(
            visible = value is DrawMode.PathEffect.Custom,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            val filter by remember(value) {
                derivedStateOf {
                    (value as? DrawMode.PathEffect.Custom)?.filter?.toUiFilter()
                }
            }
            var showFilterSelection by rememberSaveable {
                mutableStateOf(false)
            }
            AnimatedContent(targetState = filter != null) { notNull ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    if (notNull && filter != null) {
                        FilterItem(
                            filter = filter!!,
                            showDragHandle = false,
                            onRemove = {
                                onValueChange(
                                    DrawMode.PathEffect.Custom()
                                )
                            },
                            onFilterChange = { value ->
                                onValueChange(
                                    DrawMode.PathEffect.Custom(filter!!.copy(value))
                                )
                            },
                            backgroundColor = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.mixedContainer,
                            onClick = {
                                showFilterSelection = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AutoFixNormal,
                                contentDescription = stringResource(R.string.replace_filter)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.replace_filter))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    } else {
                        Row(
                            modifier = Modifier
                                .container(
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    resultPadding = 0.dp,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.pick_filter_info),
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        AddFilterButton(
                            onClick = {
                                showFilterSelection = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                AddFiltersSheet(
                    visible = showFilterSelection,
                    onVisibleChange = {
                        showFilterSelection = it
                    },
                    canAddTemplates = false,
                    previewBitmap = null,
                    onFilterPicked = {
                        onValueChange(
                            DrawMode.PathEffect.Custom(it.newInstance())
                        )
                    },
                    onFilterPickedWithParams = {
                        onValueChange(
                            DrawMode.PathEffect.Custom(it)
                        )
                    }
                )
            }
        }
    }
    SimpleSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawMode.entries.forEachIndexed { index, item ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .container(
                                shape = ContainerShapeDefaults.shapeForIndex(
                                    index,
                                    DrawMode.entries.size
                                ),
                                resultPadding = 0.dp
                            )
                    ) {
                        TitleItem(text = stringResource(item.getTitle()), icon = item.getIcon())
                        Text(
                            text = stringResource(item.getSubtitle()),
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 16.dp
                            ),
                            fontSize = 14.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        visible = isSheetVisible,
        onDismiss = {
            isSheetVisible = it
        },
        title = {
            TitleItem(text = stringResource(R.string.draw_mode))
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { isSheetVisible = false }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        }
    )
}

private fun DrawMode.getSubtitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter_sub
    is DrawMode.Neon -> R.string.neon_sub
    is DrawMode.Pen -> R.string.pen_sub
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur_sub
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation_sub
    is DrawMode.Text -> R.string.draw_text_sub
    is DrawMode.Image -> R.string.draw_mode_image_sub
    is DrawMode.PathEffect.Custom -> R.string.draw_filter_sub
}

private fun DrawMode.getTitle(): Int = when (this) {
    is DrawMode.Highlighter -> R.string.highlighter
    is DrawMode.Neon -> R.string.neon
    is DrawMode.Pen -> R.string.pen
    is DrawMode.PathEffect.PrivacyBlur -> R.string.privacy_blur
    is DrawMode.PathEffect.Pixelation -> R.string.pixelation
    is DrawMode.Text -> R.string.text
    is DrawMode.Image -> R.string.image
    is DrawMode.PathEffect.Custom -> R.string.filter
}

private fun DrawMode.getIcon(): ImageVector = when (this) {
    is DrawMode.Highlighter -> Icons.Rounded.Highlighter
    is DrawMode.Neon -> Icons.Rounded.Laser
    is DrawMode.Pen -> Icons.Rounded.Brush
    is DrawMode.PathEffect.PrivacyBlur -> Icons.Rounded.BlurCircular
    is DrawMode.PathEffect.Pixelation -> Icons.Rounded.Cube
    is DrawMode.Text -> Icons.Rounded.TextFormat
    is DrawMode.Image -> Icons.Outlined.Image
    is DrawMode.PathEffect.Custom -> Icons.Outlined.AutoFixHigh
}