package fatecfranca.painel_senhas.senha;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Produto concreto: senha do tipo prioritária.
 * Utiliza o prefixo "P".
 */
public class SenhaPrioritaria implements ISenha {

    private static final AtomicInteger contador = new AtomicInteger(0);

    @Override
    public String gerarTicket() {
        int numero = contador.incrementAndGet();
        return String.format("P-%03d", numero);
    }
}
