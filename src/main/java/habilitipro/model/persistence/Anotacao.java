package habilitipro.model.persistence;

import javax.persistence.*;

@Entity
public class Anotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String texto;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "trabalhador_id")
    private Trabalhador trabalhador;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "modulo_id")
    private Modulo modulo;

    public Anotacao(){}

    public Anotacao(String texto, Trabalhador trabalhador, Modulo modulo) {
        this.texto = texto.toLowerCase();
        this.trabalhador = trabalhador;
        this.modulo = modulo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Trabalhador getTrabalhador() {
        return trabalhador;
    }

    public void setTrabalhador(Trabalhador trabalhador) {
        this.trabalhador = trabalhador;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    @Override
    public String toString() {
        return "Anotacao{" +
                "id=" + id +
                ", texto='" + texto + '\'' +
                ", trabalhador=" + trabalhador +
                ", modulo=" + modulo +
                '}';
    }
}
