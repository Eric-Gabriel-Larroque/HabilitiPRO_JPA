package habilitipro.service;

import habilitipro.enums.Status;
import habilitipro.model.dao.ModuloDAO;
import habilitipro.model.dao.TrilhaDAO;
import habilitipro.model.persistence.Empresa;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class ModuloService {

    private final Logger LOG = LogManager.getLogger(ModuloService.class);

    private EntityManager em;

    private ModuloDAO moduloDAO;

    private TrilhaService trilhaService;

    private TrilhaDAO trilhaDAO;

    public ModuloService(EntityManager em) {
        this.em = em;
        this.moduloDAO = new ModuloDAO(em);
        this.trilhaService = new TrilhaService(em);
        this.trilhaDAO = new TrilhaDAO(em);

    }

    public void create(Modulo modulo) {
        this.LOG.info("Preparando para criação do módulo...");
        validateNullModulo(modulo);
        validateDuplicate(modulo);

        this.LOG.info("Verificando se já existe trilha do módulo informado...");
        this.trilhaService.setNome(modulo.getTrilha());
        this.trilhaService.setApelido(modulo.getTrilha());
        Trilha trilha = this.trilhaService.findByName(modulo.getTrilha().getNome());

        if(trilha != null) {
            modulo.setTrilha(trilha);
        }

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
        validateNullId(id);
        this.LOG.info("Verificando se existe módulo com o Id informado...");
        Modulo modulo = this.moduloDAO.getById(id);
        validateNullModulo(modulo);
        this.LOG.info("Módulo encontrado! Iniciando deleção...");

        try {
            beginTransaction();
            this.moduloDAO.delete(modulo);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao tentar deletar o módulo: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Modulo");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Modulo newModulo, Long moduloId) {
        this.LOG.info("Preparando para atualizar o módulo");
        validateNullId(moduloId);
        validateNullModulo(newModulo);
        this.LOG.info("Verificando se existe módulo com o id informado...");
        Modulo modulo = this.moduloDAO.getById(moduloId);
        validateNullModulo(modulo);
        this.LOG.info("Módulo encontrado, iniciando atualização...");

        try {
            beginTransaction();
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
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao tentar atualizar Modulo: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Modulo");
        }
        this.LOG.info("Atualização realizada com sucesso!");
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

    public List<Modulo> listByTrilhaId(Long trilha_id) {
        this.LOG.info("Preparando listagem de módulos pelo id da trilha...");
        validateNullId(trilha_id);
        List<Modulo> modulos = this.moduloDAO.listByTrilhaId(trilha_id);
        validateNullList(modulos);

        if(modulos != null) {
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

    public List<Modulo> validateNullList(List<Modulo> modulos) {
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

    private void setDataInicio(Modulo modulo, Status status) {
        this.LOG.info("Verificando se o status foi definido como 'Em andamento'...");
        if (status.equals(Status.EMANDAMENTO)) {
            modulo.setDataInicio(OffsetDateTime.now());
        }
    }

    private void setDataFim(Modulo modulo, Status status) {
        this.LOG.info("Verificando se o status foi definido como 'Em fase de avaliação'...");
         if(status.equals(Status.AVALIACAO)) {
            modulo.setDataFim(OffsetDateTime.now());
         }
    }

    public void setStatus(Modulo modulo, Status status) {
        beginTransaction();
        this.moduloDAO.update(modulo);
        setDataInicio(modulo,status);
        setDataFim(modulo,status);

        if(modulo.getDataFim().plusDays(modulo.getPrazoLimite()).compareTo(OffsetDateTime.now())>=0){
            this.LOG.info("Verificando se o status foi definido como 'Finalizado'...");
            modulo.setStatus(Status.FINALIZADO);
        }
        commitAndClose();
    }

    public void setNivelSatisfacao(Trilha trilha, int nivelSatisfacao) {
        final String NIVEL_SATISFACAO_TEMPLATE = "^[1-5]";
        beginTransaction();
        this.LOG.info("Preparando para inserir o nivel de satisfacao da trilha...");
        trilhaDAO.update(trilha);

        this.LOG.info("Listando módulos que compõem a trilha");
        List<Modulo> modulos = listByTrilhaId(trilha.getId());
        validateNullList(modulos);

        if(modulos != null && String.valueOf(nivelSatisfacao).matches(NIVEL_SATISFACAO_TEMPLATE) &&
                modulos.stream().filter(m->m.getStatus().equals(Status.FINALIZADO))
                        .toArray().length== modulos.size()) {
            trilha.setNivelSatisfacaoGeral(nivelSatisfacao);
        }
        commitAndClose();
    }

    public void setAnotacoes(Trilha trilha, String anotacoes) {
        this.LOG.info("Preparando para inserir as anotações da trilha...");
        beginTransaction();
        trilhaDAO.update(trilha);

        this.LOG.info("Listando módulos que compõem a trilha");
        List<Modulo> modulos = listByTrilhaId(trilha.getId());
        validateNullList(modulos);

        if(modulos != null && modulos.stream().
                filter(m->m.getStatus().equals(Status.FINALIZADO))
                .toArray().length== modulos.size()) {
            trilha.setAnotacoes(anotacoes);
        }
        commitAndClose();
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