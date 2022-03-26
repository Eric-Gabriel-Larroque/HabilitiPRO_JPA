package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.FuncaoDAO;
import habilitipro.model.persistence.Funcao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class FuncaoService {

    private final Logger LOG = LogManager.getLogger(FuncaoService.class);

    private EntityManager em;

    private FuncaoDAO funcaoDAO;

    private Transaction transaction;

    public FuncaoService(EntityManager em) {
        this.em = em;
        this.funcaoDAO = new FuncaoDAO(em);
        this.transaction = new Transaction(em);
    }

    public void create(Funcao funcao) {
        this.LOG.info("Preparando criação de função...");
        validateNullObject(funcao,"função");
        validateDuplicate(funcao);

        try {
            transaction.beginTransaction();
            this.funcaoDAO.create(funcao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar função: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Funcao");
        }
        this.LOG.info("Funcão criada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção da Função...");
        validateNullId(id);
        Funcao funcao = this.funcaoDAO.getById(id);
        validateNullObject(funcao,"função");
        this.LOG.info("Função encontrada! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.funcaoDAO.delete(funcao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar função: "+e.getMessage());
            throw new RuntimeException("Failed to delete Funcao");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Funcao newFuncao, Long funcaoId) {
        this.LOG.info("Preparando para atualização da função...");
        validateNullId(funcaoId);
        validateNullObject(newFuncao,"função");
        this.LOG.info("Validando existência de função com o Id informado");
        Funcao funcao = this.funcaoDAO.getById(funcaoId);
        validateNullObject(funcao,"função");
        this.LOG.info("Função encontrada! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.funcaoDAO.update(funcao);
            if(funcao.getNome().equals(newFuncao.getNome())) {
                this.LOG.info("A função atual já contém os dados atualizados");
                transaction.commitAndClose();
                return;
            }
            validateDuplicate(newFuncao);
            funcao.setNome(newFuncao.getNome());
            transaction.commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar função: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Funcao");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Funcao> listAll() {
        this.LOG.info("Preparando listagem das funções...");
        List<Funcao> funcoes = this.funcaoDAO.listAll();
        validateNullList(Collections.singletonList(funcoes),"função");

        if(funcoes != null) {
            this.LOG.info(funcoes.size()+" funç(ão/ões) encontrada(s)");
        }
        return funcoes;
    }

    public List<Funcao> listByName(String nome) {
        this.LOG.info("Preparando listagem de funções pelo nome...");
        validateNullString(nome,"nome");
        List<Funcao> funcoes = this.funcaoDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(funcoes),"função");

        if(funcoes != null){
            this.LOG.info(funcoes.size()+" funç(ão/ões) encontrada(s)");
        }
        return funcoes;
    }

    public Funcao findByName(String nome) {
        validateNullString(nome,"nome");
        try {
            this.LOG.info("Verificando se existe função com o nome informado...");
            Funcao funcao = this.funcaoDAO.findByName(nome.toLowerCase());
            this.LOG.info("Função encontrada!");
            return funcao;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada função com o nome informado");
            return null;
        }
    }

    public Funcao getById(Long id) {
        this.LOG.info("Preparando busca de função pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe função com o id informado...");
        Funcao funcao = this.funcaoDAO.getById(id);
        validateNullObject(funcao,"função");
        if(funcao != null) {
            this.LOG.info("Função encontrada!");
        }
        return funcao;
    }

    private void validateDuplicate(Funcao funcao) {
        this.LOG.info("Verificando se já existe função com esse nome...");
        Funcao funcao1 = this.findByName(funcao.getNome());

        if(funcao1 != null) {
            this.LOG.error("A função informada já existe no banco de dados");
            throw new EntityExistsException("Entity Funcao already exists");
        }
    }
}