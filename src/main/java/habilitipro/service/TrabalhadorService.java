package habilitipro.service;

import habilitipro.model.dao.TrabalhadorDAO;
import habilitipro.model.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class TrabalhadorService {

    private final Logger LOG = LogManager.getLogger(TrabalhadorService.class);

    private EntityManager em;

    private TrabalhadorDAO trabalhadorDAO;

    private FuncaoService funcaoService;

    private TrilhaService trilhaService;

    private EmpresaService empresaService;

    private SetorService setorService;

    private OcupacaoService ocupacaoService;

    private final String CPF_TEMPLATE = "\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";

    public TrabalhadorService(EntityManager em) {
        this.em = em;
        this.trabalhadorDAO = new TrabalhadorDAO(em);
        this.funcaoService = new FuncaoService(em);
        this.trilhaService = new TrilhaService(em);
        this.empresaService = new EmpresaService(em);
        this.setorService = new SetorService(em);
        this.ocupacaoService = new OcupacaoService(em);
    }

    public void create(Trabalhador trabalhador) {
        this.LOG.info("Preparando para criação de trabalhador...");
        validateNullTrabalhador(trabalhador);

        Trabalhador trabalhador1 = this.findByCpf(trabalhador.getCpf());

        if(trabalhador1 != null) {
            this.LOG.info("Esse trabalhador já se encontra em nossa base de dados.");
            throw new EntityExistsException("Trabalhador already exists");
        }else {
            validateCpfTemplate(trabalhador.getCpf());
        }


        this.LOG.info("Verificando se a trilha pertence a mesma empresa do trabalhador");
        if(!trabalhador.getTrilha().stream().allMatch(t->t.getEmpresa()
                .getCnpj().equals(trabalhador.getEmpresa().getCnpj()))){
            this.LOG.info("Há uma ou mais trilhas vinculadas à uma empresa diferente");
            throw new RuntimeException("One or more Trilhas are assigned to another Empresa");
        }

        this.LOG.info("Verificando se a empresa do trabalhador já existe");
        Empresa empresa = this.empresaService.findByCnpj(trabalhador.getEmpresa().getCnpj());

        if(empresa != null) {
            trabalhador.setEmpresa(empresa);
            trabalhador.getTrilha().forEach(t->t.setEmpresa(empresa));
        }

        this.LOG.info("Verificando se já existe a função informada...");
        Funcao funcao = this.funcaoService.findByName(trabalhador.getFuncao().getNome());

        if(funcao != null) {
            trabalhador.setFuncao(funcao);
        }

        this.LOG.info("Verificando se já existe o setor informada...");
        Setor setor = this.setorService.findByName(trabalhador.getSetor().getNome());

        if(setor != null) {
            trabalhador.setSetor(setor);
        }

        this.LOG.info("Verificando se já existe a trilha informada...");
        trabalhador.getTrilha().forEach(t->{
            this.trilhaService.setNome(t);
            this.trilhaService.setApelido(t);
            Trilha trilha = this.trilhaService.findByName(t.getNome());

            if(trilha != null) {
                t = trilha;
            }
            Ocupacao ocupacao = this.ocupacaoService.findByName(t.getOcupacao().getNome());

            if(ocupacao != null) {
                t.setOcupacao(ocupacao);
            }
        });

        try {
            beginTransaction();
            this.trabalhadorDAO.create(trabalhador);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao tentar criar trabalhador: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Trabalhador");
        }
        this.LOG.info("Criação realizada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção do trabalhador");
        validateNullId(id);

        this.LOG.info("Verificando se existe trabalhador com o Id informado...");
        Trabalhador trabalhador = this.trabalhadorDAO.getById(id);
        validateNullTrabalhador(trabalhador);
        this.LOG.info("Trabalhador encontrado! Iniciando deleção...");

        try {
            beginTransaction();
            this.trabalhadorDAO.delete(trabalhador);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao tentar deletar trabalhador: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Trabalhador");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Trabalhador newTrabalhador, Long trabalhadorId) {
        this.LOG.info("Preparando para atualização do trabalhador...");
        validateNullId(trabalhadorId);
        validateNullTrabalhador(newTrabalhador);
        this.LOG.info("Verificando se existe trabalhador com o Id informado...");
        Trabalhador trabalhador = this.trabalhadorDAO.getById(trabalhadorId);
        validateNullTrabalhador(trabalhador);
        this.LOG.info("Trabalhador encontrado! Iniciando atualização...");

        if(!trabalhador.getFuncao().getNome().equals(newTrabalhador.getFuncao().getNome())) {
            trabalhador.setDataAlteracaoDaFuncao(OffsetDateTime.now());
        }

        try {
            beginTransaction();
            this.trabalhadorDAO.update(trabalhador);

            this.LOG.info("Verificando se a trilha pertence a mesma empresa do trabalhador");
            if(!newTrabalhador.getTrilha().stream().allMatch(t->t.getEmpresa()
                    .getCnpj().equals(newTrabalhador.getEmpresa().getCnpj()))){
                this.LOG.info("Há uma ou mais trilhas vinculadas à uma empresa diferente");
                throw new RuntimeException("One or more Trilhas are assigned to another Empresa");
            }

            this.LOG.info("Verificando se a empresa do trabalhador já existe");
            Empresa empresa = this.empresaService.findByCnpj(newTrabalhador.getEmpresa().getCnpj());

            if(empresa != null) {
                trabalhador.setEmpresa(empresa);
                trabalhador.getTrilha().forEach(t->t.setEmpresa(empresa));
            }else{
                trabalhador.setEmpresa(newTrabalhador.getEmpresa());
                trabalhador.getTrilha().forEach(t->t.setEmpresa(empresa));
            }

            this.LOG.info("Verificando se já existe a função informada...");
            Funcao funcao = this.funcaoService.findByName(newTrabalhador.getFuncao().getNome());

            if(funcao != null) {
                trabalhador.setFuncao(funcao);
            }else {
                trabalhador.setFuncao(newTrabalhador.getFuncao());
            }

            this.LOG.info("Verificando se já existe o setor informada...");
            Setor setor = this.setorService.findByName(newTrabalhador.getSetor().getNome());

            if(setor != null) {
                trabalhador.setSetor(setor);
            }else {
                trabalhador.setSetor(newTrabalhador.getSetor());
            }

            this.LOG.info("Verificando se já existe a trilha informada...");
            for(Trilha newTrilha: newTrabalhador.getTrilha()) {
                for(Trilha trilha: trabalhador.getTrilha()) {
                    this.trilhaService.setNome(newTrilha);
                    this.trilhaService.setApelido(newTrilha);

                    Trilha trilha1 = this.trilhaService.findByName(newTrilha.getNome());

                    if(trilha1 != null) {
                        trilha1.setEmpresa(trilha.getEmpresa());
                        trilha = trilha1;
                    }else {
                        newTrilha.setEmpresa(trilha.getEmpresa());
                        trilha = newTrilha;
                    }

                    Ocupacao ocupacao = this.ocupacaoService.findByName(newTrilha.getOcupacao().getNome());

                    if(ocupacao != null) {
                        trilha.setOcupacao(ocupacao);
                    }else {
                        trilha.setOcupacao(newTrilha.getOcupacao());
                    }
                }
            }
            validateCpfTemplate(newTrabalhador.getCpf());
            trabalhador.setCpf(newTrabalhador.getCpf());
            trabalhador.setNome(newTrabalhador.getNome());

            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao atualizar entidade Trabalhador: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Trabalhador");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Trabalhador> listAll() {
        this.LOG.info("Preparando para listagem dos Trabalhadores...");
        List<Trabalhador> trabalhadores = this.trabalhadorDAO.listAll();
        validateNullList(trabalhadores);
        if(trabalhadores != null) {
            this.LOG.info(trabalhadores.size()+" trabalhador(es) encontrado(s)");
        }
        return trabalhadores;
    }

    public List<Trabalhador> listByName(String nome) {
        this.LOG.info("Preparando para listagem dos Trabalhadores pelo nome...");
        validateNullName(nome);
        List<Trabalhador> trabalhadores = this.trabalhadorDAO.listByName(nome);
        validateNullList(trabalhadores);
        if(trabalhadores != null) {
            this.LOG.info(trabalhadores.size()+" trabalhador(es) encontrado(s)");
        }
        return trabalhadores;
    }


    public Trabalhador findByCpf(String cpf) {
        this.LOG.info("Preparando para buscar trabalhador pelo cpf...");
        validateNullCpf(cpf);

        try {
            this.LOG.info("Verificando se existe trabalhador com o cpf informado...");
            Trabalhador trabalhador = this.trabalhadorDAO.findByCpf(cpf);
            this.LOG.info("Trabalhador encontrado!");
            return trabalhador;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrado um trabalhador com o cpf informado.");
            return null;
        }
    }

    public Trabalhador getById(Long id) {
        validateNullId(id);
        this.LOG.info("Verificano se existe Trabalhador com o Id informado...");
        Trabalhador trabalhador = this.trabalhadorDAO.getById(id);
        validateNullTrabalhador(trabalhador);
        if(trabalhador != null) {
            this.LOG.info("Trabalhador encontrado!");
        }
        return trabalhador;
     }

    private void validateDuplicate(Trabalhador trabalhador)  {
        this.LOG.info("Verificando se já existe trabalhador com esse cpf...");
        Trabalhador trabalhador1 = this.trabalhadorDAO.findByCpf(trabalhador.getCpf());
        if(trabalhador1 != null) {
            this.LOG.error("O usuário informado já existe no banco de dados");
            throw new EntityExistsException("Entity Usuario already exists");
        }
    }

    public List<Trabalhador> validateNullList(List<Trabalhador> trabalhadores) {
        this.LOG.info("Verificando se existe registros de trabalhador");
        if(trabalhadores == null) {
            this.LOG.info("Não foram encontrados trabalhadores.");
            return new ArrayList<>();
        }
        return trabalhadores;
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

    private void validateNullCpf(String cpf) {
        this.LOG.info("Verificando se o cpf informado é nulo...");
        if(cpf == null || cpf.isEmpty() || cpf.isBlank()) {
            this.LOG.error("O cpf informado é vazio ou nulo");
            throw new RuntimeException("cpf is empty or null");
        }
    }

    private void validateNullTrabalhador(Trabalhador trabalhador) {
        this.LOG.info("Verificando se o trabalhador é nulo...");
        if(trabalhador == null) {
            this.LOG.error("Entidade trabalhador não encontrado");
            throw new EntityNotFoundException("Entity Trabalhador not found");
        }
    }

    private void validateCpfTemplate(String cpf) {
        this.LOG.info("Verificando se o cpf está correto...");
        if(!cpf.matches(this.CPF_TEMPLATE)){
            this.LOG.error("O formato do cpf está incorreto");
            throw new RuntimeException("Wrong cpf format");
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