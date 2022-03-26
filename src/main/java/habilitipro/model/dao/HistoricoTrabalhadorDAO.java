package habilitipro.model.dao;
import habilitipro.model.persistence.HistoricoTrabalhador;

import javax.persistence.EntityManager;
import java.util.List;

public class HistoricoTrabalhadorDAO {


    private EntityManager em;

    public HistoricoTrabalhadorDAO(EntityManager em) {
        this.em = em;
    }

    public void create(HistoricoTrabalhador historicoTrabalhador) {
        this.em.persist(historicoTrabalhador);
    }

    public void delete(HistoricoTrabalhador historicoTrabalhador) {
        this.em.remove(convertToMerge(historicoTrabalhador));
    }

    public void update(HistoricoTrabalhador historicoTrabalhador) {
        convertToMerge(historicoTrabalhador);
    }

    public List<HistoricoTrabalhador> listAll() {
        String jpql = "SELECT h FROM HistoricoTrabalhador AS h";
        return this.em.
                createQuery(jpql,HistoricoTrabalhador.class)
                .getResultList();
    }



    public HistoricoTrabalhador getById(Long id) {
        return this.em.find(HistoricoTrabalhador.class,id);
    }

    public HistoricoTrabalhador convertToMerge(HistoricoTrabalhador historicoTrabalhador) {
        return this.em.merge(historicoTrabalhador);
    }
}
