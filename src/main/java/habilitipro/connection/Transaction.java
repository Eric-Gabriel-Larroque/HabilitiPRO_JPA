package habilitipro.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;

public class Transaction {

    private final Logger LOG = LogManager.getLogger(Transaction.class);

    private EntityManager em;

    public Transaction(EntityManager em) {
        this.em = em;
    }

    public void beginTransaction() {
        this.LOG.info("Iniciando transação...");
        this.em.getTransaction().begin();
    }

    public void commitAndClose() {
        this.LOG.info("Commitando e fechando transação...");
        this.em.getTransaction().commit();
        this.em.close();
    }
}
