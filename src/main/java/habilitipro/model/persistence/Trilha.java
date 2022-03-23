package habilitipro.model.persistence;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(nullable = false, name = "empresa_id")
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Empresa empresa;

    @JoinColumn(nullable = false,name = "ocupacao_id")
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.REFRESH,
            CascadeType.MERGE,CascadeType.DETACH},fetch = FetchType.LAZY)
    private Ocupacao ocupacao;

    @Column(unique = true)
    private String nome;

    @Column(unique = true)
    private String apelido;

    private String anotacoes;

    private int nivelSatisfacaoGeral;

    @Transient
    private static List<Trilha> trilhas = new ArrayList<>();

    @ManyToMany(mappedBy = "trilhas")
    private Set<Trabalhador> trabalhadores = new HashSet<>();

    public Trilha() {}

    public Trilha(Empresa empresa, Ocupacao ocupacao) {
        this.empresa = empresa;
        this.ocupacao = ocupacao;
        setNome();
        setApelido();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Ocupacao getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(Ocupacao ocupacao) {
        this.ocupacao = ocupacao;
    }

    private int getNumeroSequencial() {
        int length = trilhas.stream()
                .filter(t -> t.empresa == this.empresa && t.ocupacao.getNome().equals(this.ocupacao.getNome()))
                .toArray().length;
        return length>0?length+1:1;
    }

    public String getNome() {
        return nome;
    }

    private void setNome() {
        this.nome = this.ocupacao.getNome()+this.empresa.getNome()+getNumeroSequencial()+ LocalDate.now().getYear();
    }

    public String getApelido() {
        return apelido;
    }

    private void setApelido() {
        this.apelido = this.ocupacao.getNome()+getNumeroSequencial();
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    @Override
    public String toString() {
        return "Trilha{" +
                "id=" + id +
                ", empresa=" + empresa +
                ", ocupacao='" + ocupacao + '\'' +
                ", nome='" + nome + '\'' +
                ", apelido='" + apelido + '\'' +
                ", anotacoes='" + anotacoes + '\'' +
                '}';
    }
}
