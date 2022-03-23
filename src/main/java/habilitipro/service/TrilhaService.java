package habilitipro.service;

import habilitipro.model.dao.TrilhaDAO;
import habilitipro.model.persistence.Funcao;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class TrilhaService {

    private final Logger LOG = LogManager.getLogger(TrilhaService.class);

    private EntityManager em;

    private TrilhaDAO trilhaDAO;

    public TrilhaService(EntityManager em) {
        this.em = em;
        this.trilhaDAO = new TrilhaDAO(em);
    }

    public void create(Trilha trilha) {
        validateNullTrilha(trilha);


    }

    public void delete(Long id) {

    }

    public void update(Trilha newTrilha, Long trilhaId) {

    }

    public List<Trilha> listAll() {
        this.LOG.info("Preparando listagem das trilhas...");
        List<Trilha> trilhas = this.trilhaDAO.listAll();
        validateNullList(trilhas);

        if(trilhas != null) {
            this.LOG.info(trilhas.size()+" trilha(s) encontrada(s)");
        }
        return trilhas;
    }

    public List<Trilha> listByName(String nome) {
        this.LOG.info("Preparando listagem de trilhas pelo nome...");
        validateNullName(nome);
        List<Trilha> trilhas = this.trilhaDAO.listByName(nome.toLowerCase());
        validateNullList(trilhas);

        if(trilhas != null){
            this.LOG.info(trilhas.size()+" trilha(s) encontrada(s)");
        }
        return trilhas;
    }

    public Trilha findByName(String nome) {
        validateNullName(nome);
        try {
            this.LOG.info("Verificando se existe módulo com o nome informado...");
            Trilha trilha = this.trilhaDAO.findByName(nome.toLowerCase());
            this.LOG.info("Trilha encontrada!");
            return trilha;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada trilha com o nome informado");
            return null;
        }catch (NonUniqueResultException e) {
            this.LOG.info("Há mais de um resultado para o nome informado");
            return null;
        }
    }

    public Trilha getById(Long id) {
        this.LOG.info("Preparando busca de trilha pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe trilha com o id informado...");
        Trilha trilha = this.trilhaDAO.getById(id);
        validateNullTrilha(trilha);
        if(trilha != null) {
            this.LOG.info("Trilha encontrada!");
        }
        return trilha;
    }

    private void validateDuplicate(Trilha trilha) {
        this.LOG.info("Verificando se já existe trilha com esse nome...");
        Trilha trilha1 = this.findByName(trilha.getNome());

        if(trilha1 != null) {
            this.LOG.error("A trilha informada já existe no banco de dados");
            throw new EntityExistsException("Entity Trilha already exists");
        }
    }

    private List<Trilha> validateNullList(List<Trilha> trilhas) {
        this.LOG.info("Verificando se existe registros de trilha");
        if(trilhas == null) {
            this.LOG.info("Não foram encontradas trilhas.");
            return new ArrayList<>();
        }
        return trilhas;
    }

    private void validateNullId(Long id) {
        this.LOG.info("Verificando se o id informado é nulo...");
        if(id == null) {
            this.LOG.error("O id informado é nulo");
            throw new RuntimeException("Id is null");
        }
    }

    private void validateNullName(String nome) {
        this.LOG.info("Verificando se o nome informado é nulo...");
        if(nome == null || nome.isEmpty() || nome.isBlank()) {
            this.LOG.error("O nome informado é vazio ou nulo");
            throw new RuntimeException("nome is empty or null");
        }
    }

    private void validateNullTrilha(Trilha trilha) {
        this.LOG.info("Verificando se a trilha é nula...");
        if(trilha == null) {
            this.LOG.error("Entidade Trilha não encontrada");
            throw new EntityNotFoundException("Entity Trilha not found");
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
