package habilitipro.model.dao;

import habilitipro.model.persistence.Modulo;

import javax.persistence.EntityManager;
import java.util.List;

public class ModuloDAO {

    private EntityManager em;

    public ModuloDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Modulo modulo) {
        this.em.persist(modulo);
    }

    public void delete(Modulo modulo) {
        this.em.remove(convertToMerge(modulo));
    }

    public void update(Modulo modulo) {
        convertToMerge(modulo);
    }

    public List<Modulo> listAll() {
        String jpql = "SELECT m FROM Modulo AS m";
        return this.em.
                createQuery(jpql,Modulo.class)
                .getResultList();
    }

    public List<Modulo> listByName(String nome) {
        String jpql = "SELECT m FROM Modulo AS m WHERE m.nome=:nome";
        return this.em.
                createQuery(jpql,Modulo.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Modulo findByName(String nome) {
        String jpql = "SELECT m FROM Modulo AS m WHERE m.nome=:nome";
        return this.em.
                createQuery(jpql,Modulo.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Modulo getById(Long id) {
        return this.em.find(Modulo.class,id);
    }

    public Modulo convertToMerge(Modulo modulo) {
        return this.em.merge(modulo);
    }
}
