package fatecfranca.painel_senhas.service;

import fatecfranca.painel_senhas.model.Painel;
import fatecfranca.painel_senhas.senha.ISenha;
import fatecfranca.painel_senhas.senha.SenhaCreator;
import fatecfranca.painel_senhas.senha.SenhaPrioritaria;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PainelService {

    private Painel senhaAtual;
    private final List<Painel> historico = new ArrayList<>();
    private final List<Painel> fila = new ArrayList<>();
    // Quantas prioritárias foram chamadas desde a última normal.
    private int prioritariasDesdeUltimaNormal = 0;

    /**
     * Emite uma senha e registra o horário de emissão.
     * A senha ainda não é considerada chamada nem atendida.
     */
    public synchronized Painel emitirSenha(SenhaCreator creator) {
        ISenha senha = creator.criarSenha();
        String ticket = senha.gerarTicket();
        String tipo = (senha instanceof SenhaPrioritaria) ? "Prioritária" : "Normal";

        Painel novaSenha = new Painel(ticket, tipo, 0);
        historico.add(novaSenha);
        fila.add(novaSenha);
        return novaSenha;
    }

    /**
     * Chama a próxima senha aguardando. Prioritárias têm preferência.
     * O sistema atual possui uma única chamada ativa no painel principal.
     */
    public synchronized Painel chamarProximaSenha(int guiche) {
        if (senhaAtual != null && senhaAtual.isEmAtendimento()) {
            return null;
        }

        // Mantém a ordem de chegada dentro de cada tipo de senha.
        Painel proximaPrioritaria = fila.stream()
                .filter(Painel::isPrioritaria)
                .min(Comparator.comparing(Painel::getEmitidaEm))
                .orElse(null);

        Painel proximaNormal = fila.stream()
                .filter(s -> !s.isPrioritaria())
                .min(Comparator.comparing(Painel::getEmitidaEm))
                .orElse(null);

        Painel proxima;

        if (proximaPrioritaria != null && proximaNormal != null) {
            if (prioritariasDesdeUltimaNormal < 2) {
                proxima = proximaPrioritaria;
                prioritariasDesdeUltimaNormal++;
            } else {
                proxima = proximaNormal;
                prioritariasDesdeUltimaNormal = 0;
            }
        } else if (proximaPrioritaria != null) {
            proxima = proximaPrioritaria;
            if (prioritariasDesdeUltimaNormal < 2) {
                prioritariasDesdeUltimaNormal++;
            }
        } else if (proximaNormal != null) {
            proxima = proximaNormal;
            prioritariasDesdeUltimaNormal = 0;
        } else {
            return null;
        }

        proxima.chamar(guiche);
        senhaAtual = proxima;
        fila.remove(proxima);
        return proxima;
    }

    public synchronized boolean finalizarAtendimento() {
        if (senhaAtual == null || !senhaAtual.isEmAtendimento()) {
            return false;
        }
        senhaAtual.finalizar();
        return true;
    }

    public synchronized Painel getSenhaAtual() {
        return senhaAtual;
    }

    public synchronized boolean existeAtendimentoEmAndamento() {
        return senhaAtual != null && senhaAtual.isEmAtendimento();
    }

    public synchronized int getQuantidadeNaFila() {
        return fila.size();
    }

    public synchronized List<Painel> getSenhasAguardando() {
        return fila.stream()
                .sorted(Comparator.comparing(Painel::isPrioritaria).reversed()
                        .thenComparing(Painel::getEmitidaEm))
                .toList();
    }

    public synchronized List<Painel> getHistorico() {
        return new ArrayList<>(historico);
    }

    public synchronized List<Painel> getHistoricoChamadas() {
        return historico.stream()
                .filter(Painel::isChamada)
                .toList();
    }

    public synchronized DashboardStats getDashboardStats() {
        LocalDate hoje = LocalDate.now();

        List<Painel> chamadasHoje = historico.stream()
                .filter(Painel::isChamada)
                .filter(p -> p.getChamadaEm().toLocalDate().equals(hoje))
                .toList();

        List<Painel> finalizadasHoje = historico.stream()
                .filter(Painel::isFinalizada)
                .filter(p -> p.getFinalizadaEm().toLocalDate().equals(hoje))
                .toList();

        long esperaNormal = mediaSegundos(chamadasHoje.stream()
                .filter(p -> !p.isPrioritaria())
                .filter(p -> p.getEmitidaEm() != null && p.getChamadaEm() != null)
                .map(p -> Duration.between(p.getEmitidaEm(), p.getChamadaEm()).getSeconds())
                .toList());

        long esperaPrioritaria = mediaSegundos(chamadasHoje.stream()
                .filter(Painel::isPrioritaria)
                .filter(p -> p.getEmitidaEm() != null && p.getChamadaEm() != null)
                .map(p -> Duration.between(p.getEmitidaEm(), p.getChamadaEm()).getSeconds())
                .toList());

        long tempoAtendimento = mediaSegundos(finalizadasHoje.stream()
                .filter(p -> p.getChamadaEm() != null && p.getFinalizadaEm() != null)
                .map(p -> Duration.between(p.getChamadaEm(), p.getFinalizadaEm()).getSeconds())
                .toList());

        return new DashboardStats(
                finalizadasHoje.size(),
                esperaNormal,
                chamadasHoje.stream().filter(p -> !p.isPrioritaria())
                        .filter(p -> p.getEmitidaEm() != null && p.getChamadaEm() != null).count(),
                esperaPrioritaria,
                chamadasHoje.stream().filter(Painel::isPrioritaria)
                        .filter(p -> p.getEmitidaEm() != null && p.getChamadaEm() != null).count(),
                tempoAtendimento,
                finalizadasHoje.size()
        );
    }

    private long mediaSegundos(List<Long> valores) {
        if (valores.isEmpty()) {
            return 0;
        }
        return Math.round(valores.stream().mapToLong(Long::longValue).average().orElse(0));
    }

    public record DashboardStats(
            long atendimentosHoje,
            long esperaNormalSegundos,
            long quantidadeEsperaNormal,
            long esperaPrioritariaSegundos,
            long quantidadeEsperaPrioritaria,
            long atendimentoMedioSegundos,
            long quantidadeAtendimentosComTempo
    ) {}
}
