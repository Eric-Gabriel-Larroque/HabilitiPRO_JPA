package habilitipro.model.persistence;

import habilitipro.enums.Regional;
import habilitipro.enums.Segmento;

import javax.persistence.*;

@Entity
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false,unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private boolean matriz;

    private String nomeFilial;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Segmento segmento;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String estado;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Regional regional;
    public Empresa() {}

    public Empresa(String nome, String cnpj, Segmento segmento, String cidade, String estado, Regional regional, boolean matriz, String nomeFilial){
        this.nome = nome.toLowerCase();
        this.cnpj = cnpj;
        this.segmento = segmento;
        this.cidade = cidade.toLowerCase();
        this.estado = estado.toLowerCase();
        this.regional = regional;
        this.matriz = matriz;
        this.nomeFilial = nomeFilial.toLowerCase();
        setNomeFilial(this.nomeFilial);
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
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isMatriz() {
        return matriz;
    }

    public void setMatriz(boolean matriz) {
        this.matriz = matriz;
    }

    public String getNomeFilial() {
        return nomeFilial;
    }

    public void setNomeFilial(String nomeFilial) {
        if(!isMatriz()) {
            this.nomeFilial = nomeFilial;
        } else {
            this.nomeFilial = "";
        }
    }

    public Segmento getSegmento() {
        return segmento;
    }

    public void setSegmento(Segmento segmento) {
        this.segmento = segmento;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Regional getRegional() {
        return regional;
    }

    public void setRegional(Regional regional) {
        this.regional = regional;
    }

    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", CNPJ='" + cnpj + '\'' +
                ", nomeFilial='" + nomeFilial + '\'' +
                ", segmento=" + segmento +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                ", regional=" + regional +
                '}';
    }
}
