package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.enums.Status;
import habilitipro.model.dao.ModuloDAO;
import habilitipro.model.dao.TrilhaDAO;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class ModuloService {

    private final Logger LOG = LogManager.getLogger(ModuloService.class);

    private EntityManager em;

    private ModuloDAO moduloDAO;

    private TrilhaService trilhaService;

    private TrilhaDAO trilhaDAO;

    private Transaction transaction;

    public ModuloService(EntityManager em) {
        this.em = em;
        this.moduloDAO = new ModuloDAO(em);
        this.trilhaService = new TrilhaService(em);
        this.trilhaDAO = new TrilhaDAO(em);
        this.transaction = new Transaction(em);

    }

    public void create(Modulo modulo) {
        this.LOG.info("Preparando para criação do módulo...");
        validateNullObject(modulo,"módulo");
        validateDuplicate(modulo);

        this.LOG.info("Verificando se já existe trilha do módulo informado...");
        this.trilhaService.setNome(modulo.getTrilha());
        this.trilhaService.setApelido(modulo.getTrilha());
        Trilha trilha = this.trilhaService.findByName(modulo.getTrilha().getNome());

        if(trilha != null) {
            modulo.setTrilha(trilha);
        }

        try {
            transaction.beginTransaction();
            this.moduloDAO.create(modulo);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar o módulo: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Modulo");
        }
        this.LOG.info("Criação realizada com sucesso!");
    }

    public void delete(Long id) {
        validateNullId(id);
        this.LOG.info("Verificando se existe módulo com o Id informado...");
        Modulo modulo = this.moduloDAO.getById(id);
        validateNullObject(modulo,"módulo");
        this.LOG.info("Módulo encontrado! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.moduloDAO.delete(modulo);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao tentar deletar o módulo: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Modulo");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Modulo newModulo, Long moduloId) {
        this.LOG.info("Preparando para atualizar o módulo");
        validateNullId(moduloId);
        validateNullObject(newModulo,"módulo");
        this.LOG.info("Verificando se existe módulo com o id informado...");
        Modulo modulo = this.moduloDAO.getById(moduloId);
        validateNullObject(modulo,"módulo");
        this.LOG.info("Módulo encontrado, iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.moduloDAO.update(modulo);
            modulo.setPrazoLimite(newModulo.getPrazoLimite());
            modulo.setHabilidadesTrabalhadas(newModulo.getHabilidadesTrabalhadas());
            modulo.setNome(newModulo.getNome());
            modulo.setTarefaDeValidacao(newModulo.getTarefaDeValidacao());

            this.LOG.info("Verificando se já existe trilha do módulo informado...");
            this.trilhaService.setNome(modulo.getTrilha());
            this.trilhaService.setApelido(modulo.getTrilha());
            Trilha trilha = this.trilhaService.findByName(modulo.getTrilha().getNome());
            if(trilha != null) {
                modulo.setTrilha(trilha);
            }else {
                modulo.setTrilha(newModulo.getTrilha());
            }
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao tentar atualizar Modulo: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Modulo");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Modulo> listAll() {
        this.LOG.info("Preparando listagem dos módulos...");
        List<Modulo> modulos = this.moduloDAO.listAll();
        validateNullList(Collections.singletonList(modulos),"módulo");

        if(modulos != null) {
            this.LOG.info(modulos.size()+" módulo(s) encontrado(s)");
        }
        return modulos;
    }

    public List<Modulo> listByName(String nome) {
        this.LOG.info("Preparando listagem de módulos pelo nome...");
        validateNullString(nome,"nome");
        List<Modulo> modulos = this.moduloDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(modulos),"módulo");

        if(modulos != null){
            this.LOG.info(modulos.size()+" módulo(s) encontrado(s)");
        }
        return modulos;
    }

    public List<Modulo> listByTrilhaId(Long trilha_id) {
        this.LOG.info("Preparando listagem de módulos pelo id da trilha...");
        validateNullId(trilha_id);
        List<Modulo> modulos = this.moduloDAO.listByTrilhaId(trilha_id);
        validateNullList(Collections.singletonList(modulos),"módulo");

        if(modulos != null) {
            this.LOG.info(modulos.size()+" módulo(s) encontrado(s)");
        }
        return modulos;
    }

    public Modulo findByName(String nome) {
        validateNullString(nome,"nome");
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
        validateNullObject(modulo,"módulo");
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

    private void setDataInicio(Modulo modulo, Status status) {
        this.LOG.info("Verificando se o status foi definido como 'Em andamento'...");
        if (status.equals(Status.EMANDAMENTO)) {
            modulo.setDataInicio(OffsetDateTime.now());
            modulo.setStatus(status);
        }
    }

    private void setDataFim(Modulo modulo, Status status) {
        this.LOG.info("Verificando se o status foi definido como 'Em fase de avaliação'...");
         if(status.equals(Status.AVALIACAO)) {
            modulo.setDataFim(OffsetDateTime.now());
            modulo.setStatus(status);
         }
    }

    public void setStatus(Modulo modulo, Status status) {
        transaction.beginTransaction();
        this.moduloDAO.update(modulo);
        setDataInicio(modulo,status);
        setDataFim(modulo,status);

        if(modulo.getDataFim() != null && modulo.getDataFim().plusDays(modulo.getPrazoLimite()).compareTo(OffsetDateTime.now())>=0){
            this.LOG.info("Verificando se o status foi definido como 'Finalizado'...");
            modulo.setStatus(Status.FINALIZADO);
        }
        transaction.commitAndClose();
    }

    public void setNivelSatisfacao(Trilha trilha, int nivelSatisfacao) {
        final String NIVEL_SATISFACAO_TEMPLATE = "^[1-5]";
        transaction.beginTransaction();
        this.LOG.info("Preparando para inserir o nivel de satisfacao da trilha...");
        trilhaDAO.update(trilha);

        this.LOG.info("Listando módulos que compõem a trilha");
        List<Modulo> modulos = listByTrilhaId(trilha.getId());
        validateNullList(Collections.singletonList(modulos),"módulo");

        if(modulos != null && String.valueOf(nivelSatisfacao).matches(NIVEL_SATISFACAO_TEMPLATE) &&
                modulos.stream().filter(m->m.getStatus().equals(Status.FINALIZADO))
                        .toArray().length== modulos.size()) {
            trilha.setNivelSatisfacaoGeral(nivelSatisfacao);
        }
        transaction.commitAndClose();
    }

    public void setAnotacoes(Trilha trilha, String anotacoes) {
        this.LOG.info("Preparando para inserir as anotações da trilha...");
        transaction.beginTransaction();
        trilhaDAO.update(trilha);

        this.LOG.info("Listando módulos que compõem a trilha");
        List<Modulo> modulos = listByTrilhaId(trilha.getId());
        validateNullList(Collections.singletonList(modulos),"módulo");

        if(modulos != null && modulos.stream().
                filter(m->m.getStatus().equals(Status.FINALIZADO))
                .toArray().length== modulos.size()) {
            trilha.setAnotacoes(anotacoes);
        }
        transaction.commitAndClose();
    }
}