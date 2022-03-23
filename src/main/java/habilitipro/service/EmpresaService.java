package habilitipro.service;

import habilitipro.model.dao.EmpresaDAO;
import habilitipro.model.persistence.Empresa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaService {

    private final Logger LOG = LogManager.getLogger(EmpresaService.class);

    private final String CNPJ_TEMPLATE = "\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";

    private EntityManager em;

    private EmpresaDAO empresaDAO;

    public EmpresaService(EntityManager em) {
        this.em = em;
        this.empresaDAO = new EmpresaDAO(em);
    }

    public void create(Empresa empresa) {
        this.LOG.info("Preparando criação da empresa...");
        validateNullEmpresa(empresa);

        Empresa empresa1 = this.findByCnpj(empresa.getCnpj());

        if(empresa1 != null) {
            this.LOG.info("Essa empresa já se encontra em nossa base de dados.");
            throw new EntityExistsException("Empresa already exists");
        } else {
            validateCnpjTemplate(empresa.getCnpj());
        }

        this.LOG.info("Verificando se a empresa é matriz ou filial...");
            empresa.setNomeFilial(empresa.getNomeFilial());

        try {
            beginTransaction();
            this.empresaDAO.create(empresa);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao criar a empresa: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Empresa");
        }
        this.LOG.info("Criação da empresa realizada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção da empresa...");

        validateNullId(id);

        this.LOG.info("Verificando se existe empresa com o id informado...");
        Empresa empresa = this.empresaDAO.getById(id);
        validateNullEmpresa(empresa);

        this.LOG.info("Empresa encontrada! Iniciando deleção...");

        try {
            beginTransaction();
            this.empresaDAO.delete(empresa);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao deletar a empresa: "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Empresa");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Empresa newEmpresa, Long empresaId) {
        validateNullEmpresa(newEmpresa);
        validateNullId(empresaId);
        this.LOG.info("Validando existência de empresa com o Id informado");
        Empresa empresa = this.empresaDAO.getById(empresaId);
        validateNullEmpresa(empresa);
        this.LOG.info("Empresa encontrada! Iniciando atualização...");

        try {
            beginTransaction();
            this.empresaDAO.udpate(empresa);
            validateDuplicate(newEmpresa,empresaId);
            validateCnpjTemplate(newEmpresa.getCnpj());
            empresa.setNome(newEmpresa.getNome());
            empresa.setCnpj(newEmpresa.getCnpj());
            empresa.setSegmento(newEmpresa.getSegmento());
            empresa.setCidade(newEmpresa.getCidade());
            empresa.setEstado(newEmpresa.getEstado());
            empresa.setRegional(newEmpresa.getRegional());
            empresa.setMatriz(newEmpresa.isMatriz());
            empresa.setNomeFilial(newEmpresa.getNomeFilial());
            commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Erro ao atualizar empresa: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Empresa");
        }
        this.LOG.info("Empresa atualizada com sucesso!");
    }

    public List<Empresa> listAll() {
        this.LOG.info("Preparando listagem das empresas...");
        List<Empresa> empresas = this.empresaDAO.listAll();
        validateNullList(empresas);

        if(empresas != null) {
            this.LOG.info(empresas.size()+" empresa(s) encontrada(s)");
        }
        return empresas;
    }

    public List<Empresa> listByName(String nome) {
        this.LOG.info("Preparando listagem de empresas pelo nome...");
        validateNullName(nome);
        List<Empresa> empresas = this.empresaDAO.listByName(nome.toLowerCase());
        validateNullList(empresas);

        if(empresas != null) {
            this.LOG.info(empresas.size()+" empresa(s) encontrada(s)");
        }
        return empresas;
    }

    public Empresa findByCnpj(String cnpj) {
        validateNullCnpj(cnpj);
        try {
               Empresa empresa = this.empresaDAO.findByCnpj(cnpj);
               this.LOG.info("Empresa encontrada!");
               return empresa;
           }catch (NoResultException e) {
               this.LOG.info("Não foi encontrada uma empresa com o cnpj informado");
               return null;
           }
    }

    public Empresa getById(Long id) {
        this.LOG.info("Preparando para buscar a empresa pelo id...");
        validateNullId(id);

        this.LOG.info("Verificando se existe empresa com o id informado...");
        Empresa empresa = this.empresaDAO.getById(id);
        validateNullEmpresa(empresa);

        if(empresa != null ) {
            this.LOG.info("Empresa encontrada!");
        }
        return empresa;
    }

    public void validateDuplicate(Empresa empresa, Long id) {
        Empresa empresa1 = this.getById(id);

        if(!empresa1.getCnpj().equals(empresa.getCnpj())){
            this.LOG.info("Verificando se o cnpj atualizado já existe");
            Empresa empresa2 = this.findByCnpj(empresa.getCnpj());

            if(empresa2 != null) {
                this.LOG.info("Essa empresa já se encontra em nossa base de dados.");
                throw new EntityExistsException("Empresa already exists");
            }
        }

    }

    private void validateCnpjTemplate(String cnpj) {
        this.LOG.info("Verificando se o cnpj está correto...");
        if(!cnpj.matches(this.CNPJ_TEMPLATE)){
            this.LOG.error("O formato do cnpj está incorreto");
            throw new RuntimeException("Wrong cnpj format");
        }
    }

    private List<Empresa> validateNullList(List<Empresa> empresas) {
        this.LOG.info("Verificando se existe registros de empresa");
        if(empresas == null) {
            this.LOG.info("Não foram encontradas empresas.");
            return new ArrayList<>();
        }
        return empresas;
    }

    private void validateNullName(String nome) {
        this.LOG.info("Verificando se o nome informado é nulo");
        if (nome == null || nome.isEmpty() || nome.isBlank()) {
            this.LOG.error("O nome informado é vazio ou nulo");
            throw new RuntimeException("nome is empty or null");
        }
    }

    private void validateNullCnpj(String cnpj) {
        this.LOG.info("Verificando se o CNPJ informado é nulo...");
        if(cnpj == null) {
            this.LOG.error("O cnpj informado é nulo");
            throw new RuntimeException("CNPJ is null");
        }
    }

    private void validateNullId(Long id) {
        this.LOG.info("Verificando se o id informado é nulo...");
        if(id == null) {
            this.LOG.error("O id informado é nulo");
            throw new RuntimeException("Id is null");
        }
    }

    private void validateNullEmpresa(Empresa empresa) {
        this.LOG.info("Verificando se a empresa é nula...");
        if(empresa == null) {
            this.LOG.error("Entidade Empresa não encontrada");
            throw new EntityNotFoundException("Entity Empresa not found");
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
