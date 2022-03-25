package habilitipro.service;

import habilitipro.enums.Perfis;
import habilitipro.enums.Status;
import habilitipro.model.dao.UsuarioDAO;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import habilitipro.model.persistence.Usuario;
import habilitipro.util.Input;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import static habilitipro.util.Input.*;

public class UsuarioService {

    private final Logger LOG = LogManager.getLogger(UsuarioService.class);

    private EntityManager em;

    private UsuarioDAO usuarioDAO;

    private TrilhaService trilhaService;

    private ModuloService moduloService;

    private final String EMAIL_TEMPLATE = "[a-zA-Z0-9]+@[a-zA-Z]+.+[a-zA-Z]";

    private final String CPF_TEMPLATE = "\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";

    private final String SENHA_TEMPLATE = "[a-zA-Z0-9]+";



    public UsuarioService(EntityManager em) {
        this.em = em;
        this.usuarioDAO = new UsuarioDAO(em);
        this.trilhaService = new TrilhaService(em);
        this.moduloService = new ModuloService(em);
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
        validateNullUsuario(usuario);
        validateDuplicate(usuario);

            validateCpfTemplate(usuario.getCpf());
            validateEmailTemplate(usuario.getEmail());
            validateSenhaTemplate(usuario.getSenha());

        try {
            beginTransaction();
            this.usuarioDAO.create(usuario);
            commitAndClose();
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
        validateNullUsuario(usuario);

        this.LOG.info("Usuário encontrado! Iniciando deleção...");

        try {
            beginTransaction();
            this.usuarioDAO.delete(usuario);
            commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao deletar entidade usuário; "+e.getMessage());
            throw new RuntimeException("Failed to delete entity Usuario");
        }
        this.LOG.info("Deleção realizada com sucesso!");
    }

    public void update(Usuario newUsuario, Long usuarioId) {
        this.LOG.info("Preparando para atualização de usuário...");
        validateNullId(usuarioId);
        validateNullUsuario(newUsuario);
        this.LOG.info("Verificando se existe usuário com o id informado...");
        Usuario usuario = this.usuarioDAO.getById(usuarioId);
        validateNullUsuario(usuario);

        this.LOG.info("Usuário encontrado! Iniciando atualização...");

        if(!usuario.getCpf().equals(newUsuario.getCpf())) {
            validateDuplicate(newUsuario);
        }

        try {
            beginTransaction();

            usuario.setPerfisDeAcesso(newUsuario.getPerfisDeAcesso());
            usuario.setNome(newUsuario.getNome());
            validateCpfTemplate(newUsuario.getCpf());
            usuario.setCpf(newUsuario.getCpf());
            validateEmailTemplate(newUsuario.getEmail());
            usuario.setEmail(newUsuario.getEmail());
            validateSenhaTemplate(newUsuario.getSenha());
            usuario.setSenha(newUsuario.getSenha());

            commitAndClose();
        }catch (Exception e) {
            this.LOG.info("Falha ao atualizar usuário: "+e.getMessage());
            throw new RuntimeException("Failed to update entity Usuario");
        }
        this.LOG.info("Atualização realizada com sucesso!");
    }


    public List<Usuario> listAll() {
        this.LOG.info("Preparando listagem dos usuários...");
        List<Usuario> usuarios = this.usuarioDAO.listAll();
        validateNullList(usuarios);

        if(usuarios != null) {
            this.LOG.info(usuarios.size()+" usuário(s) encontrado(s)");
        }
        return usuarios;
    }

    public List<Usuario> listByName(String nome) {
        this.LOG.info("Preparando listagem de usuários pelo nome...");
        validateNullName(nome);
        List<Usuario> usuarios = this.usuarioDAO.listByName(nome.toLowerCase());
        validateNullList(usuarios);

        if(usuarios != null){
            this.LOG.info(usuarios.size()+" usuário(s) encontrado(s)");
        }
        return usuarios;
    }

    private void validateCpfTemplate(String cpf) {
        this.LOG.info("Verificando se o cpf está correto...");
        if(!cpf.matches(this.CPF_TEMPLATE)){
            this.LOG.error("O formato do cpf está incorreto");
            throw new RuntimeException("Wrong cpf format");
        }
    }

    private void validateSenhaTemplate(String senha) {
        this.LOG.info("Verificando se a senha se encontra dentro da regra de negócio...");
        if(!senha.matches(this.SENHA_TEMPLATE)){
            this.LOG.error("A senha deve conter somente caracteres alfanuméricos (números e letras)");
            throw new RuntimeException("Password must contain only alphanumeric characters");
        }else if(senha.length()<8) {
            this.LOG.error("A senha deve conter pelo menos 8 caracteres");
            throw new RuntimeException("Password must contain at least 8 characters");
        }
    }

    private void validateEmailTemplate(String email) {
        this.LOG.info("Verificando se o email é valido...");
        if(!email.matches(this.EMAIL_TEMPLATE)) {
            this.LOG.error("Email deve conter o seguinte formato: usuário@domínio.terminação");
            throw new RuntimeException("Email must contain the following format: user@domain.termination");
        }
    }


    public Usuario findByCpf(String cpf) {
        this.LOG.info("Preparando para buscar usuário pelo cpf...");
        validateNullCpf(cpf);

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
        validateNullEmail(email);

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
        validateNullUsuario(usuario);
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

    public List<Usuario> validateNullList(List<Usuario> usuarios) {
        this.LOG.info("Verificando se existe registros de usuário");
        if(usuarios == null) {
            this.LOG.info("Não foram encontrados usuários.");
            return new ArrayList<>();
        }
        return usuarios;
    }

    private void validateNullId(Long id) {
        this.LOG.info("Verificando se o id informado é nulo...");
        if(id == null) {
            this.LOG.error("O id informado é nulo");
            throw new RuntimeException("Id is null");
        }
    }

    private void validateNullName(String nome) {
        this.LOG.info("Verificando se o nome informado é nulo...");
        if(nome == null || nome.isEmpty() || nome.isBlank()) {
            this.LOG.error("O nome informado é vazio ou nulo");
            throw new RuntimeException("nome is empty or null");
        }
    }

    private void validateNullCpf(String cpf) {
        this.LOG.info("Verificando se o cpf informado é nulo...");
        if(cpf == null || cpf.isEmpty() || cpf.isBlank()) {
            this.LOG.error("O cpf informado é vazio ou nulo");
            throw new RuntimeException("cpf is empty or null");
        }
    }

    private void validateNullEmail(String email) {
        this.LOG.info("Verificando se o email informado é nulo...");
        if(email == null || email.isEmpty() || email.isBlank()) {
            this.LOG.error("O email informado é vazio ou nulo");
            throw new RuntimeException("email is empty or null");
        }
    }

    private void validateNullSenha(String senha) {
        this.LOG.info("Verificando se a senha informada é nula...");
        if(senha == null || senha.isEmpty() || senha.isBlank()) {
            this.LOG.error("A senha informada é vazia ou nula");
            throw new RuntimeException("senha is empty or null");
        }
    }

    private void validateNullUsuario(Usuario usuario) {
        this.LOG.info("Verificando se o usuário é nulo...");
        if(usuario == null) {
            this.LOG.error("Entidade usuário não encontrado");
            throw new EntityNotFoundException("Entity Usuario not found");
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
