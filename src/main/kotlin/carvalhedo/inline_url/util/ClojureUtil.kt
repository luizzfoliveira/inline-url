package carvalhedo.inline_url.util

import clojure.java.api.Clojure

object ClojureUtil {
    val LINE_INDEXER_NS = "carvalhedo.inline-url.line-indexer"
    val SEARCH_URL_NS = "carvalhedo.inline-url.search-url"

    fun requireClojure(ns: String) {
        val require = Clojure.`var`("clojure.core", "require")
        require.invoke(Clojure.read(ns))
    }
}