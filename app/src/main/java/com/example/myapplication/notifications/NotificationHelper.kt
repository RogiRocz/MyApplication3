package com.example.myapplication.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.RequiresPermission // Import necessário para a anotação

/**
 * Objeto helper para agendar notificações de lembrete.
 * Utiliza o [AlarmManager] para disparar um [BroadcastReceiver] ([ReminderReceiver])
 * em um momento específico.
 */
object NotificationHelper {

    /**
     * Agenda um lembrete para um item específico.
     * Esta função requer a permissão `SCHEDULE_EXACT_ALARM`.
     *
     * @param context O contexto do aplicativo.
     * @param itemId O ID do item para o qual o lembrete está sendo agendado. Usado para identificar o PendingIntent.
     * @param title O título do lembrete, que será exibido na notificação.
     * @param timeInMillis O tempo em milissegundos (desde a epoch) em que o lembrete deve ser disparado.
     */
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun scheduleReminder(
        context: Context,
        itemId: Int,
        title: String,
        timeInMillis: Long
    ) {
        // Cria um Intent para o ReminderReceiver, passando o ID do item e o título como extras.
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ReminderReceiver.EXTRA_ITEM_ID, itemId) // Usa a constante definida em ReminderReceiver
            putExtra(ReminderReceiver.EXTRA_TITLE, title)    // Usa a constante definida em ReminderReceiver
        }

        // Cria um PendingIntent que será disparado quando o alarme tocar.
        // O itemId é usado como requestCode para garantir que cada lembrete tenha um PendingIntent único.
        // FLAG_UPDATE_CURRENT: Se já existir um PendingIntent com o mesmo Intent (mas possivelmente com extras diferentes),
        //                      ele será atualizado com os novos extras.
        // FLAG_IMMUTABLE: Indica que o Intent dentro do PendingIntent não será modificado.
        //                 Necessário para apps direcionados ao Android S (API 31) e superior.
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            itemId, // requestCode - deve ser único para cada alarme que você deseja gerenciar separadamente
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Obtém o AlarmManager do sistema.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Agenda o alarme.
        // setExactAndAllowWhileIdle: Agenda um alarme para ser disparado no tempo exato especificado,
        //                            mesmo que o dispositivo esteja em modo Doze (economia de energia).
        // AlarmManager.RTC_WAKEUP: Usa o tempo real (wall clock time) e acorda o dispositivo se ele estiver dormindo.
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )
    }
}