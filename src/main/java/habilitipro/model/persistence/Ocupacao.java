package habilitipro.model.persistence;

import javax.persistence.*;

@Entity
public class Ocupacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String nome;

    public Ocupacao() {
    }

    public Ocupacao(String nome) {
        this.nome = nome.toLowerCase();
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

    @Override
    public String toString() {
        return "Ocupacao{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
