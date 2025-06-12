package medlife.com.br.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.List;
import java.util.Map;

public class Usuario {
    private String uid;
    private String nome;
    private String email;
    private String genero;
    private List<String> telefone;
    private List<Map<String, Object>> endereco;
    @ServerTimestamp
    private Timestamp criadoEm;

    public Usuario() {
        // Required empty constructor for Firestore
    }

    public Usuario(String uid, String nome, String email) {
        this.uid = uid;
        this.nome = nome;
        this.email = email;
    }

    // Getters and Setters
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public List<String> getTelefone() {
        return telefone;
    }

    public void setTelefone(List<String> telefone) {
        this.telefone = telefone;
    }

    public List<Map<String, Object>> getEndereco() {
        return endereco;
    }

    public void setEndereco(List<Map<String, Object>> endereco) {
        this.endereco = endereco;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }
} 