package habilitipro.service;

import habilitipro.model.dao.OcupacaoDAO;
import habilitipro.model.persistence.Ocupacao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class OcupacaoService {

        private final Logger LOG = LogManager.getLogger(OcupacaoService.class);

        private EntityManager em;

        private OcupacaoDAO ocupacaoDAO;

        public OcupacaoService(EntityManager em) {
            this.em = em;
            this.ocupacaoDAO = new OcupacaoDAO(em);
        }

    public void create(Ocupacao ocupacao) {
        this.LOG.info("Preparando criação de ocupação...");
        validateNullOcupacao(ocupacao);
        validateDuplicate(ocupacao);

        try {
            beginTransaction();
            this.ocupacaoDAO.create(ocupacao);
            commitAndClose();
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
        validateNullOcupacao(ocupacao);
        this.LOG.info("Ocupação encontrada! Iniciando deleção...");

        try {
            beginTransaction();
            this.ocupacaoDAO.delete(ocupacao);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar ocupação: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Ocupacao");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Ocupacao newOcupacao, Long ocupacaoId) {
        validateNullId(ocupacaoId);
        validateNullOcupacao(newOcupacao);
        this.LOG.info("Validando existência de ocupação com o Id informado");
        Ocupacao ocupacao = this.ocupacaoDAO.getById(ocupacaoId);
        validateNullOcupacao(ocupacao);
        this.LOG.info("Ocupação encontrada! Iniciando atualização...");

        try {
            beginTransaction();
            this.ocupacaoDAO.update(ocupacao);
            if(ocupacao.getNome().equals(newOcupacao.getNome())) {
                this.LOG.info("A ocupação atual já contém os dados atualizados");
                commitAndClose();
                return;
            }
            ocupacao.setNome(newOcupacao.getNome());
            commitAndClose();

        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar ocupação: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Ocupacao");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Ocupacao> listAll() {
        this.LOG.info("Preparando listagem das ocupações...");
        List<Ocupacao> ocupacoes = this.ocupacaoDAO.listAll();
        validateNullList(ocupacoes);

        if(ocupacoes != null) {
            this.LOG.info(ocupacoes.size()+" ocupaç(ão/ões) encontrada(s)");
        }
        return ocupacoes;
    }

    public List<Ocupacao> listByName(String nome) {
        this.LOG.info("Preparando listagem de ocupações pelo nome...");
        validateNullName(nome);
        List<Ocupacao> ocupacoes = this.ocupacaoDAO.listByName(nome.toLowerCase());
        validateNullList(ocupacoes);

        if(ocupacoes != null){
            this.LOG.info(ocupacoes.size()+" ocupaç(ão/ões) encontrada(s)");
        }
        return ocupacoes;
    }

    public Ocupacao findByName(String nome) {
        validateNullName(nome);
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
        validateNullOcupacao(ocupacao);
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

    private List<Ocupacao> validateNullList(List<Ocupacao> ocupacoes) {
        this.LOG.info("Verificando se existe registros de ocupação");
        if(ocupacoes == null) {
            this.LOG.info("Não foram encontradas ocupações.");
            return new ArrayList<>();
        }
        return ocupacoes;
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

    private void validateNullOcupacao(Ocupacao ocupacao) {
        this.LOG.info("Verificando se a ocupação é nula...");
        if(ocupacao == null) {
            this.LOG.error("Entidade Ocupação não encontrada");
            throw new EntityNotFoundException("Entity Ocupacao not found");
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
