package com.juyeon.androidpractice.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.juyeon.androidpractice.ui.theme.BackgroundBase

@Composable
fun GradientBackground(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundBase)
    ){
        content()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GradientBackgroundPreview(){
    GradientBackground {  }
}