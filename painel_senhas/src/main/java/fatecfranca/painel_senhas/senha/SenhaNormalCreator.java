package fatecfranca.painel_senhas.senha;

public final class SenhaNormalCreator extends SenhaCreator {
    @Override
    public ISenha criarSenha() {
        return new SenhaNormal();
    }
}
