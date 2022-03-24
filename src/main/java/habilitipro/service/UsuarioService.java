package habilitipro.service;

import habilitipro.enums.Perfis;
import habilitipro.enums.Status;
import habilitipro.model.dao.UsuarioDAO;
import habilitipro.model.persistence.Modulo;
import habilitipro.model.persistence.Trilha;
import habilitipro.model.persistence.Usuario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioService {

    private final Logger LOG = LogManager.getLogger(UsuarioService.class);

    private EntityManager em;

    private UsuarioDAO usuarioDAO;

    private TrilhaService trilhaService;

    private ModuloService moduloService;

    public UsuarioService(EntityManager em) {
        this.em = em;
        this.usuarioDAO = new UsuarioDAO(em);
        this.trilhaService = new TrilhaService(em);
        this.moduloService = new ModuloService(em);
    }

    private void setNivelSatisfacaoGeral(Usuario usuario, Trilha trilha, int nivelSatisfacao) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.OPERACIONAL)) {
            this.moduloService.setNivelSatisfacao(trilha,nivelSatisfacao);
        }
    }

    private void setAnotacoes(Usuario usuario, Trilha trilha, String anotacoes) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.OPERACIONAL)) {
            this.moduloService.setAnotacoes(trilha,anotacoes);
        }
    }

    private void setStatus(Usuario usuario, Modulo modulo, Status status) {
        if(usuario.getPerfisDeAcesso().contains(Perfis.ADM)) {
            this.moduloService.setStatus(modulo,status);
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

    private void validateDuplicate(Usuario usuario)  {
        this.LOG.info("Verificando se já existe usuário com esse cpf...");
        Usuario usuario1 = this.usuarioDAO.findByCpf(usuario.getCpf());
        if(usuario1 != null) {
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
