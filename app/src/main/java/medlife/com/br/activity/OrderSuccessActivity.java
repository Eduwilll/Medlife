package medlife.com.br.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import medlife.com.br.R;

public class OrderSuccessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_success);

        Button trackOrderButton = findViewById(R.id.track_order_button);
        TextView backToHomeButton = findViewById(R.id.back_to_home_button);

        trackOrderButton.setOnClickListener(v -> {
            // TODO: Implement order tracking logic
            Toast.makeText(this, "Acompanhar pedido - A ser implementado", Toast.LENGTH_SHORT).show();
        });

        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        // Prevent going back to the cart
        super.onBackPressed();
        findViewById(R.id.back_to_home_button).performClick();
    }
} 