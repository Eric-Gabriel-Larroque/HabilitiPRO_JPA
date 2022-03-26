package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.TrabalhadorDAO;
import habilitipro.model.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class TrabalhadorService {

    private final Logger LOG = LogManager.getLogger(TrabalhadorService.class);

    private EntityManager em;

    private TrabalhadorDAO trabalhadorDAO;

    private FuncaoService funcaoService;

    private TrilhaService trilhaService;

    private EmpresaService empresaService;

    private SetorService setorService;

    private OcupacaoService ocupacaoService;

    private ModuloService moduloService;

    private HistoricoTrabalhadorService historicoTrabalhadorService;

    private Transaction transaction;

    public TrabalhadorService(EntityManager em) {
        this.em = em;
        this.trabalhadorDAO = new TrabalhadorDAO(em);
        this.funcaoService = new FuncaoService(em);
        this.trilhaService = new TrilhaService(em);
        this.empresaService = new EmpresaService(em);
        this.setorService = new SetorService(em);
        this.ocupacaoService = new OcupacaoService(em);
        this.moduloService = new ModuloService(em);
        this.historicoTrabalhadorService = new HistoricoTrabalhadorService(em);
        this.transaction = new Transaction(em);
    }

    public void create(Trabalhador trabalhador) {
        this.LOG.info("Preparando para criação de trabalhador...");
        validateNullObject(trabalhador,"trabalhador");

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

        this.LOG.info("Verificando se já existe o setor informado...");
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

        this.LOG.info("Verificando se os módulos já existem");
        trabalhador.getModulos().forEach(m->{
            Modulo modulo = this.moduloService.findByName(m.getNome());



            if(modulo != null) {
                m = modulo;
            }

            if(!trabalhador.getTrilha().contains(m.getTrilha())) {
                this.LOG.error("O módulo informado não faz parte desta trilha.");
                throw new RuntimeException("Entity Modulo does not make part of entity Trilha");
            }
        });

        try {
            transaction.beginTransaction();
            this.trabalhadorDAO.create(trabalhador);
            this.trabalhadorDAO.update(trabalhador);
           this.historicoTrabalhadorService.setPrimeiroRegistro(trabalhador);
            transaction.commitAndClose();
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
        validateNullObject(trabalhador,"trabalhador");
        this.LOG.info("Trabalhador encontrado! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.trabalhadorDAO.delete(trabalhador);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao tentar deletar trabalhador: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Trabalhador");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Trabalhador newTrabalhador, Long trabalhadorId) {
        this.LOG.info("Preparando para atualização do trabalhador...");
        validateNullId(trabalhadorId);
        validateNullObject(newTrabalhador,"trabalhador");
        this.LOG.info("Verificando se existe trabalhador com o Id informado...");
        Trabalhador trabalhador = this.trabalhadorDAO.getById(trabalhadorId);
        validateNullObject(trabalhador,"trabalhador");
        this.LOG.info("Trabalhador encontrado! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.trabalhadorDAO.update(trabalhador);

            validateCpfTemplate(newTrabalhador.getCpf());
            trabalhador.setCpf(newTrabalhador.getCpf());
            trabalhador.setNome(newTrabalhador.getNome());


            this.LOG.info("Verificando se a trilha pertence a mesma empresa do trabalhador");
            if(!newTrabalhador.getTrilha().stream().allMatch(t->t.getEmpresa()
                    .getCnpj().equals(newTrabalhador.getEmpresa().getCnpj()))){
                this.LOG.info("Há uma ou mais trilhas vinculadas à uma empresa diferente");
                throw new RuntimeException("One or more Trilhas are assigned to another Empresa");
            }

            this.LOG.info("Verificando se a empresa do trabalhador já existe");
            Empresa empresa = this.empresaService.findByCnpj(newTrabalhador.getEmpresa().getCnpj());

            if(empresa != null) {
                this.historicoTrabalhadorService.setRegistroEmpresa(trabalhador,empresa);
                trabalhador.setEmpresa(empresa);
                trabalhador.getTrilha().forEach(t->t.setEmpresa(empresa));
            }else{
                this.historicoTrabalhadorService.setRegistroEmpresa(trabalhador,newTrabalhador.getEmpresa());
                trabalhador.setEmpresa(newTrabalhador.getEmpresa());
                trabalhador.getTrilha().forEach(t->t.setEmpresa(empresa));
            }

            this.LOG.info("Verificando se já existe a função informada...");
            Funcao funcao = this.funcaoService.findByName(newTrabalhador.getFuncao().getNome());

            if(funcao != null) {
                this.historicoTrabalhadorService.setRegistroFuncao(trabalhador,funcao);
                trabalhador.setFuncao(funcao);
            }else {
                this.historicoTrabalhadorService.setRegistroFuncao(trabalhador,newTrabalhador.getFuncao());
                trabalhador.setFuncao(newTrabalhador.getFuncao());
            }

            this.LOG.info("Verificando se já existe o setor informada...");
            Setor setor = this.setorService.findByName(newTrabalhador.getSetor().getNome());

            if(setor != null) {
                this.historicoTrabalhadorService.setRegistroSetor(trabalhador,setor);
                trabalhador.setSetor(setor);
            }else {
                this.historicoTrabalhadorService.setRegistroSetor(trabalhador, newTrabalhador.getSetor());
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

            this.LOG.info("Verificando se existem os módulo informados...");

            for(Modulo newModulo: newTrabalhador.getModulos()) {
                for(Modulo modulo: trabalhador.getModulos()) {
                    Modulo modulo1 = this.moduloService.findByName(newModulo.getNome());

                    if(modulo1 != null) {
                        modulo = modulo1;
                    }else {
                        modulo = newModulo;
                    }

                    if(!modulo1.getTrilha().getNome().equals(modulo.getTrilha().getNome())) {
                        this.LOG.error("Esse módulo não faz parte da trilha informada");
                        throw new RuntimeException("Entity Modulo arent asigned to this entity Trilha");
                    }
                }
            }

            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao atualizar entidade Trabalhador: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Trabalhador");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }

    public List<Trabalhador> listAll() {
        this.LOG.info("Preparando para listagem dos Trabalhadores...");
        List<Trabalhador> trabalhadores = this.trabalhadorDAO.listAll();
        validateNullList(Collections.singletonList(trabalhadores),"trabalhador");
        if(trabalhadores != null) {
            this.LOG.info(trabalhadores.size()+" trabalhador(es) encontrado(s)");
        }
        return trabalhadores;
    }

    public List<Trabalhador> listByName(String nome) {
        this.LOG.info("Preparando para listagem dos Trabalhadores pelo nome...");
        validateNullString(nome,"nome");
        List<Trabalhador> trabalhadores = this.trabalhadorDAO.listByName(nome);
        validateNullList(Collections.singletonList(trabalhadores),"trabalhador");
        if(trabalhadores != null) {
            this.LOG.info(trabalhadores.size()+" trabalhador(es) encontrado(s)");
        }
        return trabalhadores;
    }


    public Trabalhador findByCpf(String cpf) {
        this.LOG.info("Preparando para buscar trabalhador pelo cpf...");
        validateNullString(cpf,"cpf");

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
        this.LOG.info("Verificando se existe Trabalhador com o Id informado...");
        Trabalhador trabalhador = this.trabalhadorDAO.getById(id);
        validateNullObject(trabalhador,"trabalhador");
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
}