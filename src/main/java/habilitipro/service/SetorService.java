package habilitipro.service;

import habilitipro.model.dao.SetorDAO;
import habilitipro.model.persistence.Ocupacao;
import habilitipro.model.persistence.Setor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class SetorService {

    private final Logger LOG = LogManager.getLogger(SetorService.class);

    private EntityManager em;

    private SetorDAO setorDAO;

    public SetorService(EntityManager em) {
        this.em = em;
        this.setorDAO = new SetorDAO(em);
    }

    public void create(Setor setor) {
        this.LOG.info("Preparando criação de setor...");
        validateNullSetor(setor);
        validateDuplicate(setor);

        try {
            beginTransaction();
            this.setorDAO.create(setor);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar setor: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Setor");
        }
        this.LOG.info("Setor criado com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção do setor...");
        validateNullId(id);
        Setor setor = this.setorDAO.getById(id);
        validateNullSetor(setor);
        this.LOG.info("Setor encontrado! Iniciando deleção...");

        try {
            beginTransaction();
            this.setorDAO.delete(setor);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar setor: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Setor");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Setor newSetor, Long setorId) {
        this.LOG.info("Preparando para atualização do setor...");

        validateNullId(setorId);
        validateNullSetor(newSetor);
        this.LOG.info("Validando existência de setor com o Id informado");
        Setor setor = this.setorDAO.getById(setorId);
        validateNullSetor(setor);
        this.LOG.info("Setor encontrado! Iniciando atualização...");

        try {
            beginTransaction();
            this.setorDAO.update(setor);
            if(setor.getNome().equals(newSetor.getNome())) {
                this.LOG.info("O setor atual já contém os dados atualizados");
                commitAndClose();
                return;
            }
            validateDuplicate(newSetor);
            setor.setNome(newSetor.getNome());
            commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar setor: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Setor");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Setor> listAll() {
        this.LOG.info("Preparando listagem das setores...");
        List<Setor> setores = this.setorDAO.listAll();
        validateNullList(setores);

        if(setores != null) {
            this.LOG.info(setores.size()+" setor(es) encontrado(s)");
        }
        return setores;
    }

    public List<Setor> listByName(String nome) {
        this.LOG.info("Preparando listagem de setores pelo nome...");
        validateNullName(nome);
        List<Setor> setores = this.setorDAO.listByName(nome.toLowerCase());
        validateNullList(setores);

        if(setores != null){
            this.LOG.info(setores.size()+" setor(es) encontrado(s)");
        }
        return setores;
    }

    public Setor findByName(String nome) {
        validateNullName(nome);
        try {
            this.LOG.info("Verificando se existe setor com o nome informado...");
            Setor setor = this.setorDAO.findByName(nome.toLowerCase());
            this.LOG.info("Setor encontrado!");
            return setor;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada setor com o nome informado");
            return null;
        }
    }

    public Setor getById(Long id) {
        this.LOG.info("Preparando busca de setor pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe setor com o id informado...");
        Setor setor = this.setorDAO.getById(id);
        validateNullSetor(setor);
        if(setor != null) {
            this.LOG.info("Setor encontrado!");
        }
        return setor;
    }

    private void validateDuplicate(Setor setor) {
        this.LOG.info("Verificando se já existe setor com esse nome...");
        Setor setor1 = this.findByName(setor.getNome());

        if(setor1 != null) {
            this.LOG.error("O setor informado já existe no banco de dados");
            throw new EntityExistsException("Entity Setor already exists");
        }
    }

    private List<Setor> validateNullList(List<Setor> setores) {
        this.LOG.info("Verificando se existe registros de setor");
        if(setores == null) {
            this.LOG.info("Não foram encontrados setores.");
            return new ArrayList<>();
        }
        return setores;
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

    private void validateNullSetor(Setor setor) {
        this.LOG.info("Verificando se o setor é nulo...");
        if(setor == null) {
            this.LOG.error("Entidade Setor não encontrado");
            throw new EntityNotFoundException("Entity Setor not found");
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
