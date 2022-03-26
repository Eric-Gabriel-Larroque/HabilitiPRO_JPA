package habilitipro.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Validation {

    private static final Logger LOG = LogManager.getLogger(Validation.class);

    private static final String CNPJ_TEMPLATE = "\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";

    private static final String CPF_TEMPLATE = "\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d\\d";

    private static final String EMAIL_TEMPLATE = "[a-zA-Z0-9]+@[a-zA-Z]+.+[a-zA-Z]";

    private static final String SENHA_TEMPLATE = "[a-zA-Z0-9]+";

    public static void validateNullObject(Object object, String objectName) {
        LOG.info("Verificando se o(a) "+objectName+" é nulo(a)");
        if(object == null) {
            LOG.error("O(A) "+objectName+" é nulo(a)");
            throw new EntityNotFoundException("Entity "+objectName+" is null");
        }
    }

    public static void validateNullId(Long id) {
        LOG.info("Verificando se o id informado é nulo...");
        if(id == null) {
            LOG.error("O id informado é nulo");
            throw new RuntimeException("Id is null");
        }
    }

    public static void validateNullNota(Integer nota) {
        LOG.info("Verificando se a nota informada é nula...");
        if(nota == null) {
            LOG.error("A nota informada é nula");
            throw new RuntimeException("score attribute is null");
        }
    }

    public static List<Object> validateNullList(List<Object> listaObjetos, String nomeObjeto) {
        LOG.info("Verificando se existe registros de "+nomeObjeto);
        if(listaObjetos == null) {
            LOG.info("Não foram encontrados(as) nenhum(a) "+nomeObjeto);
            return new ArrayList<>();
        }
        return listaObjetos;
    }

    public static void validateNullString(String atributo, String nomeAtributo) {
        LOG.info("Verificando se o(a)"+nomeAtributo+" informado(a) é nulo(a)...");
        if(atributo == null || atributo.isEmpty() || atributo.isBlank()) {
            LOG.error("O(a)"+nomeAtributo+" informado(a) é vazio(a) ou nulo(a)");
            throw new RuntimeException(nomeAtributo+" is empty or null");
        }
    }


    public static void validateCnpjTemplate(String cnpj) {
        LOG.info("Verificando se o cnpj está correto...");
        if(!cnpj.matches(CNPJ_TEMPLATE)){
            LOG.error("O formato do cnpj está incorreto");
            throw new RuntimeException("Wrong cnpj format");
        }
    }

    public static void validateCpfTemplate(String cpf) {
        LOG.info("Verificando se o cpf está correto...");
        if(!cpf.matches(CPF_TEMPLATE)){
            LOG.error("O formato do cpf está incorreto");
            throw new RuntimeException("Wrong cpf format");
        }
    }

    public static void validateSenhaTemplate(String senha) {
        LOG.info("Verificando se a senha se encontra dentro da regra de negócio...");
        if(!senha.matches(SENHA_TEMPLATE)){
            LOG.error("A senha deve conter somente caracteres alfanuméricos (números e letras)");
            throw new RuntimeException("Password must contain only alphanumeric characters");
        }else if(senha.length()<8) {
            LOG.error("A senha deve conter pelo menos 8 caracteres");
            throw new RuntimeException("Password must contain at least 8 characters");
        }
    }

    public static void validateEmailTemplate(String email) {
        LOG.info("Verificando se o email é valido...");
        if(!email.matches(EMAIL_TEMPLATE)) {
            LOG.error("Email deve conter o seguinte formato: usuário@domínio.terminação");
            throw new RuntimeException("Email must contain the following format: user@domain.termination");
        }
    }

}
