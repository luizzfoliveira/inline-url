package carvalhedo.inline_url.services

class ToggleService  {
    var path: String = ""
    var isPluginOn: Boolean = false

    fun isOn(): Boolean {
        return isPluginOn
    }

    fun setUrlPath(path: String) {
        this.path = path
    }

    fun toggleUrlInlay() {
        this.isPluginOn = isPluginOn == false
    }

    fun getUrlPath(): String {
        return path
    }
}
