package habilitipro.model.persistence;

import javax.persistence.*;


@Entity
public class HistoricoTrabalhador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String trabalhadorRegistro;

    @OneToOne(cascade = {CascadeType.ALL},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "trabalhador_id")
    private Trabalhador trabalhador;

    public HistoricoTrabalhador() {
    }

    public HistoricoTrabalhador(String trabalhadorRegistro, Trabalhador trabalhador) {
        this.trabalhadorRegistro = trabalhadorRegistro;
        this.trabalhador = trabalhador;
    }


    public long getId() {
        return id;
    }

    public String getTrabalhadorRegistro() {
        return trabalhadorRegistro;
    }

    public void setTrabalhadorRegistro(String trabalhadorRegistro) {
        this.trabalhadorRegistro = trabalhadorRegistro;
    }

    public Trabalhador getTrabalhador() {
        return trabalhador;
    }

    public void setTrabalhador(Trabalhador trabalhador) {
        this.trabalhador = trabalhador;
    }

    @Override
    public String toString() {
        return "HistoricoTrabalhador{" +
                "id=" + id +
                ", trabalhadorRegistro='" + trabalhadorRegistro + '\'' +
                ", trabalhador=" + trabalhador +
                '}';
    }
}
