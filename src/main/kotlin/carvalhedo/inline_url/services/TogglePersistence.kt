package carvalhedo.inline_url.services

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
        name = "lf.inline_url.services.TogglePersistence",
        storages = [Storage("UrlInlinePlugin.xml")]
)
class TogglePersistence: PersistentStateComponent<ToggleService> {
    private var toggleService: ToggleService = ToggleService()

    override fun getState(): ToggleService {
        return toggleService
    }

    override fun loadState(state: ToggleService) {
        toggleService = state
    }
}
