package habilitipro.service;

import habilitipro.model.dao.EmpresaDAO;
import habilitipro.model.persistence.Empresa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

public class EmpresaService {

    private final Logger LOG = LogManager.getLogger(EmpresaService.class);

    private EntityManager em;

    private EmpresaDAO empresaDAO;

    public EmpresaService(EntityManager em) {
        this.em = em;
        this.empresaDAO = new EmpresaDAO(em);
    }

    public void create(Empresa empresa) {
        validateNullEmpresa(empresa);


    }

    private void validateNullEmpresa(Empresa empresa) {
        this.LOG.info("Verificando se a empresa é nula...");
        if(empresa == null) {
            this.LOG.error("A entidade empresa é nula");
            throw new EntityNotFoundException("Entity Empresa is null");
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
