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


    /**
     * Update the lastLogin timestamp for the current user
     */
    public static void atualizarLastLogin() {
        FirebaseUser currentUser = getUsuarioAtual();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("usuarios").document(currentUser.getUid())
                    .update("lastLogin", com.google.firebase.Timestamp.now())
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("LastLogin updated successfully for user: " + currentUser.getUid());
                    })
                    .addOnFailureListener(e -> {
                        // Log error but don't block the app
                        System.out.println("Error updating lastLogin: " + e.getMessage());
                        e.printStackTrace();
                    });
        } else {
            System.out.println("No authenticated user found for lastLogin update");
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
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("LastLogin updated successfully for user: " + userId);
                    })
                    .addOnFailureListener(e -> {
                        // Log error but don't block the app
                        System.out.println("Error updating lastLogin for user " + userId + ": " + e.getMessage());
                        e.printStackTrace();
                    });
        } else {
            System.out.println("Invalid userId for lastLogin update: " + userId);
        }
    }

    /**
     * Safely update lastLogin without overwriting other user data
     */
    public static void atualizarLastLoginSeguro(String userId) {
        if (userId != null && !userId.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            
            // First check if user document exists
            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // User exists, safely update only lastLogin
                            db.collection("usuarios").document(userId)
                                    .update("lastLogin", com.google.firebase.Timestamp.now())
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("LastLogin updated safely for user: " + userId);
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("Error updating lastLogin safely: " + e.getMessage());
                                    });
                        } else {
                            System.out.println("User document doesn't exist for lastLogin update: " + userId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        System.out.println("Error checking user document for lastLogin update: " + e.getMessage());
                    });
        } else {
            System.out.println("Invalid userId for safe lastLogin update: " + userId);
        }
    }

}
