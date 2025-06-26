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
import medlife.com.br.model.Farmacia;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;
    private Context context;
    private List<Object> displayList; // Can be Farmacia (header) or CartItem
    private CartListener listener;

    public interface CartListener {
        void onCartUpdated();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartListener listener) {
        this.context = context;
        this.listener = listener;
        this.displayList = buildDisplayList(cartItems);
    }

    private List<Object> buildDisplayList(List<CartItem> cartItems) {
        Map<Farmacia, List<CartItem>> grouped = new LinkedHashMap<>();
        for (CartItem item : cartItems) {
            Farmacia farmacia = item.getProduct().getFarmacia();
            if (!grouped.containsKey(farmacia)) {
                grouped.put(farmacia, new ArrayList<>());
            }
            grouped.get(farmacia).add(item);
        }
        List<Object> result = new ArrayList<>();
        for (Map.Entry<Farmacia, List<CartItem>> entry : grouped.entrySet()) {
            result.add(entry.getKey());
            result.addAll(entry.getValue());
        }
        return result;
    }

    @Override
    public int getItemViewType(int position) {
        return (displayList.get(position) instanceof Farmacia) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_cart_pharmacy_header, parent, false);
            return new PharmacyHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            return new CartViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            Farmacia farmacia = (Farmacia) displayList.get(position);
            PharmacyHeaderViewHolder headerHolder = (PharmacyHeaderViewHolder) holder;
            headerHolder.textPharmacyName.setText(farmacia.getName());
            headerHolder.textPharmacyLocation.setText(farmacia.getLocation());
            // Set logo based on pharmacy name (mock logic)
            if (farmacia.getName().toLowerCase().contains("são paulo")) {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.mock_drogariasaopaulo);
            } else if (farmacia.getName().toLowerCase().contains("drogasil")) {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.mock_logo_drogasil_2048);
            } else {
                headerHolder.imagePharmacyLogo.setImageResource(R.drawable.logo);
            }
        } else {
            CartItem cartItem = (CartItem) displayList.get(position);
            CartViewHolder itemHolder = (CartViewHolder) holder;
            itemHolder.productImage.setImageResource(cartItem.getProduct().getImage());
            itemHolder.productName.setText(cartItem.getProduct().getName());
            itemHolder.productPrice.setText(cartItem.getProduct().getPrice());
            itemHolder.quantityText.setText(String.valueOf(cartItem.getQuantity()));

            itemHolder.plusButton.setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(cartItem, cartItem.getQuantity() + 1);
                notifyDataSetChanged();
                listener.onCartUpdated();
            });

            itemHolder.minusButton.setOnClickListener(v -> {
                CartManager.getInstance().updateQuantity(cartItem, cartItem.getQuantity() - 1);
                notifyDataSetChanged();
                listener.onCartUpdated();
            });

            itemHolder.deleteButton.setOnClickListener(v -> {
                CartManager.getInstance().removeProduct(cartItem);
                notifyDataSetChanged();
                listener.onCartUpdated();
            });
        }
    }

    @Override
    public int getItemCount() {
        return displayList.size();
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

    public static class PharmacyHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textPharmacyName;
        TextView textPharmacyLocation;
        ImageView imagePharmacyLogo;
        public PharmacyHeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textPharmacyName = itemView.findViewById(R.id.textPharmacyName);
            textPharmacyLocation = itemView.findViewById(R.id.textPharmacyLocation);
            imagePharmacyLogo = itemView.findViewById(R.id.imagePharmacyLogo);
        }
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.displayList = buildDisplayList(cartItems);
        notifyDataSetChanged();
    }
} 