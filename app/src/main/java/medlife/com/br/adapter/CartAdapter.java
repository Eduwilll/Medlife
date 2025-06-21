package medlife.com.br.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.model.CartItem;
import medlife.com.br.helper.CartManager;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItems;
    private CartListener listener;

    public interface CartListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.productImage.setImageResource(cartItem.getProduct().getImage());
        holder.productName.setText(cartItem.getProduct().getName());
        holder.productPrice.setText(cartItem.getProduct().getPrice());
        holder.quantityText.setText(String.valueOf(cartItem.getQuantity()));

        holder.plusButton.setOnClickListener(v -> {
            CartManager.getInstance().updateQuantity(cartItem, cartItem.getQuantity() + 1);
            notifyItemChanged(position);
            listener.onCartUpdated();
        });

        holder.minusButton.setOnClickListener(v -> {
            CartManager.getInstance().updateQuantity(cartItem, cartItem.getQuantity() - 1);
            notifyDataSetChanged(); // Use notifyDataSetChanged for removals
            listener.onCartUpdated();
        });

        holder.deleteButton.setOnClickListener(v -> {
            CartManager.getInstance().removeProduct(cartItem);
            notifyDataSetChanged(); // Use notifyDataSetChanged for removals
            listener.onCartUpdated();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView quantityText;
        ImageView plusButton;
        ImageView minusButton;
        ImageView deleteButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            plusButton = itemView.findViewById(R.id.plus_button);
            minusButton = itemView.findViewById(R.id.minus_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
} 