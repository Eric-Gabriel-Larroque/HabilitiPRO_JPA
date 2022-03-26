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
        this.LOG.info("Preparando para criação de usuário...");
        validateNullObject(usuario,"usuário");
        validateDuplicate(usuario);

            validateCpfTemplate(usuario.getCpf());
            validateEmailTemplate(usuario.getEmail());
            validateSenhaTemplate(usuario.getSenha());

        try {
            transaction.beginTransaction();
            this.usuarioDAO.create(usuario);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.error("Falha ao criar entidade usuário: "+e.getMessage());
            throw new RuntimeException("Failed to create entity Usuario");
        }
        this.LOG.info("Criação realizada com sucesso!");
    }

    public void delete(Long id) {
        this.LOG.info("Preparando para deleção de usuário...");
        validateNullId(id);
        this.LOG.info("Verificando se existe usuário com o Id informado...");
        Usuario usuario = this.usuarioDAO.getById(id);
        validateNullObject(usuario,"usuário");

        this.LOG.info("Usuário encontrado! Iniciando deleção...");

        try {
            transaction.beginTransaction();
            this.usuarioDAO.delete(usuario);
            transaction.commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao deletar entidade usuário; "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Usuario");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Usuario newUsuario, Long usuarioId) {
        this.LOG.info("Preparando para atualização de usuário...");
        validateNullId(usuarioId);
        validateNullObject(newUsuario,"usuário");
        this.LOG.info("Verificando se existe usuário com o id informado...");
        Usuario usuario = this.usuarioDAO.getById(usuarioId);
        validateNullObject(usuario,"usuário");

        this.LOG.info("Usuário encontrado! Iniciando atualização...");

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
            this.LOG.info("Falha ao atualizar usuário: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Usuario");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }


    public List<Usuario> listAll() {
        this.LOG.info("Preparando listagem dos usuários...");
        List<Usuario> usuarios = this.usuarioDAO.listAll();
        validateNullList(Collections.singletonList(usuarios),"usuário");

        if(usuarios != null) {
            this.LOG.info(usuarios.size()+" usuário(s) encontrado(s)");
        }
        return usuarios;
    }

    public List<Usuario> listByName(String nome) {
        this.LOG.info("Preparando listagem de usuários pelo nome...");
        validateNullString(nome,"nome");
        List<Usuario> usuarios = this.usuarioDAO.listByName(nome.toLowerCase());
        validateNullList(Collections.singletonList(usuarios),"usuário");

        if(usuarios != null){
            this.LOG.info(usuarios.size()+" usuário(s) encontrado(s)");
        }
        return usuarios;
    }

    public Usuario findByCpf(String cpf) {
        this.LOG.info("Preparando para buscar usuário pelo cpf...");
        validateNullString(cpf,"cpf");
        validateCpfTemplate(cpf);

        try {
            this.LOG.info("Verificando se existe Usuário com o cpf informado...");
            Usuario usuario = this.usuarioDAO.findByCpf(cpf);
            this.LOG.info("Usuário encontrado!");
            return usuario;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrado um usuário com o cpf informado.");
            return null;
        }
    }

    public Usuario findByEmail(String email) {
        this.LOG.info("Preparando para buscar usuário pelo email...");
        validateNullString(email,"email");
        validateEmailTemplate(email);

        try {
            this.LOG.info("Verificando se existe Usuário com o email informado...");
            Usuario usuario = this.usuarioDAO.findByEmail(email.toLowerCase());
            this.LOG.info("Usuário encontrado!");
            return usuario;
        }catch (NoResultException e) {
            this.LOG.info("Não foi encontrado um usuário com o email informado.");
            return null;
        }
    }

    public Usuario getById(Long id) {
        this.LOG.info("Preparando para buscar Usuário pelo Id");
        validateNullId(id);
        this.LOG.info("Verificando se existe Usuário com o id informado...");
        Usuario usuario = this.usuarioDAO.getById(id);
        validateNullObject(usuario,"usuario");
        if(usuario != null) {
            this.LOG.info("Usuário encontrado!");
        }
        return usuario;
    }

    private void validateDuplicate(Usuario usuario)  {
        this.LOG.info("Verificando se já existe usuário com esse cpf...");
        Usuario usuario1 = this.findByCpf(usuario.getCpf());
        if(usuario1 != null) {
            this.LOG.error("O usuário informado já existe no banco de dados");
            throw new EntityExistsException("Entity Usuario already exists");
        }
        this.LOG.info("Verificando agora se existe usuário com esse email...");
        Usuario usuario2 = this.findByEmail(usuario.getEmail());
        if(usuario2 != null) {
            this.LOG.error("O usuário informado já existe no banco de dados");
            throw new EntityExistsException("Entity Usuario already exists");
        }
    }
}