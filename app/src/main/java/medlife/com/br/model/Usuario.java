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
    private String otherGender;
    private String phoneNumber;
    private String dateOfBirth;
    private String cpf;
    private List<Map<String, Object>> endereco;
    private List<Order> orders;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public List<Map<String, Object>> getEndereco() {
        return endereco;
    }

    public void setEndereco(List<Map<String, Object>> endereco) {
        this.endereco = endereco;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Timestamp getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Timestamp criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getOtherGender() { return otherGender; }

    public void setOtherGender(String outroGenero) { this.otherGender = outroGenero; }
}