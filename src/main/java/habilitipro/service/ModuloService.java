package habilitipro.service;

import habilitipro.model.dao.ModuloDAO;
import habilitipro.model.persistence.Funcao;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class ModuloService {

    private final Logger LOG = LogManager.getLogger(ModuloService.class);

    private EntityManager em;

    private ModuloDAO moduloDAO;

    private TrilhaService trilhaService;

    public ModuloService(EntityManager em) {
        this.em = em;
        this.moduloDAO = new ModuloDAO(em);
        this.trilhaService = new TrilhaService(em);
    }

    public void create(Modulo modulo) {
        this.LOG.info("Preparando para criação do módulo...");
        validateNullModulo(modulo);
        validateDuplicate(modulo);


        try {
            beginTransaction();
            this.moduloDAO.create(modulo);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar o módulo: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Modulo");
        }
        this.LOG.info("Criação realizada com sucesso!");
    }

    public void delete(Long id) {

    }

    public void update(Modulo newModulo, Long moduloId) {

    }

    public List<Modulo> listAll() {
        this.LOG.info("Preparando listagem dos módulos...");
        List<Modulo> modulos = this.moduloDAO.listAll();
        validateNullList(modulos);

        if(modulos != null) {
            this.LOG.info(modulos.size()+" módulo(s) encontrado(s)");
        }
        return modulos;
    }

    public List<Modulo> listByName(String nome) {
        this.LOG.info("Preparando listagem de módulos pelo nome...");
        validateNullName(nome);
        List<Modulo> modulos = this.moduloDAO.listByName(nome.toLowerCase());
        validateNullList(modulos);

        if(modulos != null){
            this.LOG.info(modulos.size()+" módulo(s) encontrado(s)");
        }
        return modulos;
    }

    public Modulo findByName(String nome) {
        validateNullName(nome);
        try {
            this.LOG.info("Verificando se existe módulo com o nome informado...");
            Modulo modulo = this.moduloDAO.findByName(nome.toLowerCase());
            this.LOG.info("Módulo encontrado!");
            return modulo;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrado módulo com o nome informado");
            return null;
        }catch (NonUniqueResultException e) {
            this.LOG.info("Há mais de um resultado para o nome informado");
            return null;
        }
    }

    public Modulo getById(Long id) {
        this.LOG.info("Preparando busca de módulo pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe módulo com o id informado...");
        Modulo modulo = this.moduloDAO.getById(id);
        validateNullModulo(modulo);
        if(modulo != null) {
            this.LOG.info("Módulo encontrado!");
        }
        return modulo;
    }

    private void validateDuplicate(Modulo modulo) {
        this.LOG.info("Verificando se já existe módulo com esse nome...");
        Modulo modulo1 = this.findByName(modulo.getNome());

        if(modulo1 != null) {
            this.LOG.error("O módulo informado já existe no banco de dados");
            throw new EntityExistsException("Entity Modulo already exists");
        }
    }

    private List<Modulo> validateNullList(List<Modulo> modulos) {
        this.LOG.info("Verificando se existe registros de módulo");
        if(modulos == null) {
            this.LOG.info("Não foram encontrados módulos.");
            return new ArrayList<>();
        }
        return modulos;
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

    private void validateNullModulo(Modulo modulo) {
        this.LOG.info("Verificando se o módulo é nulo...");
        if(modulo == null) {
            this.LOG.error("Entidade módulo não encontrado");
            throw new EntityNotFoundException("Entity Modulo not found");
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
