package fatecfranca.painel_senhas.senha;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produto concreto: senha do tipo normal.
 * Utiliza o prefixo "N".
 */
public class SenhaNormal implements ISenha {

    private static final AtomicInteger contador = new AtomicInteger(0);

    @Override
    public String gerarTicket() {
        int numero = contador.incrementAndGet();
        return String.format("N-%03d", numero);
    }
}
