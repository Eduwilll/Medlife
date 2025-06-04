package medlife.com.br.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuProvider; // Import MenuProvider
import androidx.lifecycle.Lifecycle; // Import Lifecycle
import androidx.lifecycle.LifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;

import medlife.com.br.R;
import medlife.com.br.helper.ConfiguracaoFirebase;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);

        // Add menu items without overriding MenuActivity methods
        addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_empresa, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                // Handle option Menu Here
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menuSair) {
                    deslogarUsuario();
                    return true;
                } else if (itemId == R.id.menuConfiguracoes) {
                    abrirConfiguracoes();
                    return true;
                } else if (itemId == R.id.menuNovoProduto) {
                    abrirNovoProduto();
                    return true;
                }
                return false; // Return false if the item was not handled
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED); // Observe lifecycle
    }

    private LifecycleOwner getViewLifecycleOwner() {
        return this;
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish(); //  Consider navigating the user away or finishing the activity
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}