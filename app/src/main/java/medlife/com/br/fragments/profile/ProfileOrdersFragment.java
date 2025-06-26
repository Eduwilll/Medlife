package medlife.com.br.fragments.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.helper.OrderManager;
import medlife.com.br.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class ProfileOrdersFragment extends Fragment {
    private RecyclerView recyclerOrders;
    private TextView textEmptyOrders;
    private FirebaseFirestore db;
    private OrderAdapter orderAdapter;
    private List<Order> ordersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_orders, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerOrders = view.findViewById(R.id.recyclerOrders);
        textEmptyOrders = view.findViewById(R.id.textEmptyOrders);
        
        // Initialize RecyclerView
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersList = new ArrayList<>();
        orderAdapter = new OrderAdapter(ordersList);
        recyclerOrders.setAdapter(orderAdapter);

        // Load orders
        loadOrders();

        return view;
    }

    private void loadOrders() {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null) {
            OrderManager.getUserOrders(userId)
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        ordersList.clear();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Order order = document.toObject(Order.class);
                            if (order != null) {
                                ordersList.add(order);
                            }
                        }
                        
                        if (ordersList.isEmpty()) {
                            recyclerOrders.setVisibility(View.GONE);
                            textEmptyOrders.setVisibility(View.VISIBLE);
                        } else {
                            recyclerOrders.setVisibility(View.VISIBLE);
                            textEmptyOrders.setVisibility(View.GONE);
                            orderAdapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Erro ao carregar pedidos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        recyclerOrders.setVisibility(View.GONE);
                        textEmptyOrders.setVisibility(View.VISIBLE);
                    });
        }
    }
    
    // OrderAdapter class
    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
        private List<Order> orders;

        public OrderAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @NonNull
        @Override
        public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
            Order order = orders.get(position);
            holder.bind(order);
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }

        class OrderViewHolder extends RecyclerView.ViewHolder {
            private TextView textOrderId;
            private TextView textOrderDate;
            private TextView textOrderStatus;
            private TextView textOrderTotal;
            private TextView textDeliveryOption;
            private TextView textPaymentStatus;
            private TextView textOrderItemsPreview;

            public OrderViewHolder(@NonNull View itemView) {
                super(itemView);
                textOrderId = itemView.findViewById(R.id.textOrderId);
                textOrderDate = itemView.findViewById(R.id.textOrderDate);
                textOrderStatus = itemView.findViewById(R.id.textOrderStatus);
                textOrderTotal = itemView.findViewById(R.id.textOrderTotal);
                textDeliveryOption = itemView.findViewById(R.id.textDeliveryOption);
                textPaymentStatus = itemView.findViewById(R.id.textPaymentStatus);
                textOrderItemsPreview = itemView.findViewById(R.id.textOrderItemsPreview);
            }

            public void bind(Order order) {
                // Format order ID (show only first 8 characters)
                String shortOrderId = order.getOrderId().substring(0, 8).toUpperCase();
                textOrderId.setText("Pedido #" + shortOrderId);
                
                // Format date
                if (order.getCreatedAt() != null) {
                    String date = order.getCreatedAt().toDate().toString();
                    textOrderDate.setText(date);
                }
                
                // Set status
                textOrderStatus.setText(order.getStatus());
                
                // Format total
                NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
                textOrderTotal.setText(format.format(order.getTotalPrice()));
                
                // Set delivery option
                String deliveryText = "";
                switch (order.getDeliveryOption()) {
                    case "immediate":
                        deliveryText = "Entrega Imediata";
                        break;
                    case "pickup":
                        deliveryText = "Retirar na Loja";
                        break;
                    case "scheduled":
                        deliveryText = "Entrega Agendada";
                        break;
                }
                textDeliveryOption.setText(deliveryText);
                
                // Set payment status
                textPaymentStatus.setText(order.getPaymentStatus());
                
                // Set items preview
                int itemCount = order.getItems() != null ? order.getItems().size() : 0;
                textOrderItemsPreview.setText(itemCount + " item" + (itemCount != 1 ? "s" : ""));
            }
        }
    }
} 