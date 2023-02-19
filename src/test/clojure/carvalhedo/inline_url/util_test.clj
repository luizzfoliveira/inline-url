(ns carvalhedo.inline-url.util-test
  (:require [carvalhedo.inline-url.util :as util]
            [clojure.test :refer :all]))

(deftest should-get-children-from-vec
  (is (= [["/c" 123] ["/d" 123 ["/e" 123]]]
         (util/get-children ["/b" 123 ["/c" 123] ["/d" 123 ["/e" 123]]]))))

(deftest should-remove-handler-tag
  (is (= "[\"/e\" {:post [ :interceptors  :some-handler]}] #_[:something :commented]"
         (util/sanitize-edn "[\"/e\" {:post [#interceptor :interceptors #handler :some-handler]}] #_[:something :commented]")))

  (is (= "[\"/e\" {:post [:interceptors  :some-handler]} #{:some :thing}]"
         (util/sanitize-edn "[\"/e\" {:post [:interceptors #handler :some-handler]} #{:some :thing}]"))))
