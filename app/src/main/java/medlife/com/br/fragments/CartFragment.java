package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.adapter.CartAdapter;
import medlife.com.br.helper.CartManager;
import medlife.com.br.model.CartItem;
import android.widget.TextView;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import com.google.firebase.firestore.FirebaseFirestore;

import medlife.com.br.activity.OrderSuccessActivity;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;
import medlife.com.br.helper.OrderManager;
import medlife.com.br.model.Order;
import android.widget.ImageView;
import medlife.com.br.model.Product;

public class CartFragment extends Fragment implements CartAdapter.CartListener {
    private RecyclerView recyclerCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView textTotal;
    private Button buttonCheckout;
    private FirebaseFirestore db;
    private TextView textEmptyCart;
    private ImageView imageEmptyCart;
    private LinearLayout layoutEmptyState;
    private LinearLayout layoutMainContent;
    private LinearLayout layoutPrescriptionWarning;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerCartItems = view.findViewById(R.id.recyclerCartItems);
        textTotal = view.findViewById(R.id.textTotal);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
        db = FirebaseFirestore.getInstance();
        textEmptyCart = view.findViewById(R.id.textEmptyCart);
        imageEmptyCart = view.findViewById(R.id.imageEmptyCart);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        layoutMainContent = view.findViewById(R.id.layoutMainContent);
        layoutPrescriptionWarning = view.findViewById(R.id.layoutPrescriptionWarning);

        setupCart();

        buttonCheckout.setOnClickListener(v -> {
            Order newOrder = CartManager.getInstance().createOrderFromCart();
            if (newOrder != null) {
                Objects.requireNonNull(OrderManager.saveOrder(newOrder))
                        .addOnSuccessListener(aVoid -> {
                            CartManager.getInstance().clearCart();
                            Intent intent = new Intent(getActivity(), OrderSuccessActivity.class);
                            startActivity(intent);
                            Toast.makeText(getContext(), "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Falha ao realizar o pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        });
            } else {
                Toast.makeText(getContext(), "Erro: Não foi possível criar o pedido", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void setupCart() {
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            layoutMainContent.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            layoutMainContent.setVisibility(View.VISIBLE);

            cartAdapter = new CartAdapter(getContext(), cartItems, this);
            recyclerCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerCartItems.setAdapter(cartAdapter);
        }
        updateTotal();
        updatePrescriptionWarning();
    }

    private void updateTotal() {
        double totalPrice = CartManager.getInstance().getTotalPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        textTotal.setText(format.format(totalPrice));
    }

    private void updatePrescriptionWarning() {
        boolean needsPrescription = false;
        for (CartItem item : cartItems) {
            String tarja = item.getProduct().getTarja();
            if (Product.TARJA_PRETA.equals(tarja) || Product.TARJA_VERMELHA_SEM_RETENCAO.equals(tarja) || Product.TARJA_VERMELHA_COM_RETENCAO.equals(tarja)) {
                needsPrescription = true;
                break;
            }
        }
        if (layoutPrescriptionWarning != null) {
            layoutPrescriptionWarning.setVisibility(needsPrescription ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setupCart();
    }

    @Override
    public void onCartUpdated() {
        updateTotal();
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            setupCart(); // Refresh the whole view if cart becomes empty
        }
    }
}