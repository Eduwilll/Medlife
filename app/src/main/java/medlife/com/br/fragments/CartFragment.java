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
import medlife.com.br.model.Usuario;
import medlife.com.br.helper.UsuarioFirebase;
import java.util.Map;

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
    private TextView textDeliveryAddress;
    private TextView textDeliveryAddressTitle;
    private LinearLayout layoutDeliveryAddress;
    private TextView textSubtotal;
    private TextView textDeliveryFee;
    private TextView textDiscount;
    private LinearLayout cardDeliveryImmediate;
    private LinearLayout cardDeliveryStorePickup;
    private LinearLayout cardDeliveryScheduled;
    private TextView textDeliveryImmediateTitle;
    private TextView textDeliveryStorePickupTitle;
    private TextView textDeliveryScheduledTitle;
    private double deliveryFee = 7.00;
    private double discountAmount = 0.0;
    private String selectedDeliveryOption = "immediate"; // Default to immediate delivery

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
        textDeliveryAddress = view.findViewById(R.id.textDeliveryAddress);
        textDeliveryAddressTitle = view.findViewById(R.id.textDeliveryAddressTitle);
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress);
        textSubtotal = view.findViewById(R.id.textSubtotal);
        textDeliveryFee = view.findViewById(R.id.textDeliveryFee);
        textDiscount = view.findViewById(R.id.textDiscount);
        cardDeliveryImmediate = view.findViewById(R.id.cardDeliveryImmediate);
        cardDeliveryStorePickup = view.findViewById(R.id.cardDeliveryStorePickup);
        cardDeliveryScheduled = view.findViewById(R.id.cardDeliveryScheduled);
        textDeliveryImmediateTitle = view.findViewById(R.id.textDeliveryImmediateTitle);
        textDeliveryStorePickupTitle = view.findViewById(R.id.textDeliveryStorePickupTitle);
        textDeliveryScheduledTitle = view.findViewById(R.id.textDeliveryScheduledTitle);

        setupCart();
        loadUserPrincipalAddress();
        setupDeliveryOptions();

        buttonCheckout.setOnClickListener(v -> {
            Order newOrder = CartManager.getInstance().createOrderFromCart();
            if (newOrder != null) {
                // Populate order with current cart information
                populateOrderDetails(newOrder);
                
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

    private void loadUserPrincipalAddress() {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null) {
            db.collection("usuarios")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            if (usuario != null && usuario.getEndereco() != null && !usuario.getEndereco().isEmpty()) {
                                int principalIndex = usuario.getEnderecoPrincipal();
                                if (principalIndex >= 0 && principalIndex < usuario.getEndereco().size()) {
                                    Map<String, Object> principalAddress = usuario.getEndereco().get(principalIndex);
                                    displayAddress(principalAddress);
                                } else {
                                    // If principal index is invalid, show first address
                                    displayAddress(usuario.getEndereco().get(0));
                                }
                            } else {
                                // No addresses found, hide delivery address section
                                layoutDeliveryAddress.setVisibility(View.GONE);
                            }
                        } else {
                            // User document doesn't exist, hide delivery address section
                            layoutDeliveryAddress.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error loading user data, hide delivery address section
                        layoutDeliveryAddress.setVisibility(View.GONE);
                    });
        } else {
            // User not authenticated, hide delivery address section
            layoutDeliveryAddress.setVisibility(View.GONE);
        }
    }

    private void displayAddress(Map<String, Object> address) {
        String logradouro = (String) address.get("logradouro");
        String numero = (String) address.get("numero");
        String complemento = (String) address.get("complemento");
        String bairro = (String) address.get("bairro");
        String cidade = (String) address.get("cidade");
        String estado = (String) address.get("estado");
        String cep = (String) address.get("cep");

        String addressLine1 = String.format("%s, %s%s", logradouro, numero, 
            (complemento != null && !complemento.isEmpty() ? " - " + complemento : ""));
        String addressLine2 = String.format("%s, %s - %s, %s", bairro, cidade, estado, cep);

        textDeliveryAddress.setText(String.format("%s\n%s", addressLine1, addressLine2));
        layoutDeliveryAddress.setVisibility(View.VISIBLE);
    }

    private void setupCart() {
        cartItems = CartManager.getInstance().getCartItems();

        if (cartItems.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            layoutMainContent.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            layoutMainContent.setVisibility(View.VISIBLE);

            if (cartAdapter == null) {
                cartAdapter = new CartAdapter(getContext(), cartItems, this);
                recyclerCartItems.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerCartItems.setAdapter(cartAdapter);
            } else {
                cartAdapter.updateCartItems(cartItems);
            }
        }
        updateSummary();
        updatePrescriptionWarning();
    }

    private void updateTotal() {
        updateSummary();
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
        loadUserPrincipalAddress(); // Reload address when returning to fragment
    }

    @Override
    public void onCartUpdated() {
        updateSummary();
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            setupCart(); // Refresh the whole view if cart becomes empty
        } else {
            cartAdapter.updateCartItems(CartManager.getInstance().getCartItems());
        }
    }

    private void setupDeliveryOptions() {
        updateDeliveryCardSelection();
        cardDeliveryImmediate.setOnClickListener(v -> {
            selectedDeliveryOption = "immediate";
            deliveryFee = 7.00;
            updateDeliveryCardSelection();
            updateSummary();
        });
        cardDeliveryStorePickup.setOnClickListener(v -> {
            selectedDeliveryOption = "pickup";
            deliveryFee = 0.00;
            updateDeliveryCardSelection();
            updateSummary();
        });
        cardDeliveryScheduled.setOnClickListener(v -> {
            selectedDeliveryOption = "scheduled";
            deliveryFee = 7.00;
            updateDeliveryCardSelection();
            updateSummary();
        });
    }
    
    private void updateDeliveryCardSelection() {
        cardDeliveryImmediate.setBackgroundResource(selectedDeliveryOption.equals("immediate") ? R.drawable.bg_delivery_option_selected : R.drawable.bg_delivery_option_unselected);
        cardDeliveryStorePickup.setBackgroundResource(selectedDeliveryOption.equals("pickup") ? R.drawable.bg_delivery_option_selected : R.drawable.bg_delivery_option_unselected);
        cardDeliveryScheduled.setBackgroundResource(selectedDeliveryOption.equals("scheduled") ? R.drawable.bg_delivery_option_selected : R.drawable.bg_delivery_option_unselected);
        // Update title color for selected
        textDeliveryImmediateTitle.setTextColor(selectedDeliveryOption.equals("immediate") ? 0xFF1E56A0 : 0xFF1E56A0);
        textDeliveryStorePickupTitle.setTextColor(selectedDeliveryOption.equals("pickup") ? 0xFF1E56A0 : 0xFF1E56A0);
        textDeliveryScheduledTitle.setTextColor(selectedDeliveryOption.equals("scheduled") ? 0xFF1E56A0 : 0xFF1E56A0);
    }

    private void updateSummary() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        
        // Calculate subtotal from cart items
        double subtotal = CartManager.getInstance().getTotalPrice();
        textSubtotal.setText(format.format(subtotal));
        
        // Set delivery fee based on selected option
        textDeliveryFee.setText(format.format(deliveryFee));
        
        // Set discount (currently 0, can be extended for coupon functionality)
        textDiscount.setText(format.format(discountAmount));
        
        // Calculate total
        double total = subtotal + deliveryFee - discountAmount;
        textTotal.setText(format.format(total));
    }

    // Method to apply coupon discount
    public void applyCoupon(double discountValue) {
        this.discountAmount = discountValue;
        updateSummary();
    }

    // Method to remove coupon discount
    public void removeCoupon() {
        this.discountAmount = 0.0;
        updateSummary();
    }
    
    // Method to check and apply coupon
    public boolean applyCouponCode(String couponCode) {
        // This is a placeholder implementation
        // In a real app, you would validate the coupon code against a database
        if ("DESCONTO10".equals(couponCode)) {
            double subtotal = CartManager.getInstance().getTotalPrice();
            this.discountAmount = subtotal * 0.10; // 10% discount
            updateSummary();
            return true;
        } else if ("FREEGRATIS".equals(couponCode)) {
            this.discountAmount = deliveryFee; // Free delivery
            updateSummary();
            return true;
        }
        return false; // Invalid coupon
    }
    
    // Method to get current total
    public double getCurrentTotal() {
        double subtotal = CartManager.getInstance().getTotalPrice();
        return subtotal + deliveryFee - discountAmount;
    }
    
    // Method to get current delivery fee
    public double getCurrentDeliveryFee() {
        return deliveryFee;
    }
    
    // Method to get current discount
    public double getCurrentDiscount() {
        return discountAmount;
    }

    private void populateOrderDetails(Order newOrder) {
        // Set delivery information
        newOrder.setDeliveryOption(selectedDeliveryOption);
        newOrder.setDeliveryFee(deliveryFee);
        
        // Set summary breakdown
        newOrder.setSubtotal(CartManager.getInstance().getTotalPrice());
        newOrder.setDiscountAmount(discountAmount);
        newOrder.setTotalPrice(getCurrentTotal());
        
        // Set payment information (default to pending, can be updated later)
        newOrder.setPaymentMethod("Pendente");
        newOrder.setPaymentStatus("pending");
        
        // Set store information
        newOrder.setStoreName("Drogaria São Paulo");
        newOrder.setStoreLocation("Cidade Campinas");
        
        // Check if prescription is required
        boolean needsPrescription = false;
        for (CartItem item : cartItems) {
            String tarja = item.getProduct().getTarja();
            if (Product.TARJA_PRETA.equals(tarja) || 
                Product.TARJA_VERMELHA_SEM_RETENCAO.equals(tarja) || 
                Product.TARJA_VERMELHA_COM_RETENCAO.equals(tarja)) {
                needsPrescription = true;
                break;
            }
        }
        newOrder.setRequiresPrescription(needsPrescription);
        
        // Set delivery address if available
        if (layoutDeliveryAddress.getVisibility() == View.VISIBLE) {
            // Load user's principal address
            String userId = UsuarioFirebase.getIdUsuario();
            if (userId != null) {
                db.collection("usuarios")
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                if (usuario != null && usuario.getEndereco() != null && !usuario.getEndereco().isEmpty()) {
                                    int principalIndex = usuario.getEnderecoPrincipal();
                                    if (principalIndex >= 0 && principalIndex < usuario.getEndereco().size()) {
                                        Map<String, Object> principalAddress = usuario.getEndereco().get(principalIndex);
                                        newOrder.setDeliveryAddress(principalAddress);
                                    }
                                }
                            }
                        });
            }
        }
        
        // Set estimated delivery time based on delivery option
        if ("immediate".equals(selectedDeliveryOption)) {
            // Add 1 hour for immediate delivery
            newOrder.setEstimatedDeliveryTime(com.google.firebase.Timestamp.now());
        } else if ("scheduled".equals(selectedDeliveryOption)) {
            // Add 24 hours for scheduled delivery
            newOrder.setEstimatedDeliveryTime(com.google.firebase.Timestamp.now());
        }
        // For pickup, no estimated delivery time needed
    }
}