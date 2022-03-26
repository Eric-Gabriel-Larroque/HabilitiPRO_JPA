package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.HistoricoTrabalhadorDAO;
import habilitipro.model.dao.TrabalhadorDAO;
import habilitipro.model.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class HistoricoTrabalhadorService {

    private Logger LOG = LogManager.getLogger(HistoricoTrabalhadorService.class);

    private EntityManager em;

    private HistoricoTrabalhadorDAO historicoTrabalhadorDAO;

    private TrabalhadorDAO trabalhadorDAO;

    private Transaction transaction;

    public HistoricoTrabalhadorService(EntityManager em) {
        this.em = em;
        this.historicoTrabalhadorDAO = new HistoricoTrabalhadorDAO(em);
        this.trabalhadorDAO = new TrabalhadorDAO(em);
        this.transaction = new Transaction(em);
    }

    public void create(HistoricoTrabalhador historicoTrabalhador) {
        this.LOG.info("Preparando criação de histórico...");
        validateNullObject(historicoTrabalhador,"histórico");
        validateNullString(historicoTrabalhador.getTrabalhadorRegistro(),"registro");

        try {
            transaction.beginTransaction();
            this.historicoTrabalhadorDAO.create(historicoTrabalhador);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar histórico: "+e.getMessage());
            throw new RuntimeException("Failed to create entity HistoricoTrabalhador");
        }
        this.LOG.info("Histórico criado com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção do Histórico...");
        validateNullId(id);
        HistoricoTrabalhador historicoTrabalhador = this.historicoTrabalhadorDAO.getById(id);
        validateNullObject(historicoTrabalhador,"histórico");
        this.LOG.info("Histórico encontrado! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.historicoTrabalhadorDAO.delete(historicoTrabalhador);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar histórico: "+e.getMessage());
            throw new RuntimeException("Failed to delete HistoricoTabalhador");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(HistoricoTrabalhador newHistorico, Long scoreId) {
        this.LOG.info("Preparando para atualização do histórico...");
        validateNullId(scoreId);
        validateNullObject(newHistorico,"histórico");
        this.LOG.info("Validando existência de histórico com o Id informado");
        HistoricoTrabalhador historicoTrabalhador = this.historicoTrabalhadorDAO.getById(scoreId);
        validateNullObject(historicoTrabalhador,"histórico");
        this.LOG.info("Histórico encontrado! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.historicoTrabalhadorDAO.update(historicoTrabalhador);
            validateNullString(newHistorico.getTrabalhadorRegistro(),"registro");
            historicoTrabalhador.setTrabalhadorRegistro(newHistorico.getTrabalhadorRegistro());
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar histórico: "+e.getMessage());
            throw new RuntimeException("Failed to update entity HistoricoTabalhador");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<HistoricoTrabalhador> listAll() {
        this.LOG.info("Preparando listagem dos históricos...");
        List<HistoricoTrabalhador> historicoTrabalhadores = this.historicoTrabalhadorDAO.listAll();
        validateNullList(Collections.singletonList(historicoTrabalhadores),"histórico");

        if(historicoTrabalhadores != null) {
            this.LOG.info(historicoTrabalhadores.size()+" histórico(s) encontrado(s)");
        }
        return historicoTrabalhadores;
    }

    public HistoricoTrabalhador getById(Long id) {
        this.LOG.info("Preparando busca de histórico pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe histórico com o id informado...");
        HistoricoTrabalhador historicoTrabalhador = this.historicoTrabalhadorDAO.getById(id);
        validateNullObject(historicoTrabalhador,"histórico");
        if(historicoTrabalhador != null) {
            this.LOG.info("Histórico encontrado!");
        }
        return historicoTrabalhador;
    }

    public void setPrimeiroRegistro(Trabalhador trabalhador) {
        String registro = "Trabalhador "+trabalhador.getNome()+" possui " +trabalhador.getTrilha().size()+
                " trilha(s) executada(s) e vinculada(s) ao Setor: "+trabalhador.getSetor().getNome()+
                " ou Função: "+trabalhador.getFuncao().getNome()+"\n";

        HistoricoTrabalhador historicoTrabalhador = new HistoricoTrabalhador(registro,trabalhador);
        this.historicoTrabalhadorDAO.create(historicoTrabalhador);
        trabalhador.setHistoricoTrabalhador(historicoTrabalhador);
        this.LOG.info("Registro do trabalhador: "+trabalhador.getHistoricoTrabalhador().getTrabalhadorRegistro());
    }

    public void setRegistroEmpresa(Trabalhador trabalhador, Empresa empresa) {
        if(!trabalhador.getEmpresa().getCnpj().equals(empresa.getCnpj())) {
            this.LOG.info("registrando mudança de empresa...");
            this.trabalhadorDAO.convertToMerge(trabalhador);
            String registro = trabalhador.getHistoricoTrabalhador().getTrabalhadorRegistro()+
                    "Trabalhador "+trabalhador.getNome()+
                    " mudou para a empresa "+empresa.getNome()+"\n";
            trabalhador.getHistoricoTrabalhador().setTrabalhadorRegistro(registro);
            HistoricoTrabalhador historicoTrabalhador = trabalhador.getHistoricoTrabalhador();
            this.historicoTrabalhadorDAO.update(historicoTrabalhador);
        }
    }

    public void setRegistroSetor(Trabalhador trabalhador, Setor setor) {
        if(!trabalhador.getSetor().getNome().equals(setor.getNome())) {
            this.LOG.info("registrando mudança de setor...");
            this.trabalhadorDAO.convertToMerge(trabalhador);
            String registro = trabalhador.getHistoricoTrabalhador().getTrabalhadorRegistro()+
                    "Trabalhador "+trabalhador.getNome()+" mudou para o setor "+setor.getNome()+"\n";
            trabalhador.getHistoricoTrabalhador().setTrabalhadorRegistro(registro);
            HistoricoTrabalhador historicoTrabalhador = trabalhador.getHistoricoTrabalhador();
            this.historicoTrabalhadorDAO.update(historicoTrabalhador);

        }
    }

    public void setRegistroFuncao(Trabalhador trabalhador, Funcao funcao) {
        if(!trabalhador.getFuncao().getNome().equals(funcao.getNome())) {
            this.LOG.info("Registrando mudança de função...");
            this.trabalhadorDAO.convertToMerge(trabalhador);
            trabalhador.setDataAlteracaoDaFuncao(OffsetDateTime.now());
            String registro = trabalhador.getHistoricoTrabalhador().getTrabalhadorRegistro()+
                    "Trabalhador "+trabalhador.getNome()+" mudou para a função "+funcao.getNome()+"\n";
            trabalhador.getHistoricoTrabalhador().setTrabalhadorRegistro(registro);
            HistoricoTrabalhador historicoTrabalhador = trabalhador.getHistoricoTrabalhador();
            this.historicoTrabalhadorDAO.update(historicoTrabalhador);
        }
    }
}