package habilitipro.model.dao;

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
        String jpql = "SELECT t FROM Trabalhador AS t";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .getResultList();
    }

    public List<Trabalhador> listByName(String nome) {
        String jpql = "SELECT t FROM Trabalhador AS t WHERE t.nome=:nome";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Trabalhador findByCpf(String cpf) {
        String jpql = "SELECT t FROM Trabalhador AS t WHERE t.cpf=:cpf";
        return this.em.
                createQuery(jpql,Trabalhador.class)
                .setParameter("cpf",cpf)
                .getSingleResult();
    }

    public Trabalhador getById(Long id) {
        return this.em.find(Trabalhador.class,id);
    }

    public Trabalhador convertToMerge(Trabalhador trabalhador) {
        return this.em.merge(trabalhador);
    }
}
