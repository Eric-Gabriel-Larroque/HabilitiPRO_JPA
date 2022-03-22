package habilitipro.connection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaConnectionFactory {

    EntityManagerFactory factory;

    public JpaConnectionFactory() {
        this.factory = Persistence
                .createEntityManagerFactory("Projeto_HabilitiPRO_JPA");
    }

    public EntityManager getEntityManager() {
        return this.factory.createEntityManager();
    }
}