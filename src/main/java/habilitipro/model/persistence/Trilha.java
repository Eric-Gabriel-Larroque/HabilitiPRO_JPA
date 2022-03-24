package habilitipro.model.persistence;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Columns;

import javax.persistence.*;
import java.beans.BeanProperty;
import java.util.HashSet;
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

    private String apelido;

    private String anotacoes;

    private int nivelSatisfacaoGeral;

    @ManyToMany(mappedBy = "trilhas")
    private Set<Trabalhador> trabalhadores = new HashSet<>();

    public Trilha() {}

    public Trilha(Empresa empresa, Ocupacao ocupacao) {
        this.empresa = empresa;
        this.ocupacao = ocupacao;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }
    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public int getNivelSatisfacaoGeral() {
        return nivelSatisfacaoGeral;
    }

    public void setNivelSatisfacaoGeral(int nivelSatisfacaoGeral) {
       this.nivelSatisfacaoGeral = nivelSatisfacaoGeral;
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
                ", nivelSatisfacaoGeral='" + nivelSatisfacaoGeral + '\'' +
                '}';
    }
}
