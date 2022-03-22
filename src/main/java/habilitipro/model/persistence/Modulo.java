package habilitipro.model.persistence;

import habilitipro.enums.Status;

import javax.persistence.*;

@Entity
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
    CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "trilha_id")
    private Trilha trilha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String habilidadesTrabalhadas;

    @Column(nullable = false)
    private String tarefaDeValidacao;

    @Column(nullable = false)
    private int prazoLimite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    public Modulo() {}

    public Modulo(Trilha trilha,String nome,String habilidadesTrabalhadas,String tarefaDeValidacao, int prazoLimite) {
        this.trilha = trilha;
        this.nome = nome.toLowerCase();
        this.habilidadesTrabalhadas = habilidadesTrabalhadas;
        this.tarefaDeValidacao = tarefaDeValidacao;
        this.prazoLimite = prazoLimite;
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
        this.nome = nome.toLowerCase();
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
        this.prazoLimite = prazoLimite;
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
                "trilha=" + trilha +
                ", nome='" + nome + '\'' +
                ", habilidadesTrabalhadas='" + habilidadesTrabalhadas + '\'' +
                ", tarefaDeValidacao='" + tarefaDeValidacao + '\'' +
                ", prazoLimite=" + prazoLimite +
                ", status='" + status + '\'' +
                '}';
    }
}
