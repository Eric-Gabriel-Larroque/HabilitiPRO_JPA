package habilitipro.service;

import habilitipro.connection.Transaction;
import habilitipro.enums.Perfis;
import habilitipro.enums.Status;
import habilitipro.model.dao.UsuarioDAO;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import habilitipro.model.persistence.Usuario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import static habilitipro.util.Input.*;
import static habilitipro.util.Validation.*;

public class UsuarioService {

    private final Logger LOG = LogManager.getLogger(UsuarioService.class);

    private EntityManager em;

    private UsuarioDAO usuarioDAO;

    private ModuloService moduloService;

    private Transaction transaction;

    public UsuarioService(EntityManager em) {
        this.em = em;
        this.usuarioDAO = new UsuarioDAO(em);
        this.moduloService = new ModuloService(em);
        this.transaction = new Transaction(em);
    }

    public void login() {
       String email = validateString("Insira seu email\n--> ");
       validateEmailTemplate(email);
       String senha = validateString("Insira sua senha\n--> ");
       validateSenhaTemplate(senha);

       Usuario usuario = findByEmail(email);

       if(usuario.getEmail().equals(email)&&usuario.getSenha().equals(senha)) {
           this.LOG.info("Bem-vindo(a), "+usuario.getNome()+".");
       } else {
           this.LOG.error("Email ou senha incorretos");
           throw new RuntimeException("Incorrect email or password");
       }
    }

    public void setNivelSatisfacaoGeral(Usuario usuario, Trilha trilha, int nivelSatisfacao) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.OPERACIONAL)) {
            this.moduloService.setNivelSatisfacao(trilha,nivelSatisfacao);
        }
    }

    public void setAnotacoes(Usuario usuario, Trilha trilha, String anotacoes) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.OPERACIONAL)) {
            this.moduloService.setAnotacoes(trilha,anotacoes);
        }
    }

    public void setStatus(Usuario usuario, Modulo modulo, Status status) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.ADM)) {
            this.moduloService.setStatus(modulo,status);
        }
    }

    public void create(Usuario usuario) {
        this.LOG.info("Preparando para cria????o de usu??rio...");
        validateNullObject(usuario,"usu??rio");
        validateDuplicate(usuario);

            validateCpfTemplate(usuario.getCpf());
            validateEmailTemplate(usuario.getEmail());
            validateSenhaTemplate(usuario.getSenha());

        try {
            transaction.beginTransaction();
            this.usuarioDAO.create(usuario);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao criar entidade usu??rio: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Usuario");
        }
        this.LOG.info("Cria????o realizada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para dele????o de usu??rio...");
        validateNullId(id);
        this.LOG.info("Verificando se existe usu??rio com o Id informado...");
        Usuario usuario = this.usuarioDAO.getById(id);
        validateNullObject(usuario,"usu??rio");

        this.LOG.info("Usu??rio encontrado! Iniciando dele????o...");

        try {
            transaction.beginTransaction();
            this.usuarioDAO.delete(usuario);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao deletar entidade usu??rio; "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Usuario");
        }
        this.LOG.info("Dele????o realizada com sucesso!");
    }

    public void update(Usuario newUsuario, Long usuarioId) {
        this.LOG.info("Preparando para atualiza????o de usu??rio...");
        validateNullId(usuarioId);
        validateNullObject(newUsuario,"usu??rio");
        this.LOG.info("Verificando se existe usu??rio com o id informado...");
        Usuario usuario = this.usuarioDAO.getById(usuarioId);
        validateNullObject(usuario,"usu??rio");

        this.LOG.info("Usu??rio encontrado! Iniciando atualiza????o...");

        if(!usuario.getCpf().equals(newUsuario.getCpf())) {
            validateDuplicate(newUsuario);
        }

        try {
            transaction.beginTransaction();

            usuario.setPerfisDeAcesso(newUsuario.getPerfisDeAcesso());
            usuario.setNome(newUsuario.getNome());
            validateCpfTemplate(newUsuario.getCpf());
            usuario.setCpf(newUsuario.getCpf());
            validateEmailTemplate(newUsuario.getEmail());
            usuario.setEmail(newUsuario.getEmail());
            validateSenhaTemplate(newUsuario.getSenha());
            usuario.setSenha(newUsuario.getSenha());

            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao atualizar usu??rio: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Usuario");
        }
        this.LOG.info("Atualiza????o realizada com sucesso!");
    }


    public List<Usuario> listAll() {
        this.LOG.info("Preparando listagem dos usu??rios...");
        List<Usuario> usuarios = this.usuarioDAO.listAll();
        validateNullList(Collections.singletonList(usuarios),"usu??rio");

        if(usuarios != null) {
            this.LOG.info(usuarios.size()+" usu??rio(s) encontrado(s)");
        }
        return usuarios;
    }

    public List<Usuario> listByName(String nome) {
        this.LOG.info("Preparando listagem de usu??rios pelo nome...");
        validateNullString(nome,"nome");
        List<Usuario> usuarios = this.usuarioDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(usuarios),"usu??rio");

        if(usuarios != null){
            this.LOG.info(usuarios.size()+" usu??rio(s) encontrado(s)");
        }
        return usuarios;
    }

    public Usuario findByCpf(String cpf) {
        this.LOG.info("Preparando para buscar usu??rio pelo cpf...");
        validateNullString(cpf,"cpf");
        validateCpfTemplate(cpf);

        try {
            this.LOG.info("Verificando se existe Usu??rio com o cpf informado...");
            Usuario usuario = this.usuarioDAO.findByCpf(cpf);
            this.LOG.info("Usu??rio encontrado!");
            return usuario;
        }catch (NoResultException e) {
            this.LOG.info("N??o foi encontrado um usu??rio com o cpf informado.");
            return null;
        }
    }

    public Usuario findByEmail(String email) {
        this.LOG.info("Preparando para buscar usu??rio pelo email...");
        validateNullString(email,"email");
        validateEmailTemplate(email);

        try {
            this.LOG.info("Verificando se existe Usu??rio com o email informado...");
            Usuario usuario = this.usuarioDAO.findByEmail(email.toLowerCase());
            this.LOG.info("Usu??rio encontrado!");
            return usuario;
        }catch (NoResultException e) {
            this.LOG.info("N??o foi encontrado um usu??rio com o email informado.");
            return null;
        }
    }

    public Usuario getById(Long id) {
        this.LOG.info("Preparando para buscar Usu??rio pelo Id");
        validateNullId(id);
        this.LOG.info("Verificando se existe Usu??rio com o id informado...");
        Usuario usuario = this.usuarioDAO.getById(id);
        validateNullObject(usuario,"usuario");
        if(usuario != null) {
            this.LOG.info("Usu??rio encontrado!");
        }
        return usuario;
    }

    private void validateDuplicate(Usuario usuario)  {
        this.LOG.info("Verificando se j?? existe usu??rio com esse cpf...");
        Usuario usuario1 = this.findByCpf(usuario.getCpf());
        if(usuario1 != null) {
            this.LOG.error("O usu??rio informado j?? existe no banco de dados");
            throw new EntityExistsException("Entity Usuario already exists");
        }
        this.LOG.info("Verificando agora se existe usu??rio com esse email...");
        Usuario usuario2 = this.findByEmail(usuario.getEmail());
        if(usuario2 != null) {
            this.LOG.error("O usu??rio informado j?? existe no banco de dados");
            throw new EntityExistsException("Entity Usuario already exists");
        }
    }
}