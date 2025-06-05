package medlife.com.br.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import medlife.com.br.R;
import medlife.com.br.activity.AutenticacaoActivity;
import medlife.com.br.helper.ConfiguracaoFirebase;

public class ProfileFragment extends Fragment {

    private ImageView imageProfile;
    private TextView textName, textEmail;
    private View menuPedidos, menuMeusDados, menuEnderecos, menuCarteira, 
                menuCupons, menuNotificacoes, menuAjuda, menuSobre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize views
        imageProfile = view.findViewById(R.id.imageProfile);
        textName = view.findViewById(R.id.textName);
        textEmail = view.findViewById(R.id.textEmail);

        // Initialize menu items
        menuPedidos = view.findViewById(R.id.menuPedidos);
        menuMeusDados = view.findViewById(R.id.menuMeusDados);
        menuEnderecos = view.findViewById(R.id.menuEnderecos);
        menuCarteira = view.findViewById(R.id.menuCarteira);
        menuCupons = view.findViewById(R.id.menuCupons);
        menuNotificacoes = view.findViewById(R.id.menuNotificacoes);
        menuAjuda = view.findViewById(R.id.menuAjuda);
        menuSobre = view.findViewById(R.id.menuSobre);

        // Set click listeners
        setupClickListeners();

        // Load user info
//        loadUserInfo();

        return view;
    }

    private void setupClickListeners() {
        menuPedidos.setOnClickListener(v -> {
            // TODO: Navigate to Orders screen
            Toast.makeText(getContext(), "Pedidos", Toast.LENGTH_SHORT).show();
        });

        menuMeusDados.setOnClickListener(v -> {
            // TODO: Navigate to User Data screen
            Toast.makeText(getContext(), "Meus Dados", Toast.LENGTH_SHORT).show();
        });

        menuEnderecos.setOnClickListener(v -> {
            // TODO: Navigate to Addresses screen
            Toast.makeText(getContext(), "Endereços", Toast.LENGTH_SHORT).show();
        });

        menuCarteira.setOnClickListener(v -> {
            // TODO: Navigate to Wallet screen
            Toast.makeText(getContext(), "Carteira", Toast.LENGTH_SHORT).show();
        });

        menuCupons.setOnClickListener(v -> {
            // TODO: Navigate to Coupons screen
            Toast.makeText(getContext(), "Cupons", Toast.LENGTH_SHORT).show();
        });

        menuNotificacoes.setOnClickListener(v -> {
            // TODO: Navigate to Notifications screen
            Toast.makeText(getContext(), "Notificações", Toast.LENGTH_SHORT).show();
        });

        menuAjuda.setOnClickListener(v -> {
            // TODO: Navigate to Help screen
            Toast.makeText(getContext(), "Ajuda", Toast.LENGTH_SHORT).show();
        });

        menuSobre.setOnClickListener(v -> {
            // TODO: Navigate to About screen
            Toast.makeText(getContext(), "Sobre", Toast.LENGTH_SHORT).show();
        });
    }

//    private void loadUserInfo() {
//        FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
//        if (user != null) {
//            textName.setText(user.getDisplayName() != null ? user.getDisplayName() : "Usuário");
//            textEmail.setText(user.getEmail());
//
//            if (user.getPhotoUrl() != null) {
//                Glide.with(this)
//                    .load(user.getPhotoUrl())
//                    .placeholder(R.drawable.ic_profile_placeholder)
//                    .into(imageProfile);
//            } else {
//                imageProfile.setImageResource(R.drawable.ic_profile_placeholder);
//            }
//        }
//    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View sairButton = view.findViewById(R.id.sairButton);
        if (sairButton != null) {
            sairButton.setOnClickListener(v -> {
                ConfiguracaoFirebase.getFirebaseAutenticacao().signOut();
                Intent intent = new Intent(getActivity(), AutenticacaoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }
}