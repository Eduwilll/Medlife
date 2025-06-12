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
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.activity.AutenticacaoActivity;
import medlife.com.br.helper.ConfiguracaoFirebase;
import medlife.com.br.model.Usuario;
import medlife.com.br.fragments.profile.*;

public class ProfileFragment extends Fragment {

    private ImageView imageProfile;
    private TextView textName, textEmail;
    private View menuPedidos, menuMeusDados, menuEnderecos, menuCarteira, 
                menuCupons, menuNotificacoes, menuAjuda, menuSobre;
    private FirebaseFirestore db;
    private Usuario usuarioAtual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

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
        loadUserInfo();

        return view;
    }

    private void loadUserInfo() {
        FirebaseUser user = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser();
        if (user != null) {
            db.collection("usuarios")
                    .document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                usuarioAtual = document.toObject(Usuario.class);
                                if (usuarioAtual != null) {
                                    textName.setText(usuarioAtual.getNome());
                                    textEmail.setText(usuarioAtual.getEmail());
                                }
                            }
                        } else {
                            Toast.makeText(getContext(),
                                    "Erro ao carregar dados do usuário: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setupClickListeners() {
        menuPedidos.setOnClickListener(v -> {
            replaceFragment(new ProfileOrdersFragment());
        });

        menuMeusDados.setOnClickListener(v -> {
            replaceFragment(new ProfileUserDataFragment());
        });

        menuEnderecos.setOnClickListener(v -> {
            replaceFragment(new ProfileAddressesFragment());
        });

        menuCarteira.setOnClickListener(v -> {
            replaceFragment(new ProfileWalletFragment());
        });

        menuCupons.setOnClickListener(v -> {
            replaceFragment(new ProfileCouponsFragment());
        });

        menuNotificacoes.setOnClickListener(v -> {
            replaceFragment(new ProfileNotificationsFragment());
        });

        menuAjuda.setOnClickListener(v -> {
            replaceFragment(new ProfileHelpFragment());
        });

        menuSobre.setOnClickListener(v -> {
            replaceFragment(new ProfileAboutFragment());
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFrame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

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