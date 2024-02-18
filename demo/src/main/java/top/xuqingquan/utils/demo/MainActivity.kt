package top.xuqingquan.utils.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import top.xuqingquan.utils.Timber
import top.xuqingquan.utils.noFastClick
import top.xuqingquan.utils.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        val btn = findViewById<View>(R.id.btn)
        val btn1 = findViewById<View>(R.id.btn1)
        val btn2 = findViewById<View>(R.id.btn2)
        btn.noFastClick(5000) {
            Timber.d("btnbtnbtnbtnbtn")
            startActivity<TestActivity>()
        }
        btn1.noFastClick {
            Timber.d("btn1btn1btn1btn1")
        }
        btn2.noFastClick {
            Log.d("MainActivity", "onCreate: ${DeviceIdentifier.getOAID(this)}")
        }

    }
}