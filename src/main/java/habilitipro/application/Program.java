package habilitipro.application;

import habilitipro.connection.JpaConnectionFactory;
import habilitipro.enums.Perfis;
import habilitipro.model.persistence.Usuario;
import habilitipro.service.UsuarioService;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Program {

  private static Set<Perfis> perfisSet = new HashSet<>();


  //Nome do Banco de Dados --> habilitipro
  public static void main(String[] args) {

    EntityManager em = new JpaConnectionFactory().getEntityManager();

    Collections.addAll(perfisSet,Perfis.ADM,Perfis.RH,Perfis.OPERACIONAL);

            UsuarioService usuarioService = new UsuarioService(em);


    Usuario usuario = new Usuario("Xx_IntelliJ_Eh_Superior_xX","12345678910","teste@teste.com",
            "senha123",perfisSet);

    //CRIANDO USUÁRIO PARA TESTE DE LOGIN
    usuarioService.create(usuario);


    // SENHA --> senha123
    // EMAIL --> teste@teste.com
    usuarioService.login();

  }
}