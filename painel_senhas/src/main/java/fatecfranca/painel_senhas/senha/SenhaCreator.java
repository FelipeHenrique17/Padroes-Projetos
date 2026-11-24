package fatecfranca.painel_senhas.senha;

/**
 * Factory Method: define que toda classe criadora deve saber
 * criar uma ISenha, sem que quem a utiliza precise conhecer
 * a classe concreta que será instanciada.
 */
public abstract class SenhaCreator {
    public abstract ISenha criarSenha();
}
