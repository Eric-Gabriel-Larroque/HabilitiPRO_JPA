package habilitipro.model.dao;

import habilitipro.model.persistence.Anotacao;

import javax.persistence.EntityManager;
import java.util.List;


//Nesse contexto não faz sentido listar pelo 'texto'
//Sendo que é uma anotação passível de repetição
public class AnotacaoDAO {

    private EntityManager em;

    public AnotacaoDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Anotacao anotacao) {
        this.em.persist(anotacao);
    }

    public void delete(Anotacao anotacao) {
        this.em.remove(convertToMerge(anotacao));
    }

    public void update(Anotacao anotacao) {
        convertToMerge(anotacao);
    }

    public List<Anotacao> listAll() {
        String jpql = "SELECT a FROM Anotacao AS a";
        return this.em.
                createQuery(jpql,Anotacao.class)
                .getResultList();
    }

    public Anotacao findByTexto(String texto) {
        String jpql = "SELECT a FROM Anotacao AS a WHERE a.texto=:texto";
        return this.em.
                createQuery(jpql,Anotacao.class)
                .setParameter("texto",texto)
                .getSingleResult();
    }

    public Anotacao getById(Long id) {
        return this.em.find(Anotacao.class,id);
    }

    public Anotacao convertToMerge(Anotacao anotacao) {
        return this.em.merge(anotacao);
    }
}
