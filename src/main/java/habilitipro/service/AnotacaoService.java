package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.AnotacaoDAO;
import habilitipro.model.persistence.Anotacao;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trabalhador;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class AnotacaoService {


    private final Logger LOG = LogManager.getLogger(AnotacaoService.class);

    private EntityManager em;

    private AnotacaoDAO anotacaoDAO;

    private TrabalhadorService trabalhadorService;

    private ModuloService moduloService;

    private Transaction transaction;

    public AnotacaoService(EntityManager em) {
        this.em = em;
        this.anotacaoDAO = new AnotacaoDAO(em);
        this.moduloService = new ModuloService(em);
        this.trabalhadorService = new TrabalhadorService(em);
        this.transaction = new Transaction(em);
    }

    public void create(Anotacao anotacao) {
        this.LOG.info("Preparando criação de anotação...");
        validateNullObject(anotacao,"anotação");

        this.LOG.info("Verificando se o modulo já existe");
        Modulo modulo = this.moduloService.findByName(anotacao.getModulo().getNome());
        if(modulo != null ) {
            anotacao.setModulo(modulo);
        }
        this.LOG.info("Verificando se o trabalhador já existe");
        Trabalhador trabalhador = this.trabalhadorService.findByCpf(anotacao.getTrabalhador().getCpf());
        if(trabalhador != null) {
            anotacao.setTrabalhador(trabalhador);
        }

        validateDuplicate(anotacao);

        try {
            transaction.beginTransaction();
            this.anotacaoDAO.create(anotacao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar anotação: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Anotacao");
        }
        this.LOG.info("Anotação criada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção da Anotação...");
        validateNullId(id);
        Anotacao anotacao = this.anotacaoDAO.getById(id);
        validateNullObject(anotacao,"anotação");
        this.LOG.info("Anotação encontrada! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.anotacaoDAO.delete(anotacao);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar anotação: "+e.getMessage());
            throw new RuntimeException("Failed to delete Anotacao");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Anotacao newAnotacao, Long anotacaoId) {
        this.LOG.info("Preparando para atualização da função...");
        validateNullId(anotacaoId);
        validateNullObject(newAnotacao,"anotação");
        this.LOG.info("Validando existência de anotação com o Id informado");
        Anotacao anotacao = this.anotacaoDAO.getById(anotacaoId);
        validateNullObject(anotacao,"anotação");
        this.LOG.info("Anotação encontrada! Iniciando atualização...");

        try {
           transaction.beginTransaction();
            this.anotacaoDAO.update(anotacao);
            anotacao.setTexto(newAnotacao.getTexto());
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar anotação: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Anotacao");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Anotacao> listAll() {
        this.LOG.info("Preparando listagem das anotações...");
        List<Anotacao> anotacoes = this.anotacaoDAO.listAll();
        validateNullList(Collections.singletonList(anotacoes),"anotação");

        if(anotacoes != null) {
            this.LOG.info(anotacoes.size()+" anotaç(ão/ões) encontrada(s)");
        }
        return anotacoes;
    }

    private Anotacao findByTexto(String texto) {
        validateNullString(texto,"texto");
        try {
            this.LOG.info("Verificando se existe anotação com o texto informado...");
            Anotacao anotacao = this.anotacaoDAO.findByTexto(texto.toLowerCase());
            this.LOG.info("Anotação encontrada!");
            return anotacao;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada anotação com o nome informado");
            return null;
        }
    }

    public Anotacao getById(Long id) {
        this.LOG.info("Preparando busca de anotação pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe anotação com o id informado...");
        Anotacao anotacao = this.anotacaoDAO.getById(id);
        validateNullObject(anotacao,"anotação");
        if(anotacao != null) {
            this.LOG.info("Anotação encontrada!");
        }
        return anotacao;
    }

    private void validateDuplicate(Anotacao anotacao) {
        Modulo modulo = this.moduloService.findByName(anotacao.getModulo().getNome());
        Trabalhador trabalhador = this.trabalhadorService.findByCpf(anotacao.getTrabalhador().getCpf());

        List<Anotacao> anotacoes = this.listAll();

       if(anotacoes.stream().filter(a->a.getTrabalhador().getCpf().equals(trabalhador.getCpf())&&
               a.getModulo().getNome().equals(modulo.getNome())).toArray().length==1) {
           this.LOG.error("Essa anotação já consta em nossa base de dados");
           throw new EntityExistsException("Entity Anotacao already exists");
       }
    }
}