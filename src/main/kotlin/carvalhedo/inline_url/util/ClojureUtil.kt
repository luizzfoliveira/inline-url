package carvalhedo.inline_url.util

import clojure.java.api.Clojure

object ClojureUtil {
    val LINE_INDEXER_NS = "carvalhedo.inline-url.line-indexer"
    val READ_EDN_NS = "carvalhedo.inline-url.read-edn"

    fun requireClojure(ns: String) {
        val require = Clojure.`var`("clojure.core", "require")
        require.invoke(Clojure.read(ns))
    }
}