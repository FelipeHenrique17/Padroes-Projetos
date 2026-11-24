package fatecfranca.painel_senhas.senha;

public final class SenhaPrioritariaCreator extends SenhaCreator {
    @Override
    public ISenha criarSenha() {
        return new SenhaPrioritaria();
    }
}
