package habilitipro.model.persistence;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Trabalhador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,unique = true)
    private String cpf;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "empresa_id")
    private Empresa empresa;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "setor_id")
    private Setor setor;

    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "funcao_id")
    private Funcao funcao;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinTable(
            name = "Trabalhador_Trilha",
            joinColumns = {@JoinColumn(name = "trabalhador_id")},
            inverseJoinColumns = {@JoinColumn(name = "trilha_id")}
    )
    private Set<Trilha> trilhas = new HashSet<>();

    @Column(nullable = false)
    private OffsetDateTime dataAlteracaoDaFuncao;

    public Trabalhador() {}

    public Trabalhador(String nome, String cpf, Empresa empresa, Setor setor, Funcao funcao, Set<Trilha> trilha) {
        this.nome = nome.toLowerCase();
        this.cpf = cpf;
        this.empresa = empresa;
        this.setor = setor;
        this.funcao = funcao;
        this.trilhas = trilha;
        this.dataAlteracaoDaFuncao = OffsetDateTime.now();
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public Set<Trilha> getTrilha() {
        return trilhas;
    }

    public void setTrilha(Set<Trilha> trilha) {
        this.trilhas = trilha;
    }

    public OffsetDateTime getDataAlteracaoDaFuncao() {
        return dataAlteracaoDaFuncao;
    }

    public void setDataAlteracaoDaFuncao(OffsetDateTime dataAlteracaoDaFuncao) {
        this.dataAlteracaoDaFuncao = dataAlteracaoDaFuncao;
    }

    @Override
    public String toString() {
        return "Trabalhador{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", CPF='" + cpf + '\'' +
                ", empresa=" + empresa +
                ", setor=" + setor +
                ", funcao=" + funcao +
                ", trilha=" + trilhas +
                ", dataAlteracaoDaFuncao=" + dataAlteracaoDaFuncao +
                '}';
    }
}
