package habilitipro.service;

import habilitipro.model.dao.FuncaoDAO;
import habilitipro.model.persistence.Funcao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class FuncaoService {

    private final Logger LOG = LogManager.getLogger(FuncaoService.class);

    private EntityManager em;

    private FuncaoDAO funcaoDAO;

    public FuncaoService(EntityManager em) {
        this.em = em;
        this.funcaoDAO = new FuncaoDAO(em);
    }

    public void create(Funcao funcao) {
        this.LOG.info("Preparando criação de função...");
        validateNullFuncao(funcao);
        validateDuplicate(funcao);

        try {
            beginTransaction();
            this.funcaoDAO.create(funcao);
            commitAndClose();
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
        validateNullFuncao(funcao);
        this.LOG.info("Função encontrada! Iniciando deleção...");

        try {
            beginTransaction();
            this.funcaoDAO.delete(funcao);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar função: "+e.getMessage());
            throw new RuntimeException("Failed to delete Funcao");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Funcao newFuncao, Long funcaoId) {
        validateNullId(funcaoId);
        validateNullFuncao(newFuncao);
        this.LOG.info("Validando existência de função com o Id informado");
        Funcao funcao = this.funcaoDAO.getById(funcaoId);
        validateNullFuncao(funcao);
        this.LOG.info("Função encontrada! Iniciando atualização...");

        try {
            beginTransaction();
            this.funcaoDAO.update(funcao);
            if(funcao.getNome().equals(newFuncao.getNome())) {
                this.LOG.info("A função atual já contém os dados atualizados");
                commitAndClose();
                return;
            }
            funcao.setNome(newFuncao.getNome());
            commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar função: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Funcao");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Funcao> listAll() {
        this.LOG.info("Preparando listagem das funções...");
        List<Funcao> funcoes = this.funcaoDAO.listAll();
        validateNullList(funcoes);

        if(funcoes != null) {
            this.LOG.info(funcoes.size()+" funç(ão/ões) encontrada(s)");
        }
        return funcoes;
    }

    public List<Funcao> listByName(String nome) {
        this.LOG.info("Preparando listagem de funções pelo nome...");
        validateNullName(nome);
        List<Funcao> funcoes = this.funcaoDAO.listByName(nome.toLowerCase());
        validateNullList(funcoes);

        if(funcoes != null){
            this.LOG.info(funcoes.size()+" funç(ão/ões) encontrada(s)");
        }
        return funcoes;
    }

    public Funcao findByName(String nome) {
        validateNullName(nome);
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
        validateNullFuncao(funcao);
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

    private List<Funcao> validateNullList(List<Funcao> funcoes) {
        this.LOG.info("Verificando se existe registros de função");
        if(funcoes == null) {
            this.LOG.info("Não foram encontradas funções.");
            return new ArrayList<>();
        }
        return funcoes;
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

    private void validateNullFuncao(Funcao funcao) {
        this.LOG.info("Verificando se a função é nula...");
        if(funcao == null) {
            this.LOG.error("Entidade função não encontrada");
            throw new EntityNotFoundException("Entity Funcao not found");
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
