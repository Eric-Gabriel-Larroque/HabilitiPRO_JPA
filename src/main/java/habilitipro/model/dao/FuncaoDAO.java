package habilitipro.model.dao;

import habilitipro.model.persistence.Funcao;

import javax.persistence.EntityManager;
import java.util.List;

public class FuncaoDAO {

    private EntityManager em;

    public FuncaoDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Funcao funcao) {
        this.em.persist(funcao);
    }

    public void delete(Funcao funcao) {
        this.em.remove(convertToMerge(funcao));
    }

    public void update(Funcao funcao) {
        convertToMerge(funcao);
    }

    public List<Funcao> listAll() {
        String jpql = "SELECT f FROM Funcao AS f";
        return this.em.
                createQuery(jpql,Funcao.class)
                .getResultList();
    }

    public List<Funcao> listByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Funcao.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Funcao findByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Funcao.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Funcao getById(Long id) {
        return this.em.find(Funcao.class,id);
    }

    public Funcao convertToMerge(Funcao funcao) {
        return this.em.merge(funcao);
    }
}
