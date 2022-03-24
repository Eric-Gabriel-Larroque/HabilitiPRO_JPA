package habilitipro.service;

import habilitipro.enums.Status;
import habilitipro.model.dao.TrilhaDAO;
import habilitipro.model.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrilhaService {

    private final Logger LOG = LogManager.getLogger(TrilhaService.class);

    private EntityManager em;

    private TrilhaDAO trilhaDAO;

    private OcupacaoService ocupacaoService;

    private EmpresaService empresaService;


    public static List<Trilha> trilhas = new ArrayList<>();

    public TrilhaService(EntityManager em) {
        this.em = em;
        this.trilhaDAO = new TrilhaDAO(em);
        this.ocupacaoService = new OcupacaoService(em);
        this.empresaService = new EmpresaService(em);
    }

    public void create(Trilha trilha) {
        this.LOG.info("Preparando para criação da trilha...");
        validateNullTrilha(trilha);

        this.LOG.info("Verificando se já existe empresa com o cnpj informado...");
        Empresa empresa = this.empresaService.findByCnpj(trilha.getEmpresa().getCnpj());

        if(empresa != null) {
            trilha.setEmpresa(empresa);
        }

        this.LOG.info("Verificando se já existe ocupação com o nome informado...");
        Ocupacao ocupacao = this.ocupacaoService.findByName(trilha.getOcupacao().getNome());

        if(ocupacao != null) {
            trilha.setOcupacao(ocupacao);
        }

        setNome(trilha);
        setApelido(trilha);

        try {
            beginTransaction();
            this.trilhaDAO.create(trilha);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao criar trilha: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Trilha");
        }
        this.LOG.info("Criação realizada com sucesso!");
    }

    public void delete(Long id) {
        validateNullId(id);
        this.LOG.info("Verificando se existe trilha com o Id informado...");
        Trilha trilha = this.trilhaDAO.getById(id);
        validateNullTrilha(trilha);
        this.LOG.info("Trilha encontrada! Iniciando deleção...");
        trilhas.remove(trilha);
        try {
            beginTransaction();
            this.trilhaDAO.delete(trilha);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao deletar a trilha: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Trilha");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Trilha newTrilha, Long trilhaId) {
        this.LOG.info("Preparando para atualizar a trilha...");
        validateNullId(trilhaId);
        validateNullTrilha(newTrilha);

        this.LOG.info("Verificando existência da trilha com o id informado...");
        Trilha trilha = this.trilhaDAO.getById(trilhaId);
        validateNullTrilha(trilha);
        this.LOG.info("Trilha encontrada! Iniciando atualização...");

        if(trilha.getOcupacao().getNome().equals(newTrilha.getOcupacao().getNome())&&
                trilha.getEmpresa().getCnpj().equals(newTrilha.getEmpresa().getCnpj())){
            this.LOG.info("A trilha que você quer atualizar já contém a mesma empresa e ocupação da nova trilha.");
            return;
        }

        try {
            beginTransaction();
            this.trilhaDAO.update(trilha);

            this.LOG.info("Verificando se já existe empresa com cnpj informado...");
            Empresa empresa = this.empresaService.findByCnpj(newTrilha.getEmpresa().getCnpj());

            if(empresa != null) {
                trilha.setEmpresa(empresa);
            } else {
                trilha.setEmpresa(newTrilha.getEmpresa());
            }

            this.LOG.info("Verificando se já existe ocupação com o nome informado...");
            Ocupacao ocupacao = this.ocupacaoService.findByName(newTrilha.getOcupacao().getNome());

            if(ocupacao != null) {
                trilha.setOcupacao(ocupacao);
            } else {
                trilha.setOcupacao(newTrilha.getOcupacao());
            }

            setNome(trilha);
            setApelido(trilha);

            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao tentar atualizar a trilha: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Trilha");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Trilha> listAll() {
        this.LOG.info("Preparando listagem das trilhas...");
        List<Trilha> trilhas = this.trilhaDAO.listAll();
        validateNullList(trilhas);

        if(trilhas != null) {
            this.LOG.info(trilhas.size()+" trilha(s) encontrada(s)");
        }
        return trilhas;
    }

    public List<Trilha> listByApelido(String apelido) {
        this.LOG.info("Preparando listagem de trilhas pelo nome...");
        validateNullName(apelido);
        List<Trilha> trilhas = this.trilhaDAO.listByApelido(apelido.toLowerCase());
        validateNullList(trilhas);

        if(trilhas != null){
            this.LOG.info(trilhas.size()+" trilha(s) encontrada(s)");
        }
        return trilhas;
    }

    public Trilha findByName(String nome) {
        validateNullName(nome);
        try {
            this.LOG.info("Verificando se existe trilha com o nome informado...");
            Trilha trilha = this.trilhaDAO.findByName(nome.toLowerCase());
            this.LOG.info("Trilha encontrada!");
            return trilha;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrada trilha com o nome informado");
            return null;
        }catch (NonUniqueResultException e) {
            this.LOG.info("Há mais de um resultado para o nome informado");
            return null;
        }
    }

    public Trilha getById(Long id) {
        this.LOG.info("Preparando busca de trilha pelo Id...");
        validateNullId(id);
        this.LOG.info("Verificando se existe trilha com o id informado...");
        Trilha trilha = this.trilhaDAO.getById(id);
        validateNullTrilha(trilha);
        if(trilha != null) {
            this.LOG.info("Trilha encontrada!");
        }
        return trilha;
    }

    private List<Trilha> validateNullList(List<Trilha> trilhas) {
        this.LOG.info("Verificando se existe registros de trilha");
        if(trilhas == null) {
            this.LOG.info("Não foram encontradas trilhas.");
            return new ArrayList<>();
        }
        return trilhas;
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

    private void validateNullTrilha(Trilha trilha) {
        this.LOG.info("Verificando se a trilha é nula...");
        if(trilha == null) {
            this.LOG.error("Entidade Trilha não encontrada");
            throw new EntityNotFoundException("Entity Trilha not found");
        }
    }

    public void setNome(Trilha trilha) {
        validateNullTrilha(trilha);
        String nome = trilha.getOcupacao().getNome()+trilha.getEmpresa().getNome()+
                getNumeroSequencial(trilha)+ LocalDate.now().getYear();
        trilha.setNome(nome);
    }

    public void setApelido(Trilha trilha) {
        String apelido = trilha.getOcupacao().getNome()+getNumeroSequencial(trilha);
        trilha.setApelido(apelido);
    }


    public int getNumeroSequencial(Trilha trilha) {
        List<Trilha> trilhas = this.listAll();
        int length = trilhas.stream()
                .filter(t -> t.getEmpresa().equals(trilha.getEmpresa()) &&
                        t.getOcupacao().getNome().equals(trilha.getOcupacao().getNome()))
                .toArray().length;
        this.LOG.info(length);
        return length>0?length+1:1;

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