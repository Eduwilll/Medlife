package medlife.com.br.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import medlife.com.br.R;
import medlife.com.br.helper.CartManager;
import medlife.com.br.model.Product;

public class ProductDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private TextView quantityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        ImageView productImage = findViewById(R.id.product_image);
        TextView productName = findViewById(R.id.product_name);
        TextView productSubtitle = findViewById(R.id.product_subtitle);
        TextView productPrice = findViewById(R.id.product_price);
        ImageView backArrow = findViewById(R.id.back_arrow);
        ImageView minusButton = findViewById(R.id.minus_button);
        ImageView plusButton = findViewById(R.id.plus_button);
        quantityText = findViewById(R.id.quantity_text);
        Button addToCartButton = findViewById(R.id.add_to_cart_button);

        Product product = getIntent().getParcelableExtra("product");

        if (product != null) {
            productImage.setImageResource(product.getImage());
            productName.setText(product.getName());
            productSubtitle.setText(product.getDescription());
            productPrice.setText(product.getPrice());
        }

        backArrow.setOnClickListener(v -> finish());

        minusButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                quantityText.setText(String.valueOf(quantity));
            }
        });

        plusButton.setOnClickListener(v -> {
            quantity++;
            quantityText.setText(String.valueOf(quantity));
        });

        addToCartButton.setOnClickListener(v -> {
            if (product != null) {
                CartManager.getInstance().addProduct(product, quantity);
                Toast.makeText(this, "Adicionado ao carrinho", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
} 