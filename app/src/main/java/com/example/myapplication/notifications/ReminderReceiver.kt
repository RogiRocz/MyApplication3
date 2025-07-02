package com.example.myapplication.notifications

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R

/**
 * BroadcastReceiver responsável por receber os intents de alarme agendados
 * e exibir uma notificação de lembrete para o usuário.
 */
class ReminderReceiver : BroadcastReceiver() {

    /**
     * Companion object para definir constantes relacionadas ao ReminderReceiver.
     */
    companion object {
        /** ID do canal de notificação para lembretes. Deve ser o mesmo usado na criação do canal em `MainActivity`. */
        const val CHANNEL_ID     = "reminder_channel"
        /** Chave para o extra do Intent que armazena o ID do item do lembrete. */
        const val EXTRA_ITEM_ID  = "itemId"
        /** Chave para o extra do Intent que armazena o título do lembrete. */
        const val EXTRA_TITLE    = "title"
    }

    /**
     * Chamado quando o BroadcastReceiver está recebendo um Intent broadcast.
     * Neste caso, é quando um alarme agendado é disparado.
     *
     * A anotação `@SuppressLint("MissingPermission")` é usada aqui porque a permissão
     * `POST_NOTIFICATIONS` (para Android 13+) ou o ato de criar um canal de notificação
     * (para versões anteriores) já deve ter sido tratado antes de agendar a notificação.
     * O `NotificationManagerCompat` lida com a compatibilidade de permissões.
     *
     * @param context O Contexto no qual o receiver está rodando.
     * @param intent O Intent sendo recebido. Contém os extras com os detalhes do lembrete.
     */
    @SuppressLint("MissingPermission") // A permissão de notificação é verificada em tempo de execução ou o canal já foi criado.
    override fun onReceive(context: Context, intent: Intent) {
        // Extrai o ID do item e o título do lembrete do Intent.
        // Fornece valores padrão caso os extras não sejam encontrados.
        val itemId = intent.getIntExtra(EXTRA_ITEM_ID, 0) // Padrão 0 se não encontrado
        val title  = intent.getStringExtra(EXTRA_TITLE) ?: "Lembrete" // Padrão "Lembrete" se não encontrado

        // Constrói a notificação usando NotificationCompat.Builder para compatibilidade com versões anteriores do Android.
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder) // Ícone pequeno da notificação.
            .setContentTitle("Lembrete: $title") // Título da notificação.
            .setContentText("Hora de verificar o item #$itemId") // Texto principal da notificação.
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridade da notificação (afeta como ela é apresentada).
            .setAutoCancel(true) // Remove a notificação quando o usuário a toca.
            // .setContentIntent(pendingIntent) // Opcional: Ação a ser executada quando o usuário toca na notificação.

        // Exibe a notificação usando NotificationManagerCompat.
        // O itemId é usado como ID da notificação para que, se uma nova notificação com o mesmo ID for postada,
        // ela atualize a existente em vez de criar uma nova.
        NotificationManagerCompat.from(context)
            .notify(itemId, builder.build())
    }
}