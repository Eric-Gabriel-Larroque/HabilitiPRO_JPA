package habilitipro.model.dao;

import habilitipro.model.persistence.Trabalhador;
import habilitipro.model.persistence.Trilha;

import javax.persistence.EntityManager;
import java.util.List;

public class TrilhaDAO {
    private EntityManager em;

    public TrilhaDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Trilha trilha) {
        this.em.persist(trilha);
    }

    public void delete(Trilha trilha) {
        this.em.remove(convertToMerge(trilha));
    }

    public void update(Trilha trilha) {
        convertToMerge(trilha);
    }

    public List<Trilha> listAll() {
        String jpql = "SELECT f FROM Funcao AS f";
        return this.em.
                createQuery(jpql,Trilha.class)
                .getResultList();
    }

    public List<Trilha> listByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Trilha.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Trilha findByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Trilha.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Trilha getById(Long id) {
        return this.em.find(Trilha.class,id);
    }

    public Trilha convertToMerge(Trilha trilha) {
        return this.em.merge(trilha);
    }
}
