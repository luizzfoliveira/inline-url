package carvalhedo.inline_url.services

import clojure.lang.PersistentArrayMap

interface LineIndexService {
    fun setLineIndexer(fileContent: String, filePath: String, urlPath: String)
    fun getLineIndexer(): PersistentArrayMap?
}