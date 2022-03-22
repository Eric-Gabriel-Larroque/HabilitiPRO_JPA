package habilitipro.model.dao;

import habilitipro.model.persistence.Trabalhador;
import habilitipro.model.persistence.Usuario;

import javax.persistence.EntityManager;
import java.util.List;

public class UsuarioDAO {
    private EntityManager em;

    public UsuarioDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Usuario usuario) {
        this.em.persist(usuario);
    }

    public void delete(Usuario usuario) {
        this.em.remove(convertToMerge(usuario));
    }

    public void update(Usuario usuario) {
        convertToMerge(usuario);
    }

    public List<Usuario> listAll() {
        String jpql = "SELECT f FROM Funcao AS f";
        return this.em.
                createQuery(jpql,Usuario.class)
                .getResultList();
    }

    public List<Usuario> listByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Usuario.class)
                .setParameter("nome",nome)
                .getResultList();
    }

    public Usuario findByName(String nome) {
        String jpql = "SELECT f FROM Funcao AS f WHERE f.nome=:nome";
        return this.em.
                createQuery(jpql,Usuario.class)
                .setParameter("nome",nome)
                .getSingleResult();
    }

    public Usuario getById(Long id) {
        return this.em.find(Usuario.class,id);
    }

    public Usuario convertToMerge(Usuario usuario) {
        return this.em.merge(usuario);
    }
}
