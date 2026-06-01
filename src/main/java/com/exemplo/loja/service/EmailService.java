package com.exemplo.loja.service;

import com.exemplo.loja.model.ItemPedido;
import com.exemplo.loja.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Servico de notificacao por e-mail SIMULADO: em vez de enviar um e-mail real
 * (que exigiria servidor SMTP e credenciais), registra a mensagem no console/log.
 * Atende aos requisitos R5 (confirmacao de pedido) e R7 (atualizacao de status).
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /** Notifica o cliente sobre a confirmacao de um novo pedido (R5). */
    public void enviarConfirmacaoPedido(Pedido pedido) {
        StringBuilder corpo = new StringBuilder();
        corpo.append("Ola, ").append(pedido.getCliente().getNome()).append("!\n");
        corpo.append("Seu pedido #").append(pedido.getId())
                .append(" foi recebido com sucesso.\n");
        corpo.append("Itens:\n");
        for (ItemPedido item : pedido.getItens()) {
            corpo.append("  - ").append(item.getQuantidade()).append("x ")
                    .append(item.getProduto().getNome())
                    .append(" (R$ ").append(item.getSubtotal()).append(")\n");
        }
        corpo.append("Total: R$ ").append(pedido.getTotal());

        enviar(pedido.getCliente().getEmail(),
                "Pedido #" + pedido.getId() + " confirmado",
                corpo.toString());
    }

    /** Notifica o cliente sobre a atualizacao do status de um pedido (R7). */
    public void enviarAtualizacaoStatus(Pedido pedido) {
        String corpo = "Ola, " + pedido.getCliente().getNome() + "!\n"
                + "O status do seu pedido #" + pedido.getId()
                + " foi atualizado para: " + pedido.getStatus() + ".";

        enviar(pedido.getCliente().getEmail(),
                "Pedido #" + pedido.getId() + " - status: " + pedido.getStatus(),
                corpo);
    }

    private void enviar(String destinatario, String assunto, String corpo) {
        log.info("\n===================== [E-MAIL SIMULADO] =====================\n"
                + "Para:    {}\n"
                + "Assunto: {}\n"
                + "-------------------------------------------------------------\n"
                + "{}\n"
                + "=============================================================",
                destinatario, assunto, corpo);
    }
}
