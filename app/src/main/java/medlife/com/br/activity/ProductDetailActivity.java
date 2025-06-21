package medlife.com.br.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import medlife.com.br.R;
import medlife.com.br.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImage = findViewById(R.id.product_image);
        TextView productName = findViewById(R.id.product_name);
        TextView productSubtitle = findViewById(R.id.product_subtitle);
        TextView productPrice = findViewById(R.id.product_price);
        ImageView backArrow = findViewById(R.id.back_arrow);

        Product product = getIntent().getParcelableExtra("product");

        if (product != null) {
            productImage.setImageResource(product.getImage());
            productName.setText(product.getName());
            productSubtitle.setText(product.getDescription());
            productPrice.setText(product.getPrice());
        }

        backArrow.setOnClickListener(v -> finish());
    }
} 