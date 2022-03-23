package habilitipro.model.dao;

import habilitipro.model.persistence.Ocupacao;

import javax.persistence.EntityManager;
import java.util.List;

public class OcupacaoDAO {

    private EntityManager em;

    public OcupacaoDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Ocupacao ocupacao) {
        this.em.persist(ocupacao);
    }

    public void delete(Ocupacao ocupacao) {
        this.em.remove(convertToMerge(ocupacao));
    }

    public void update(Ocupacao ocupacao) {
        convertToMerge(ocupacao);
    }

    public List<Ocupacao> listAll() {
        String jpql = "SELECT o FROM Ocupacao AS o";
        return this.em.
                createQuery(jpql,Ocupacao.class)
                .getResultList();
    }

    public List<Ocupacao> listByName(String nome) {
        String jpql = "SELECT o FROM Ocupacao AS o WHERE o.nome=:nome";
        return this.em.
                createQuery(jpql,Ocupacao.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Ocupacao findByName(String nome) {
        String jpql = "SELECT o FROM Ocupacao AS o WHERE o.nome=:nome";
        return this.em.
                createQuery(jpql,Ocupacao.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Ocupacao getById(Long id) {
        return this.em.find(Ocupacao.class,id);
    }

    public Ocupacao convertToMerge(Ocupacao ocupacao) {
        return this.em.merge(ocupacao);
    }

}
