package com.example.freefood_likebhandara.splash

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.freefood_likebhandara.R
import com.example.freefood_likebhandara.activities.SignInActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        startHeavyTask()
    }

    private fun startHeavyTask() {
        LongOperation().execute()
    }
    private open inner class LongOperation :AsyncTask<String?, Void?,String?>(){
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: String?): String? {
            for (i in 0..1){
                try {
                    Thread.sleep(1000)
                }
                catch (e: Exception){
                    Thread.interrupted()
                }
            }
            return "result"
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
        }
    }
}