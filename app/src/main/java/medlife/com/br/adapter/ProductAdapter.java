package medlife.com.br.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.activity.ProductDetailActivity;
import medlife.com.br.model.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.helper.UsuarioFirebase;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> productList;
    private boolean favoriteMode = false;

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProductAdapter(Context context, List<Product> productList) {
        this(context, productList, false);
    }

    public ProductAdapter(Context context, List<Product> productList, boolean favoriteMode) {
        this.context = context;
        this.productList = productList;
        this.favoriteMode = favoriteMode;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (favoriteMode) {
            view = LayoutInflater.from(context).inflate(R.layout.item_favorite_product, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.imageProduct.setImageResource(product.getImage());
        holder.textProductName.setText(product.getName());
        if (holder.textProductPrice != null) holder.textProductPrice.setText(product.getPrice());
        if (!favoriteMode && holder.textProductDesc != null) holder.textProductDesc.setText(product.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(product);
            } else if (!favoriteMode) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("product", product);
                context.startActivity(intent);
            }
        });

        if (favoriteMode && holder.favoriteIcon != null) {
            holder.favoriteIcon.setVisibility(View.VISIBLE);
            holder.favoriteIcon.setOnClickListener(v -> {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION && adapterPosition < productList.size()) {
                    // Remove from SharedPreferences
                    SharedPreferences prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(product.getName(), false).apply();
                    // Remove from Firestore
                    removeFavoriteFromFirestore(product);
                    // Remove from list and notify
                    productList.remove(adapterPosition);
                    notifyItemRemoved(adapterPosition);
                    notifyItemRangeChanged(adapterPosition, productList.size());
                }
            });
        } else if (holder.favoriteIcon != null) {
            holder.favoriteIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName;
        TextView textProductDesc;
        TextView textProductPrice;
        ImageView addButton;
        ImageView favoriteIcon;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductDesc = itemView.findViewById(R.id.textProductDesc);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            addButton = itemView.findViewById(R.id.addButton);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
        }
    }

    public static void addFavoriteToFirestore(Product product) {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null && product != null) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .collection("favoritosProdutos")
                .document(product.getName())
                .set(product);
        }
    }

    public static void removeFavoriteFromFirestore(Product product) {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null && product != null) {
            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(userId)
                .collection("favoritosProdutos")
                .document(product.getName())
                .delete();
        }
    }
} 