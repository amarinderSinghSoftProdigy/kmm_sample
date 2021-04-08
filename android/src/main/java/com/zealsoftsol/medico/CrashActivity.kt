package com.zealsoftsol.medico

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zealsoftsol.medico.screens.common.Space

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stackTrace = intent.getStringExtra(TRACE)!!
        setContent {
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "CRASH",
                    color = Color.Red,
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
                Space(8.dp)
                Text(
                    text = "Stacktrace:",
                    style = MaterialTheme.typography.h6,
                )
                Space(16.dp)
                val string = buildAnnotatedString {
                    append(stackTrace)
                    val index = stackTrace.indexOf(CAUSED_BY)
                    if (index != -1) {
                        addStyle(
                            SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.W900,
                                fontSize = 16.sp,
                            ),
                            index,
                            index + CAUSED_BY.length,
                        )
                    }
                }
                Text(text = string, fontSize = 14.sp)
            }
        }
    }

    companion object {
        private const val CAUSED_BY = "Caused by:"
        const val TRACE = "trace"
    }
}
