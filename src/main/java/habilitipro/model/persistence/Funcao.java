package habilitipro.model.persistence;

import javax.persistence.*;

@Entity
public class Funcao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nome;

    public Funcao() {
    }

    public Funcao(String nome) {
        this.nome = nome.toLowerCase();
    }

    public long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toLowerCase();
    }

    @Override
    public String toString() {
        return "Funcao{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
