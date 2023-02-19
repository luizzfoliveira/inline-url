(ns carvalhedo.inline-url.util)

(defn get-children [urls]
  (-> (filterv #(and (vector? %)
                     (string? (first %))) urls)
      not-empty))

(defn sanitize-edn [edn-content]
  (-> edn-content
      (clojure.string/replace #"#(?!(\{|_))(.*?)(?=(\s|$))" "")))
