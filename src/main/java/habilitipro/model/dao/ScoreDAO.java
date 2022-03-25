package habilitipro.model.dao;

import habilitipro.model.persistence.Anotacao;
import habilitipro.model.persistence.Score;

import javax.persistence.EntityManager;
import java.util.List;

public class ScoreDAO {

    private EntityManager em;

    public ScoreDAO(EntityManager em) {
        this.em = em;
    }

    public void create(Score score) {
        this.em.persist(score);
    }

    public void delete(Score score) {
        this.em.remove(convertToMerge(score));
    }

    public void update(Score score) {
        convertToMerge(score);
    }

    public List<Score> listAll() {
        String jpql = "SELECT s FROM Score AS s";
        return this.em.
                createQuery(jpql,Score.class)
                .getResultList();
    }

    public List<Score> listByNota(Integer nota) {
        String jpql = "SELECT s FROM Score AS s WHERE s.nota=:nota";
        return this.em.
                createQuery(jpql,Score.class)
                .setParameter("nota",nota)
                .getResultList();
    }

    public Score getById(Long id) {
        return this.em.find(Score.class,id);
    }

    public Score convertToMerge(Score score) {
        return this.em.merge(score);
    }
}
