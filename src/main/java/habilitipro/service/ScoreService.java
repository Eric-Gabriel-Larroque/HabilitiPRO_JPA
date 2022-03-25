package habilitipro.service;

import habilitipro.model.dao.AnotacaoDAO;
import habilitipro.model.dao.ScoreDAO;
import habilitipro.model.persistence.Anotacao;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Score;
import habilitipro.model.persistence.Trabalhador;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.model.source.internal.hbm.ManyToOnePropertySource;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class ScoreService {



    private final Logger LOG = LogManager.getLogger(ScoreService.class);

    private EntityManager em;

    private ScoreDAO scoreDAO;

    private TrabalhadorService trabalhadorService;

    private ModuloService moduloService;

    public ScoreService(EntityManager em) {
        this.em = em;
        this.scoreDAO = new ScoreDAO(em);
        this.moduloService = new ModuloService(em);
        this.trabalhadorService = new TrabalhadorService(em);
    }

    public void create(Score score) {
        this.LOG.info("Preparando criação de score...");
        validateNullScore(score);
        validateNota(score.getNota());

        this.LOG.info("Verificando se o modulo já existe");
        Modulo modulo = this.moduloService.findByName(score.getModulo().getNome());
        if(modulo != null ) {
            score.setModulo(modulo);
        }
        this.LOG.info("Verificando se o trabalhador já existe");
        Trabalhador trabalhador = this.trabalhadorService.findByCpf(score.getTrabalhador().getCpf());
        if(trabalhador != null) {
            score.setTrabalhador(trabalhador);
        }

        validateDuplicate(score);


        try {
            beginTransaction();
            this.scoreDAO.create(score);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar score: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Score");
        }
        this.LOG.info("Score criado com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção do Score...");
        validateNullId(id);
        Score score = this.scoreDAO.getById(id);
        validateNullScore(score);
        this.LOG.info("Score encontrado! Iniciando deleção...");

        try {
            beginTransaction();
            this.scoreDAO.delete(score);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar score: "+e.getMessage());
            throw new RuntimeException("Failed to delete Score");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Score newScore, Long scoreId) {
        this.LOG.info("Preparando para atualização do score...");
        validateNullId(scoreId);
        validateNullScore(newScore);
        this.LOG.info("Validando existência de score com o Id informado");
        Score score = this.scoreDAO.getById(scoreId);
        validateNullScore(score);
        this.LOG.info("Score encontrado! Iniciando atualização...");

        try {
            beginTransaction();
            this.scoreDAO.update(score);
            validateNota(newScore.getNota());
            score.setNota(newScore.getNota());
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar score: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Score");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Score> listAll() {
        this.LOG.info("Preparando listagem dos scores...");
        List<Score> scores = this.scoreDAO.listAll();
        validateNullList(scores);

        if(scores != null) {
            this.LOG.info(scores.size()+" score(s) encontrado(s)");
        }
        return scores;
    }

    public Score getById(Long id) {
        this.LOG.info("Preparando busca de score pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe score com o id informado...");
        Score score = this.scoreDAO.getById(id);
        validateNullScore(score);
        if(score != null) {
            this.LOG.info("Score encontrado!");
        }
        return score;
    }

    public void validateNota(Integer nota) {
        this.LOG.info("Verificando se a nota está de acordo com a regra de negócios...");
        final String SCORE_TEMPLATE = "^[1-5]";
        if(!String.valueOf(nota).matches(SCORE_TEMPLATE)) {
            this.LOG.error("A nota deve ser um número inteiro de 1 a 5");
            throw new RuntimeException("Score must be an Integer between 1 and 5");
        }
    }

    private void validateDuplicate(Score score) {
        Modulo modulo = this.moduloService.findByName(score.getModulo().getNome());
        Trabalhador trabalhador = this.trabalhadorService.findByCpf(score.getTrabalhador().getCpf());

        List<Score> scores = this.listAll();

        if(scores.stream().filter(a->a.getTrabalhador().getCpf().equals(trabalhador.getCpf())&&
                a.getModulo().getNome().equals(modulo.getNome())).toArray().length==1) {
            this.LOG.error("Esse score já consta em nossa base de dados");
            throw new EntityExistsException("Entity Score already exists");
        }
    }

    private List<Score> validateNullList(List<Score> scores) {
        this.LOG.info("Verificando se existe registros de score");
        if(scores == null) {
            this.LOG.info("Não foram encontrados scores.");
            return new ArrayList<>();
        }
        return scores;
    }

    private void validateNullId(Long id) {
        this.LOG.info("Verificando se o id informado é nulo...");
        if(id == null) {
            this.LOG.error("O id informado é nulo");
            throw new RuntimeException("Id is null");
        }
    }

    private void validateNullNota(Integer nota) {
        this.LOG.info("Verificando se a nota informada é nula...");
        if(nota == null) {
            this.LOG.error("A nota informada é nula");
            throw new RuntimeException("score attribute is null");
        }
    }

    private void validateNullScore(Score score) {
        this.LOG.info("Verificando se o score é nulo...");
        if(score == null) {
            this.LOG.error("Entidade score não encontrado");
            throw new EntityNotFoundException("Entity Score not found");
        }
    }

    private void beginTransaction() {
        this.LOG.info("Iniciando transação...");
        this.em.getTransaction().begin();
    }

    private void commitAndClose() {
        this.LOG.info("Commitando e fechando transação...");
        this.em.getTransaction().commit();
        this.em.close();
    }
}
