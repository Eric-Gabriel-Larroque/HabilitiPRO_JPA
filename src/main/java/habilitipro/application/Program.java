package habilitipro.application;

import habilitipro.connection.JpaConnectionFactory;

import javax.persistence.EntityManager;

public class Program {

  public static void main(String[] args) {
    EntityManager em = new JpaConnectionFactory().getEntityManager();
  }
}