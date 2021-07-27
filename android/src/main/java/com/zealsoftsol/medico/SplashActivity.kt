package com.zealsoftsol.medico

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, R.anim.no_op_anim)
        window.decorView.post { finish() }
    }
}
