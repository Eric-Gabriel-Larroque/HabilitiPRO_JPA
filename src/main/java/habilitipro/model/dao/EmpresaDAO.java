package habilitipro.model.dao;

import habilitipro.model.persistence.Empresa;

import javax.persistence.EntityManager;
import java.util.List;

public class EmpresaDAO {

    private EntityManager em;

    public EmpresaDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Empresa empresa) {
        this.em.persist(empresa);
    }

    public void delete(Empresa empresa) {
        this.em.remove(convertToMerge(empresa));
    }

    public void udpate(Empresa empresa) {
        convertToMerge(empresa);
    }

    public List<Empresa> listAll() {
        String jpql = "SELECT e FROM Empresa AS e";
        return this.em
                .createQuery(jpql,Empresa.class)
                .getResultList();
    }

    public List<Empresa> listByName(String nome) {
        String jpql = "SELECT e FROM Empresa AS e WHERE e.nome =:nome";
        return this.em.
                createQuery(jpql,Empresa.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Empresa findByName(String nome) {
        String jpql = "SELECT e FROM Empresa AS e WHERE e.nome =:nome";

        return this.em.
                createQuery(jpql,Empresa.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Empresa getById(Long id) {
      return this.em.find(Empresa.class,id);
    }

    public Empresa convertToMerge(Empresa empresa) {
        return this.em.merge(empresa);
    }
}
