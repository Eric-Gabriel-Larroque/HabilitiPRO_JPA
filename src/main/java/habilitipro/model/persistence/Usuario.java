package habilitipro.model.persistence;

import habilitipro.enums.Perfis;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Perfis> perfisDeAcesso;

    public Usuario(){}

    public Usuario(String nome, String cpf, String email, String senha,Set<Perfis> perfisDeAcesso){
        this.nome = nome.toLowerCase();
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfisDeAcesso = perfisDeAcesso;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toLowerCase();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Set<Perfis> getPerfisDeAcesso() {
        return perfisDeAcesso;
    }

    public void setPerfisDeAcesso(Set<Perfis> perfisDeAcesso) {
        this.perfisDeAcesso = perfisDeAcesso;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", perfisDeAcesso=" + perfisDeAcesso +
                '}';
    }
}
