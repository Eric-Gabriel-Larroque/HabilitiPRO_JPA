package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.model.dao.EmpresaDAO;
import habilitipro.model.persistence.Empresa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

import static habilitipro.util.Validation.*;

public class EmpresaService {

    private final Logger LOG = LogManager.getLogger(EmpresaService.class);

    private EntityManager em;

    private EmpresaDAO empresaDAO;

    private Transaction transaction;

    public EmpresaService(EntityManager em) {
        this.em = em;
        this.empresaDAO = new EmpresaDAO(em);
        this.transaction = new Transaction(em);
    }

    public void create(Empresa empresa) {
        this.LOG.info("Preparando criação da empresa...");
        validateNullObject(empresa, "empresa");

        Empresa empresa1 = this.findByCnpj(empresa.getCnpj());

        if (empresa1 != null) {
            this.LOG.info("Essa empresa já se encontra em nossa base de dados.");
            throw new EntityExistsException("Empresa already exists");
        } else {
            validateCnpjTemplate(empresa.getCnpj());
        }

        this.LOG.info("Verificando se a empresa é matriz ou filial...");
        empresa.setNomeFilial(empresa.getNomeFilial());

        try {
            transaction.beginTransaction();
            this.empresaDAO.create(empresa);
            transaction.commitAndClose();
        } catch (Exception e) {
            this.LOG.error("Erro ao criar a empresa: " + e.getMessage());
            throw new RuntimeException("Failed to create entity Empresa");
        }
        this.LOG.info("Criação da empresa realizada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção da empresa...");

        validateNullId(id);

        this.LOG.info("Verificando se existe empresa com o id informado...");
        Empresa empresa = this.empresaDAO.getById(id);
        validateNullObject(empresa, "empresa");

        this.LOG.info("Empresa encontrada! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.empresaDAO.delete(empresa);
            transaction.commitAndClose();
        } catch (Exception e) {
            this.LOG.error("Erro ao deletar a empresa: " + e.getMessage());
            throw new RuntimeException("Failed to delete entity Empresa");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Empresa newEmpresa, Long empresaId) {
        this.LOG.info("Preparando para atualização de empresa...");
        validateNullObject(newEmpresa, "empresa");
        validateNullId(empresaId);
        this.LOG.info("Validando existência de empresa com o Id informado");
        Empresa empresa = this.empresaDAO.getById(empresaId);
        validateNullObject(empresa, "empresa");
        this.LOG.info("Empresa encontrada! Iniciando atualização...");

        try {
            transaction.beginTransaction();
            this.empresaDAO.udpate(empresa);
            validateDuplicate(newEmpresa, empresaId);
            validateCnpjTemplate(newEmpresa.getCnpj());
            empresa.setNome(newEmpresa.getNome());
            empresa.setCnpj(newEmpresa.getCnpj());
            empresa.setSegmento(newEmpresa.getSegmento());
            empresa.setCidade(newEmpresa.getCidade());
            empresa.setEstado(newEmpresa.getEstado());
            empresa.setRegional(newEmpresa.getRegional());
            empresa.setMatriz(newEmpresa.isMatriz());
            empresa.setNomeFilial(newEmpresa.getNomeFilial());
            transaction.commitAndClose();
        } catch (Exception e) {
            this.LOG.error("Erro ao atualizar empresa: " + e.getMessage());
            throw new RuntimeException("Failed to update entity Empresa");
        }
        this.LOG.info("Empresa atualizada com sucesso!");
    }

    public List<Empresa> listAll() {
        this.LOG.info("Preparando listagem das empresas...");
        List<Empresa> empresas = this.empresaDAO.listAll();
        validateNullList(Collections.singletonList(empresas), "empresa");

        if (empresas != null) {
            this.LOG.info(empresas.size() + " empresa(s) encontrada(s)");
        }
        return empresas;
    }

    public List<Empresa> listByName(String nome) {
        this.LOG.info("Preparando listagem de empresas pelo nome...");
        validateNullString(nome, "nome");
        List<Empresa> empresas = this.empresaDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(empresas), "empresa");

        if (empresas != null) {
            this.LOG.info(empresas.size() + " empresa(s) encontrada(s)");
        }
        return empresas;
    }

    public Empresa findByCnpj(String cnpj) {
        validateNullString(cnpj, "cnpj");
        try {
            Empresa empresa = this.empresaDAO.findByCnpj(cnpj);
            this.LOG.info("Empresa encontrada!");
            return empresa;
        } catch (NoResultException e) {
            this.LOG.info("Não foi encontrada uma empresa com o cnpj informado");
            return null;
        }
    }

    public Empresa getById(Long id) {
        this.LOG.info("Preparando para buscar a empresa pelo id...");
        validateNullId(id);

        this.LOG.info("Verificando se existe empresa com o id informado...");
        Empresa empresa = this.empresaDAO.getById(id);
        validateNullObject(empresa, "empresa");

        if (empresa != null) {
            this.LOG.info("Empresa encontrada!");
        }
        return empresa;
    }

    public void validateDuplicate(Empresa empresa, Long id) {
        Empresa empresa1 = this.getById(id);

        if (!empresa1.getCnpj().equals(empresa.getCnpj())) {
            this.LOG.info("Verificando se o cnpj atualizado já existe");
            Empresa empresa2 = this.findByCnpj(empresa.getCnpj());

            if (empresa2 != null) {
                this.LOG.info("Essa empresa já se encontra em nossa base de dados.");
                throw new EntityExistsException("Empresa already exists");
            }
        }
    }
}