package fatecfranca.painel_senhas.controller;

import fatecfranca.painel_senhas.model.Painel;
import fatecfranca.painel_senhas.service.PainelService;
import fatecfranca.painel_senhas.senha.SenhaCreator;
import fatecfranca.painel_senhas.senha.SenhaNormalCreator;
import fatecfranca.painel_senhas.senha.SenhaPrioritariaCreator;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;

@Controller
public class PainelController {

    private final PainelService senhaService;

    @Value("${painel.url-publica:http://localhost:8080}")
    private String urlPublica;

    public PainelController(PainelService senhaService) {
        this.senhaService = senhaService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String principal(Model model) {
        Painel senhaAtual = senhaService.getSenhaAtual();
        List<Painel> historico = senhaService.getHistoricoChamadas();

        int fim = historico.size();
        if (senhaAtual != null && fim > 0) {
            fim--; // não repetir a senha atual no histórico lateral
        }
        int inicio = Math.max(fim - 6, 0);
        List<Painel> anteriores = new ArrayList<>(historico.subList(inicio, fim));
        Collections.reverse(anteriores);

        model.addAttribute("senhaAtual", senhaAtual);
        model.addAttribute("anteriores", anteriores);
        model.addAttribute("emAtendimento", senhaService.existeAtendimentoEmAndamento());
        model.addAttribute("quantidadeNaFila", senhaService.getQuantidadeNaFila());
        model.addAttribute("senhasAguardando", senhaService.getSenhasAguardando());
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        PainelService.DashboardStats stats = senhaService.getDashboardStats();

        model.addAttribute("stats", stats);
        model.addAttribute("esperaNormalFormatada", formatarTempo(stats.esperaNormalSegundos()));
        model.addAttribute("esperaPrioritariaFormatada", formatarTempo(stats.esperaPrioritariaSegundos()));
        model.addAttribute("atendimentoMedioFormatado", formatarTempo(stats.atendimentoMedioSegundos()));
        return "dashboard";
    }

    @GetMapping("/display")
    public String display(Model model) {
        Painel senhaAtual = senhaService.getSenhaAtual();
        List<Painel> historico = senhaService.getHistoricoChamadas();

        int fim = historico.size();
        if (senhaAtual != null && fim > 0) {
            fim--;
        }
        int inicio = Math.max(fim - 6, 0);
        List<Painel> anteriores = new ArrayList<>(historico.subList(inicio, fim));
        Collections.reverse(anteriores);

        model.addAttribute("senhaAtual", senhaAtual);
        model.addAttribute("anteriores", anteriores);

        // Tempos médios de espera do dia, iguais aos apresentados no Dashboard
        PainelService.DashboardStats stats = senhaService.getDashboardStats();
        model.addAttribute("esperaNormalFormatada", formatarTempo(stats.esperaNormalSegundos()));
        model.addAttribute("esperaPrioritariaFormatada", formatarTempo(stats.esperaPrioritariaSegundos()));
        // Aceita tanto a URL base quanto uma URL que já termine em /acompanhar,
        // evitando gerar QR Codes com /acompanhar/acompanhar.
        String baseUrlQr = urlPublica.trim().replaceAll("/+$", "");
        if (baseUrlQr.toLowerCase().endsWith("/acompanhar")) {
            baseUrlQr = baseUrlQr.substring(0, baseUrlQr.length() - "/acompanhar".length());
        }
        model.addAttribute("qrCode", gerarQrCode(baseUrlQr + "/acompanhar"));

        return "display";
    }

    @GetMapping("/acompanhar")
    public String acompanhar(Model model) {
        Painel atual = senhaService.getSenhaAtual();
        List<Painel> historico = senhaService.getHistoricoChamadas();
        int fim = historico.size();
        if (atual != null && fim > 0) fim--;
        int inicio = Math.max(fim - 5, 0);
        List<Painel> anteriores = new ArrayList<>(historico.subList(inicio, fim));
        Collections.reverse(anteriores);

        PainelService.DashboardStats stats = senhaService.getDashboardStats();
        model.addAttribute("senhaAtual", atual);
        model.addAttribute("anteriores", anteriores);
        model.addAttribute("esperaNormalFormatada", formatarTempo(stats.esperaNormalSegundos()));
        model.addAttribute("esperaPrioritariaFormatada", formatarTempo(stats.esperaPrioritariaSegundos()));
        return "acompanhar";
    }

    private String gerarQrCode(String conteudo) {
        try {
            var matriz = new QRCodeWriter().encode(conteudo, BarcodeFormat.QR_CODE, 220, 220);
            ByteArrayOutputStream saida = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matriz, "PNG", saida);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(saida.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Não foi possível gerar o QR Code", e);
        }
    }

    @PostMapping("/emitir")
    public String emitirSenha(@RequestParam(defaultValue = "normal") String tipo) {
        SenhaCreator creator = "prioritaria".equalsIgnoreCase(tipo)
                ? new SenhaPrioritariaCreator()
                : new SenhaNormalCreator();

        senhaService.emitirSenha(creator);
        return "redirect:/";
    }

    @PostMapping("/chamar-proxima")
    public String chamarProximaSenha(@RequestParam(defaultValue = "1") int guiche) {
        if (guiche < 1 || guiche > 5) {
            guiche = 1;
        }
        senhaService.chamarProximaSenha(guiche);
        return "redirect:/";
    }

    @PostMapping("/finalizar")
    public String finalizarAtendimento() {
        senhaService.finalizarAtendimento();
        return "redirect:/";
    }

    private String formatarTempo(long segundos) {
        long minutos = segundos / 60;
        long restoSegundos = segundos % 60;
        return String.format("%02d:%02d", minutos, restoSegundos);
    }
}
