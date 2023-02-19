package carvalhedo.inline_url.services

import clojure.java.api.Clojure
import clojure.lang.PersistentArrayMap
import com.intellij.openapi.components.Service
import carvalhedo.inline_url.actions.SearchUrlAction
import carvalhedo.inline_url.util.ClojureUtil.LINE_INDEXER_NS
import carvalhedo.inline_url.util.ClojureUtil.requireClojure

@Service(Service.Level.PROJECT)
class LineIndexImpl: LineIndexService {
    private var lineIndexer: PersistentArrayMap? = null
    override fun setLineIndexer(fileContent: String, filePath: String, urlPath: String) {
        val oldLoader = Thread.currentThread().contextClassLoader
        try {
            val loader = SearchUrlAction::class.java.classLoader
            Thread.currentThread().contextClassLoader = loader

            requireClojure(LINE_INDEXER_NS)

            lineIndexer = if (lineIndexer === null) {
                Clojure.`var`(LINE_INDEXER_NS, "gen-line-indexer").invoke(fileContent, filePath, urlPath) as PersistentArrayMap
            } else {
                Clojure.`var`(LINE_INDEXER_NS, "gen-line-indexer").invoke(lineIndexer, fileContent, filePath, urlPath) as PersistentArrayMap
            }
        } finally {
            Thread.currentThread().contextClassLoader = oldLoader
        }
    }

    override fun getLineIndexer(): PersistentArrayMap? {
        return lineIndexer
    }
}