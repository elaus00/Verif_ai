package mp.verif_ai.presentation

import android.content.Context
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text

class MyAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // 위젯 UI를 설정
        provideContent {
            Text(
                text = "Hello, Verif AI Widget!",
                modifier = GlanceModifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

class UpdateWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters // ActionParameters 참조
    ) {
        // 상태 업데이트 로직
        updateAppWidgetState(context, glanceId) { prefs ->
            // 필요하다면 상태 저장
        }
        MyAppWidget().update(context, glanceId)
    }
}
