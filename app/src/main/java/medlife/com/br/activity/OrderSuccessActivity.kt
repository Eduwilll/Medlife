package medlife.com.br.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import medlife.com.br.R

class OrderSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        val trackOrderButton: Button = findViewById(R.id.track_order_button)
        val backToHomeButton: TextView = findViewById(R.id.back_to_home_button)

        trackOrderButton.setOnClickListener { 
            // TODO: Implement order tracking logic
            Toast.makeText(this, "Acompanhar pedido - A ser implementado", Toast.LENGTH_SHORT).show()
        }

        backToHomeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        findViewById<TextView>(R.id.back_to_home_button)?.performClick()
    }
}
