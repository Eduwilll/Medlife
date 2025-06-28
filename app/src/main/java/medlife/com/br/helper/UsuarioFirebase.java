package medlife.com.br.helper;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsuarioFirebase {

    public static String getIdUsuario(){

        FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return autenticacao.getCurrentUser().getUid();

    }

    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return  usuario.getCurrentUser();
    }

    public static  boolean atualizarTipoUsuario(String tipo){
        try {
            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(tipo)
                    .build();
            user.updateProfile(profile);
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update the lastLogin timestamp for the current user
     */
    public static void atualizarLastLogin() {
        FirebaseUser currentUser = getUsuarioAtual();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(currentUser.getUid())
                    .update("lastLogin", com.google.firebase.Timestamp.now())
                    .addOnFailureListener(e -> {
                        // Log error but don't block the app
                        e.printStackTrace();
                    });
        }
    }
    
    /**
     * Update the lastLogin timestamp for a specific user
     */
    public static void atualizarLastLogin(String userId) {
        if (userId != null && !userId.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(userId)
                    .update("lastLogin", com.google.firebase.Timestamp.now())
                    .addOnFailureListener(e -> {
                        // Log error but don't block the app
                        e.printStackTrace();
                    });
        }
    }

}
