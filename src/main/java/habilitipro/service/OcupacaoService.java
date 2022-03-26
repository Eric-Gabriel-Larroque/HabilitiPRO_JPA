package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.OcupacaoDAO;
import habilitipro.model.persistence.Ocupacao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class OcupacaoService {

        private final Logger LOG = LogManager.getLogger(OcupacaoService.class);

        private EntityManager em;

        private OcupacaoDAO ocupacaoDAO;

        private Transaction transaction;

        public OcupacaoService(EntityManager em) {
            this.em = em;
            this.ocupacaoDAO = new OcupacaoDAO(em);
            this.transaction = new Transaction(em);
        }

    public void create(Ocupacao ocupacao) {
        this.LOG.info("Preparando criação de ocupação...");
        validateNullObject(ocupacao,"ocupação");
        validateDuplicate(ocupacao);

        try {
            transaction.beginTransaction();
            this.ocupacaoDAO.create(ocupacao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar ocupação: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Ocupacao");
        }
        this.LOG.info("Ocupação criada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção da ocupação...");
        validateNullId(id);
        Ocupacao ocupacao = this.ocupacaoDAO.getById(id);
        validateNullObject(ocupacao,"ocupação");
        this.LOG.info("Ocupação encontrada! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.ocupacaoDAO.delete(ocupacao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar ocupação: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Ocupacao");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Ocupacao newOcupacao, Long ocupacaoId) {
        this.LOG.info("Preparando para atualização da ocupação...");
        validateNullId(ocupacaoId);
        validateNullObject(newOcupacao,"ocupação");
        this.LOG.info("Validando existência de ocupação com o Id informado");
        Ocupacao ocupacao = this.ocupacaoDAO.getById(ocupacaoId);
        validateNullObject(ocupacao,"ocupação");
        this.LOG.info("Ocupação encontrada! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.ocupacaoDAO.update(ocupacao);
            if(ocupacao.getNome().equals(newOcupacao.getNome())) {
                this.LOG.info("A ocupação atual já contém os dados atualizados");
                transaction.commitAndClose();
                return;
            }
            validateDuplicate(newOcupacao);
            ocupacao.setNome(newOcupacao.getNome());
            transaction.commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar ocupação: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Ocupacao");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Ocupacao> listAll() {
        this.LOG.info("Preparando listagem das ocupações...");
        List<Ocupacao> ocupacoes = this.ocupacaoDAO.listAll();
        validateNullList(Collections.singletonList(ocupacoes),"ocupação");

        if(ocupacoes != null) {
            this.LOG.info(ocupacoes.size()+" ocupaç(ão/ões) encontrada(s)");
        }
        return ocupacoes;
    }

    public List<Ocupacao> listByName(String nome) {
        this.LOG.info("Preparando listagem de ocupações pelo nome...");
        validateNullString(nome,"nome");
        List<Ocupacao> ocupacoes = this.ocupacaoDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(ocupacoes),"ocupação");

        if(ocupacoes != null){
            this.LOG.info(ocupacoes.size()+" ocupaç(ão/ões) encontrada(s)");
        }
        return ocupacoes;
    }

    public Ocupacao findByName(String nome) {
        validateNullString(nome,"nome");
        try {
            this.LOG.info("Verificando se existe ocupação com o nome informado...");
            Ocupacao ocupacao = this.ocupacaoDAO.findByName(nome.toLowerCase());
            this.LOG.info("Ocupação encontrada!");
            return ocupacao;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada ocupação com o nome informado");
            return null;
        }
    }

    public Ocupacao getById(Long id) {
        this.LOG.info("Preparando busca de ocupação pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe ocupação com o id informado...");
        Ocupacao ocupacao = this.ocupacaoDAO.getById(id);
        validateNullObject(ocupacao,"ocupação");
        if(ocupacao != null) {
            this.LOG.info("Ocupação encontrada!");
        }
        return ocupacao;
    }

    private void validateDuplicate(Ocupacao ocupacao) {
        this.LOG.info("Verificando se já existe ocupação com esse nome...");
        Ocupacao ocupacao1 = this.findByName(ocupacao.getNome());

        if(ocupacao1 != null) {
            this.LOG.error("A ocupação informada já existe no banco de dados");
            throw new EntityExistsException("Entity Ocupacao already exists");
        }
    }
}