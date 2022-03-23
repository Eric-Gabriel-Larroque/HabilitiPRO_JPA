package habilitipro.model.dao;

import habilitipro.model.persistence.Funcao;
import habilitipro.model.persistence.Setor;

import javax.persistence.EntityManager;
import java.util.List;

public class SetorDAO {

    private EntityManager em;

    public SetorDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Setor setor) {
        this.em.persist(setor);
    }

    public void delete(Setor setor) {
        this.em.remove(convertToMerge(setor));
    }

    public void update(Setor setor) {
        convertToMerge(setor);
    }

    public List<Setor> listAll() {
        String jpql = "SELECT s FROM Setor AS s";
        return this.em.
                createQuery(jpql,Setor.class)
                .getResultList();
    }

    public List<Setor> listByName(String nome) {
        String jpql = "SELECT s FROM Setor AS s WHERE s.nome=:nome";
        return this.em.
                createQuery(jpql,Setor.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Setor findByName(String nome) {
        String jpql = "SELECT s FROM Setor AS s WHERE s.nome=:nome";
        return this.em.
                createQuery(jpql,Setor.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Setor getById(Long id) {
        return this.em.find(Setor.class,id);
    }

    public Setor convertToMerge(Setor setor) {
        return this.em.merge(setor);
    }
}
