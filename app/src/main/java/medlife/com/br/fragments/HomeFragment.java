package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseUser;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;

public class HomeFragment extends Fragment {
    private TextView welcomeText;
    private TextView subtitleText;
    private FirebaseUser usuarioAtual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        welcomeText = view.findViewById(R.id.welcomeText);
        subtitleText = view.findViewById(R.id.subtitleText);
        

        // Get current user and update welcome message
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        if (usuarioAtual != null && usuarioAtual.getDisplayName() != null) {
            String nomeUsuario = usuarioAtual.getDisplayName();
            welcomeText.setText("Bem-vindo(a), " + nomeUsuario + "!");
        }
        
        return view;
    }
}