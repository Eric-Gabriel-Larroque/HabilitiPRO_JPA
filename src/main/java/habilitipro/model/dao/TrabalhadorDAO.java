package habilitipro.model.dao;

import habilitipro.model.persistence.Funcao;
import habilitipro.model.persistence.Trabalhador;

import javax.persistence.EntityManager;
import java.util.List;

public class TrabalhadorDAO {
    private EntityManager em;

    public TrabalhadorDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Trabalhador trabalhador) {
        this.em.persist(trabalhador);
    }

    public void delete(Trabalhador trabalhador) {
        this.em.remove(convertToMerge(trabalhador));
    }

    public void update(Trabalhador trabalhador) {
        convertToMerge(trabalhador);
    }

    public List<Trabalhador> listAll() {
        String jpql = "SELECT f FROM Funcao AS f";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .getResultList();
    }

    public List<Trabalhador> listByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Trabalhador findByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Trabalhador getById(Long id) {
        return this.em.find(Trabalhador.class,id);
    }

    public Trabalhador convertToMerge(Trabalhador trabalhador) {
        return this.em.merge(trabalhador);
    }
}
