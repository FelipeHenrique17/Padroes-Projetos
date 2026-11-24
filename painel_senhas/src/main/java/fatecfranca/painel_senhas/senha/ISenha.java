package fatecfranca.painel_senhas.senha;

/**
 * Contrato comum a todas as senhas do sistema.
 * Não define COMO o ticket é gerado, apenas que toda senha
 * deve saber gerar o seu próprio ticket.
 */
public interface ISenha {
    String gerarTicket();
}
