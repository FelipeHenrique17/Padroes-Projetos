package fatecfranca.painel_senhas.model;

import java.time.LocalDateTime;

public class Painel {

    private final String codigo;
    private final String tipo;
    private final LocalDateTime emitidaEm;
    private int guiche;
    private LocalDateTime chamadaEm;
    private LocalDateTime finalizadaEm;

    public Painel(String codigo, String tipo, int guiche) {
        this(codigo, tipo, guiche, LocalDateTime.now());
    }

    public Painel(String codigo, String tipo, int guiche, LocalDateTime emitidaEm) {
        this.codigo = codigo;
        this.tipo = tipo;
        this.guiche = guiche;
        this.emitidaEm = emitidaEm;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getTipo() {
        return tipo;
    }

    public int getGuiche() {
        return guiche;
    }

    public boolean isPrioritaria() {
        return "Prioritária".equals(tipo);
    }

    public LocalDateTime getEmitidaEm() {
        return emitidaEm;
    }

    public LocalDateTime getChamadaEm() {
        return chamadaEm;
    }

    public LocalDateTime getFinalizadaEm() {
        return finalizadaEm;
    }

    public boolean isChamada() {
        return chamadaEm != null;
    }

    public boolean isFinalizada() {
        return finalizadaEm != null;
    }

    public boolean isEmAtendimento() {
        return chamadaEm != null && finalizadaEm == null;
    }

    public void chamar(int guiche) {
        if (chamadaEm == null) {
            this.guiche = guiche;
            this.chamadaEm = LocalDateTime.now();
        }
    }

    public void finalizar() {
        if (isEmAtendimento()) {
            this.finalizadaEm = LocalDateTime.now();
        }
    }
}
