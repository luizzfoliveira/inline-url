(ns carvalhedo.inline-url.line-indexer-test
  (:require [carvalhedo.inline-url.line-indexer :as line-indexer]
            [clojure.test :refer :all]))

#_{:a
   {:b
    {:routes  [[["/v1"
                 ["/something"
                  {:get #handler :some-handler}]
                 ["/other-thing"
                  ^:interceptors [:interceptor]
                  ["/final-url"
                   {:post [^:interceptors [:interceptor]
                           #handler :other-handler]}]]
                 ["/final-url"
                  {:post [^:interceptors [:interceptor]
                          "/final-url"
                          #handler :final-handler]}]]
                ["/v2"
                 ["/something-but-better"
                  {:get #handler :some-handler}]
                 ["/other-thing-but-much-better"
                  ^:interceptors [:interceptor]
                  ["/final-url-really-better"
                   {:post [^:interceptors [:interceptor]
                           #handler :other-handler]}]]]]]
     :port    3000
     :adapter :jetty}}}

(def edn-content "{:a\n   {:b\n    {:routes  [[[\"/v1\"\n                 [\"/something\"\n                  {:get #handler :some-handler}]\n                 [\"/other-thing\"\n                  ^:interceptors [:interceptor]\n                  [\"/final-url\"\n                   {:post [^:interceptors [:interceptor]\n                           #handler :other-handler]}]]\n                 [\"/final-url\"\n                  {:post [^:interceptors [:interceptor]\n                          \"/final-url\"\n                          #handler :final-handler]}]]\n                [\"/v2\"\n                 [\"/something-but-better\"\n                  {:get #handler :some-handler}]\n                 [\"/other-thing-but-much-better\"\n                  ^:interceptors [:interceptor]\n                  [\"/final-url-really-better\"\n                   {:post [^:interceptors [:interceptor]\n                           #handler :other-handler]}]]]]]\n     :port    3000\n     :adapter :jetty}}}")

(def edn-lines (clojure.string/split edn-content #"\n"))

(def urls [[["/v1"
             ["/something"
              {:get #handler :some-handler}]
             ["/other-thing"
              ^:interceptors [:interceptor]
              ["/final-url"
               {:post [^:interceptors [:interceptor]
                       #handler :other-handler]}]]
             ["/final-url"
              {:post [^:interceptors [:interceptor]
                      #handler :final-handler]}]]
            ["/v2"
             ["/something-but-better"
              {:get #handler :some-handler}]
             ["/other-thing-but-much-better"
              ^:interceptors [:interceptor]
              ["/final-url-really-better"
               {:post [^:interceptors [:interceptor]
                       #handler :other-handler]}]]]]])

(def url-vec ["/other-thing"
              ^:interceptors [:interceptor]
              ["/final-url"
               {:post [^:interceptors [:interceptor]
                       #handler :other-handler]}]])

(def lines ["something [\"/some-url\""
            "nothing [\"/some-other-url\""
            "some other thing [\"/some-url\""])

(deftest should-get-line-number
  (is (= 1
         (line-indexer/get-line-number "/some-other-url" lines 0)))

  (is (= 0
         (line-indexer/get-line-number "/some-url" lines 0)))

  (is (= 2
         (line-indexer/get-line-number "/some-url" lines 1))))

(deftest should-get-line-map
  (is (= {5 {:val "/other-thing"
             :url [5]}
          7 {:val "/final-url"
             :url [5 7]}}
         (line-indexer/gen-url-map-from-vec url-vec (atom edn-lines))))

  (is (= {2 {:val "/v1"
             :url [2]}
          3 {:val "/something"
             :url [2 3]}
          5 {:val "/other-thing"
             :url [2 5]}
          7 {:val "/final-url"
             :url [2 5 7]}
          10 {:val "/final-url"
              :url [2 10]}}
         (line-indexer/gen-url-map-from-vec (first (first urls)) (atom edn-lines)))))

(deftest should-build-line-indexer
  (is (= {2 {:val "/v1"
             :url [2]}
          3 {:val "/something"
             :url [2 3]}
          5 {:val "/other-thing"
             :url [2 5]}
          7 {:val "/final-url"
             :url [2 5 7]}
          10 {:val "/final-url"
              :url [2 10]}
          14 {:val "/v2"
              :url [14]}
          15 {:val "/something-but-better"
              :url [14 15]}
          17 {:val "/other-thing-but-much-better"
              :url [14 17]}
          19 {:val "/final-url-really-better"
              :url [14 17 19]}}
         (line-indexer/build-line-indexer (first urls)
                                          (atom edn-lines)))))

(deftest should-index-in-correct-lines-from-multiple-files
  (let [previous-line-indexer {"something/something/some.edn" {2  {:val "/v1"
                                                                   :url [2]}
                                                               3  {:val "/something"
                                                                   :url [2 3]}
                                                               5  {:val "/other-thing"
                                                                   :url [2 5]}
                                                               7  {:val "/final-url"
                                                                   :url [2 5 7]}
                                                               10 {:val "/final-url"
                                                                   :url [2 10]}
                                                               14 {:val "/v2"
                                                                   :url [14]}
                                                               15 {:val "/something-but-better"
                                                                   :url [14 15]}
                                                               17 {:val "/other-thing-but-much-better"
                                                                   :url [14 17]}
                                                               19 {:val "/final-url-really-better"
                                                                   :url [14 17 19]}}}]
    (is (= {"something/something/some.edn" {2  {:val "/v1"
                                                :url [2]}
                                            3  {:val "/something"
                                                :url [2 3]}
                                            5  {:val "/other-thing"
                                                :url [2 5]}
                                            7  {:val "/final-url"
                                                :url [2 5 7]}
                                            10 {:val "/final-url"
                                                :url [2 10]}
                                            14 {:val "/v2"
                                                :url [14]}
                                            15 {:val "/something-but-better"
                                                :url [14 15]}
                                            17 {:val "/other-thing-but-much-better"
                                                :url [14 17]}
                                            19 {:val "/final-url-really-better"
                                                :url [14 17 19]}}}
           (line-indexer/gen-line-indexer edn-content "something/something/some.edn" "[:a :b :routes]")))

    (is (= {"something/something/some.edn"       {2  {:val "/v1"
                                                      :url [2]}
                                                  3  {:val "/something"
                                                      :url [2 3]}
                                                  5  {:val "/other-thing"
                                                      :url [2 5]}
                                                  7  {:val "/final-url"
                                                      :url [2 5 7]}
                                                  10 {:val "/final-url"
                                                      :url [2 10]}
                                                  14 {:val "/v2"
                                                      :url [14]}
                                                  15 {:val "/something-but-better"
                                                      :url [14 15]}
                                                  17 {:val "/other-thing-but-much-better"
                                                      :url [14 17]}
                                                  19 {:val "/final-url-really-better"
                                                      :url [14 17 19]}}

            "something/something/some-other.edn" {2  {:val "/v1"
                                                      :url [2]}
                                                  3  {:val "/something"
                                                      :url [2 3]}
                                                  5  {:val "/other-thing"
                                                      :url [2 5]}
                                                  7  {:val "/final-url"
                                                      :url [2 5 7]}
                                                  10 {:val "/final-url"
                                                      :url [2 10]}
                                                  14 {:val "/v2"
                                                      :url [14]}
                                                  15 {:val "/something-but-better"
                                                      :url [14 15]}
                                                  17 {:val "/other-thing-but-much-better"
                                                      :url [14 17]}
                                                  19 {:val "/final-url-really-better"
                                                      :url [14 17 19]}}}
           (line-indexer/gen-line-indexer previous-line-indexer edn-content "something/something/some-other.edn" "[:a :b :routes]")))))

(def line-index {"something/something/some.edn" {2 {:val "/v1"
                                                    :url [2]}
                                                 3 {:val "/something"
                                                    :url [2 3]}
                                                 5 {:val "/other-thing"
                                                    :url [2 5]}
                                                 7 {:val "/final-url"
                                                    :url [2 5 7]}
                                                 10 {:val "/final-url"
                                                     :url [2 10]}
                                                 14 {:val "/v2"
                                                     :url [14]}
                                                 15 {:val "/something-but-better"
                                                     :url [14 15]}
                                                 17 {:val "/other-thing-but-much-better"
                                                     :url [14 17]}
                                                 19 {:val "/final-url-really-better"
                                                     :url [14 17 19]}}})

(deftest should-return-url-from-index-and-line-number
  (is (= "/v1/something"
         (line-indexer/get-url-from-line line-index "something/something/some.edn" 3)))

  (is (= "/v1/other-thing"
         (line-indexer/get-url-from-line line-index "something/something/some.edn" 5)))

  (is (= "/v1/other-thing/final-url"
         (line-indexer/get-url-from-line line-index "something/something/some.edn" 7)))

  (is (= "/v1/final-url"
         (line-indexer/get-url-from-line line-index "something/something/some.edn" 10))))

(deftest should-return-empty-string-from-index-or-line-number-not-in-indexer
  (is (= ""
         (line-indexer/get-url-from-line line-index "something/something/some.edn" 1)))

  (is (= ""
         (line-indexer/get-url-from-line line-index "something/something/s.edn" 5))))

(deftest should-return-true-for-line-numbers-in-indexer
  (is (line-indexer/line-present-in-indexer? line-index "something/something/some.edn" 2))

  (is (line-indexer/line-present-in-indexer? line-index "something/something/some.edn" 7)))

(deftest should-return-false-for-line-numbers-not-in-indexer
  (is (not (line-indexer/line-present-in-indexer? line-index "something/something/s.edn" 2)))

  (is (not (line-indexer/line-present-in-indexer? line-index "something/something/some.edn" 1))))
