package habilitipro.model.persistence;

import habilitipro.enums.Status;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
    CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "trilha_id")
    private Trilha trilha;

    // Nome único para evitar duplicatas
    @Column(nullable = false,unique = true)
    private String nome;

    @Column(nullable = false)
    private String habilidadesTrabalhadas;

    @Column(nullable = false)
    private String tarefaDeValidacao;

    @Column(nullable = false)
    private int prazoLimite;

    private OffsetDateTime dataInicio;

    private OffsetDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToMany(mappedBy = "modulos")
    private Set<Trabalhador> trabalhadores = new HashSet<>();

    public Modulo() {}

    public Modulo(Trilha trilha,String nome,String habilidadesTrabalhadas,String tarefaDeValidacao, int prazoLimite) {
        this.trilha = trilha;
        this.nome = nome.toLowerCase();
        this.habilidadesTrabalhadas = habilidadesTrabalhadas;
        this.tarefaDeValidacao = tarefaDeValidacao;
        this.prazoLimite = prazoLimite;
        this.status = Status.NAOINICIADO;
        this.dataInicio = null;
        this.dataFim = null;
        setPrazoLimite(this.prazoLimite);
    }

    public long getId() {
        return id;
    }

    public Trilha getTrilha() {
        return trilha;
    }

    public void setTrilha(Trilha trilha) {
        this.trilha = trilha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getHabilidadesTrabalhadas() {
        return habilidadesTrabalhadas;
    }

    public void setHabilidadesTrabalhadas(String habilidadesTrabalhadas) {
        this.habilidadesTrabalhadas = habilidadesTrabalhadas;
    }

    public String getTarefaDeValidacao() {
        return tarefaDeValidacao;
    }

    public void setTarefaDeValidacao(String tarefaDeValidacao) {
        this.tarefaDeValidacao = tarefaDeValidacao;
    }

    public int getPrazoLimite() {
        return prazoLimite;
    }

    public void setPrazoLimite(int prazoLimite) {
        if(prazoLimite<=0) {
            this.prazoLimite = 10;
        }else {
            this.prazoLimite = prazoLimite;
        }
    }

    public OffsetDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(OffsetDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public OffsetDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(OffsetDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Modulo{" +
                "id=" + id +
                ", trilha=" + trilha +
                ", nome='" + nome + '\'' +
                ", habilidadesTrabalhadas='" + habilidadesTrabalhadas + '\'' +
                ", tarefaDeValidacao='" + tarefaDeValidacao + '\'' +
                ", prazoLimite=" + prazoLimite +
                ", dataInicio=" + dataInicio +
                ", dataFIm=" + dataFim +
                ", status=" + status +
                '}';
    }
}
