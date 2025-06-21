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

public class CartFragment extends Fragment implements CartAdapter.CartListener {
    private RecyclerView recyclerCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private LinearLayout layoutEmpty;
    private LinearLayout layoutBottom;
    private TextView textTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        recyclerCartItems = view.findViewById(R.id.recyclerCartItems);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        layoutBottom = view.findViewById(R.id.layoutBottom);
        textTotal = view.findViewById(R.id.textTotal);

        setupCart();

        return view;
    }

    private void setupCart() {
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerCartItems.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerCartItems.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);

            cartAdapter = new CartAdapter(getContext(), cartItems, this);
            recyclerCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerCartItems.setAdapter(cartAdapter);
        }
        updateTotal();
    }

    private void updateTotal() {
        double totalPrice = CartManager.getInstance().getTotalPrice();
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        textTotal.setText(format.format(totalPrice));
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