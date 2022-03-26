package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.SetorDAO;
import habilitipro.model.persistence.Setor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class SetorService {

    private final Logger LOG = LogManager.getLogger(SetorService.class);

    private EntityManager em;

    private SetorDAO setorDAO;

    private Transaction transaction;

    public SetorService(EntityManager em) {
        this.em = em;
        this.setorDAO = new SetorDAO(em);
        this.transaction = new Transaction(em);
    }

    public void create(Setor setor) {
        this.LOG.info("Preparando criação de setor...");
        validateNullObject(setor,"setor");
        validateDuplicate(setor);

        try {
            transaction.beginTransaction();
            this.setorDAO.create(setor);
            transaction.commitAndClose();
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
        validateNullObject(setor,"setor");
        this.LOG.info("Setor encontrado! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.setorDAO.delete(setor);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar setor: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Setor");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Setor newSetor, Long setorId) {
        this.LOG.info("Preparando para atualização do setor...");

        validateNullId(setorId);
        validateNullObject(newSetor,"setor");
        this.LOG.info("Validando existência de setor com o Id informado");
        Setor setor = this.setorDAO.getById(setorId);
        validateNullObject(setor,"setor");
        this.LOG.info("Setor encontrado! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.setorDAO.update(setor);
            if(setor.getNome().equals(newSetor.getNome())) {
                this.LOG.info("O setor atual já contém os dados atualizados");
                transaction.commitAndClose();
                return;
            }
            validateDuplicate(newSetor);
            setor.setNome(newSetor.getNome());
            transaction.commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar setor: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Setor");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Setor> listAll() {
        this.LOG.info("Preparando listagem das setores...");
        List<Setor> setores = this.setorDAO.listAll();
        validateNullList(Collections.singletonList(setores),"setor");

        if(setores != null) {
            this.LOG.info(setores.size()+" setor(es) encontrado(s)");
        }
        return setores;
    }

    public List<Setor> listByName(String nome) {
        this.LOG.info("Preparando listagem de setores pelo nome...");
        validateNullString(nome,"nome");
        List<Setor> setores = this.setorDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(setores),"setor");

        if(setores != null){
            this.LOG.info(setores.size()+" setor(es) encontrado(s)");
        }
        return setores;
    }

    public Setor findByName(String nome) {
        validateNullString(nome,"nome");
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
        validateNullObject(setor,"setor");
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
}