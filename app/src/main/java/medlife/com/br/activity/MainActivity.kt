package medlife.com.br.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import medlife.com.br.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Handler(Looper.getMainLooper()).postDelayed({
            abrirAutenticacao()
        }, 3000)
    }

    private fun abrirAutenticacao() {
        val i = Intent(this@MainActivity, AutenticacaoActivity::class.java)
        startActivity(i)
        finish()
    }
}
