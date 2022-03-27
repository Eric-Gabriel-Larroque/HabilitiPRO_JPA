package habilitipro.model.persistence;

import javax.persistence.*;


@Entity
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int nota;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "trabalhador_id")
    private Trabalhador trabalhador;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "modulo_id")
    private Modulo modulo;

    public Score(){}

    public Score(int nota, Trabalhador trabalhador, Modulo modulo) {
        this.nota = nota;
        this.trabalhador = trabalhador;
        this.modulo = modulo;
    }

    public long getId() {
        return id;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
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
        return "Score{" +
                "id=" + id +
                ", score=" + nota +
                ", trabalhador=" + trabalhador +
                ", modulo=" + modulo +
                '}';
    }
}
