package habilitipro.model.dao;

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
        String jpql = "SELECT t FROM Trilha AS t";
        return this.em.
                createQuery(jpql,Trilha.class)
                .getResultList();
    }

    public List<Trilha> listByApelido(String apelido) {
        String jpql = "SELECT t FROM Trilha AS t WHERE t.apelido=:apelido";
        return this.em.
                createQuery(jpql,Trilha.class)
                .setParameter("apelido",apelido)
                .getResultList();
    }

    public Trilha findByName(String nome) {
        String jpql = "SELECT t FROM Trilha AS t WHERE t.nome=:nome";
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
