package habilitipro.model.persistence;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Trilha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JoinColumn(nullable = false, name = "empresa_id")
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,
            CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.LAZY)
    private Empresa empresa;

    @Column(nullable = false)
    private Ocupacao ocupacao;

    @Column(unique = true)
    private String nome;

    @Column(unique = true)
    private String apelido;

    private String anotacoes;

    private List<Trilha> listaTrilhas = new ArrayList<>();

    public Trilha() {}

    public Trilha(Empresa empresa, Ocupacao ocupacao) {
        this.empresa = empresa;
        this.ocupacao = ocupacao;
        setNome();
        setApelido();
    }

    private int getNumeroSequencial() {
        int length = listaTrilhas.stream()
                .filter(t -> t.empresa == this.empresa && t.ocupacao.equals(this.ocupacao))
                .toArray().length;
        return length>0?length+1:1;
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

    public void setNome() {
        this.nome = this.ocupacao+this.empresa.getNome()+getNumeroSequencial()+ LocalDate.now().getYear();
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido() {
        this.apelido = getNumeroSequencial()+this.ocupacao.getNome();
    }

    public String getAnotacoes() {
        return anotacoes;
    }

    public void setAnotacoes(String anotacoes) {
        this.anotacoes = anotacoes;
    }

    public List<Trilha> getListaTrilhas() {
        return listaTrilhas;
    }

    public void setListaTrilhas(List<Trilha> listaTrilhas) {
        this.listaTrilhas = listaTrilhas;
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
                ", listaTrilhas=" + listaTrilhas +
                '}';
    }
}
