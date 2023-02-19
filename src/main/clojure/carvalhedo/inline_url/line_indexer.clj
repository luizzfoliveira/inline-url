(ns carvalhedo.inline-url.line-indexer
  (:require [carvalhedo.inline-url.util :as util]
            [clojure.edn :as edn]
            [clojure.pprint]))

(defn get-line-number [url lines min-value]
  (-> (keep-indexed (fn [idx line]
                      (when (and (clojure.string/includes? line (str "[\"" url "\""))
                                 (<= min-value idx))
                        idx)) lines)
      first))

(defn gen-url-map-from-vec
  ([url-vec lines]
   (gen-url-map-from-vec url-vec lines []))
  ([url-vec lines depends-on]
   (let [current-url (-> url-vec
                         flatten
                         first)
         line-number (get-line-number current-url @lines (or (last depends-on)
                                                            0))
         children (util/get-children url-vec)]
     (when line-number
       (swap! lines assoc line-number 0)
       (if children
         (into {line-number {:val current-url
                             :url (conj depends-on line-number)}}
               (mapv #(gen-url-map-from-vec % lines (conj depends-on line-number)) children))
         {line-number {:val current-url
                       :url (conj depends-on line-number)}})))))

(defn build-line-indexer [urls lines]
  (reduce (fn [acc url-vec]
            (merge acc (gen-url-map-from-vec url-vec lines)))
          {}
          urls))

(defn gen-line-indexer
  ([edn-content file-path ^String edn-url-path]
   (gen-line-indexer {} edn-content file-path edn-url-path))
  ([previous-line-indexer edn-content file-path ^String edn-url-path]
   (let [path-as-vec (read-string edn-url-path)
         sanitized-edn-content (util/sanitize-edn edn-content)
         lines (atom (clojure.string/split sanitized-edn-content #"\n"))
         urls (-> (edn/read-string sanitized-edn-content)
                  (get-in path-as-vec)
                  first)]
     (assoc previous-line-indexer file-path (build-line-indexer urls lines)))))

(defn get-url-from-line [line-index file-path line]
  (let [line-index (get line-index file-path)
        path (-> (get line-index line)
                 :url)]
    (reduce (fn [acc idx]
              (->> (get line-index idx)
                   :val
                   (str acc)))
            ""
            path)))

(defn line-present-in-indexer? [line-index ^String file-path ^Integer line]
  (let [line-index (get line-index file-path)]
    (contains? line-index line)))
