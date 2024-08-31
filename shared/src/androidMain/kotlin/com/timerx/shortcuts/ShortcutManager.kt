package com.timerx.shortcuts

import android.content.Context
import android.content.Intent
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.timerx.KEY_RUN_TIMER_ID
import com.timerx.MainActivity
import com.timerx.R
import com.timerx.database.ITimerRepository
import com.timerx.database.RoomTimer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShortcutManager(
    private val context: Context,
    private val timerRepository: ITimerRepository
) {

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private val dynamicShortcutLimit = 3

    init {
        coroutineScope.launch {
            timerRepository.getShallowTimers().collect { roomTimers ->
                handlePinnedShortcuts(roomTimers)
                refreshDynamicShortcuts(roomTimers)
            }
        }
    }

    private fun refreshDynamicShortcuts(roomTimers: List<RoomTimer>) {
        ShortcutManagerCompat.removeAllDynamicShortcuts(context)

        roomTimers.take(dynamicShortcutLimit).forEach { roomTimer ->
            val shortcut = ShortcutInfoCompat.Builder(context, roomTimer.shortcutId())
                .setShortLabel(roomTimer.name)
                .setLongLabel(roomTimer.name)
                .setIcon(IconCompat.createWithResource(context, R.drawable.play_arrow))
                .setIntent(
                    Intent(
                        context,
                        MainActivity::class.java
                    ).apply {
                        action = Intent.ACTION_VIEW
                        putExtra(KEY_RUN_TIMER_ID, roomTimer.id)
                    }
                )
                .build()

            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

    private fun handlePinnedShortcuts(roomTimers: List<RoomTimer>) {
        val pinnedShortcuts = ShortcutManagerCompat.getShortcuts(
            context,
            ShortcutManagerCompat.FLAG_MATCH_PINNED
        )
        val missingPinnedShortcuts = pinnedShortcuts.filter { shortcut ->
            roomTimers.none { roomTimer ->
                shortcut.id == roomTimer.shortcutId()
            }
        }.map { it.id }
        ShortcutManagerCompat.disableShortcuts(
            context,
            missingPinnedShortcuts,
            context.getString(R.string.timer_no_longer_exists)
        )
    }

    private fun RoomTimer.shortcutId(): String {
        return "${this.id}"
    }
}
