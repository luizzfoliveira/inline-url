(ns carvalhedo.inline-url.read-edn-test
  (:require [carvalhedo.inline-url.read-edn :as read-edn]
            [cheshire.core :as json]
            [clojure.test :refer :all]))

(deftest should-return-true-for-string-with-edn-file-type
  (is (read-edn/is-edn? "blabla.edn"))
  (is (read-edn/is-edn? "bleble.blabla.edn"))
  (is (read-edn/is-edn? "bleble.blabla..edn")))

(deftest should-return-false-for-string-without-edn-file-type
  (is (not (read-edn/is-edn? "blabla")))
  (is (not (read-edn/is-edn? "bleble.edn.blabla")))
  (is (not (read-edn/is-edn? "edn"))))

(def urls [[["/a" :interceptors {:post [:interceptors #handler :some-handler]
                                 :get [:interceptors #handler :other-handler]
                                 :put [:interceptors #handler :some-other-handler]
                                 :delete [:interceptors #handler :last-handler]}]
            ["/b" 123 ["/c" 123] ["/d" 123 ["/e" {:post #handler :some-handler}]]]]])

(def urls-2 [[["/aaa" 123]
              ["/something" 123
               ["/other-some" 123
                ["/thing" 123 123 123]]
               ["/some-other" 123
                ["/thing" {:post [:interceptors #handler :something]
                           :wrong-verb :nothing}]]]]])

(def edn-content (str {:a {:b urls}}))

(def edn-content-2 (str {:a {:b urls-2}}))

(deftest should-return-handler-from-vec
  (let [url-as-vec ["/b" "/d" "/e"]
        url-as-vec-2 ["/a"]
        url-as-vec-3 ["/something" "/some-other" "/thing"]]
    (is (= {:post :some-handler}
           (read-edn/get-handler (first urls) url-as-vec)))
    (is (= {:post   :some-handler
            :get    :other-handler
            :put    :some-other-handler
            :delete :last-handler}
           (read-edn/get-handler (first urls) url-as-vec-2)))
    (is (= {:post :something}
           (read-edn/get-handler (first urls-2) url-as-vec-3)))))

(deftest should-return-nil-for-url-not-found
  (let [not-found-url-as-vec-1 ["/b" "/d" "/e" "/d"]
        not-found-url-as-vec-2 ["/b" "/e"]
        not-found-url-as-vec-3 ["/a" "/b"]
        not-found-url-as-vec-4 ["/j"]
        not-found-url-as-vec-5 ["/c"]
        not-found-url-as-vec-6 [""]]
    (is (= nil
           (read-edn/get-handler nil not-found-url-as-vec-1)))

    (is (= nil
           (read-edn/get-handler nil nil)))

    (is (= nil
           (read-edn/get-handler (first urls) nil)))

    (is (= nil
           (read-edn/get-handler [] not-found-url-as-vec-1)))

    (is (= nil
           (read-edn/get-handler [] [])))

    (is (= nil
           (read-edn/get-handler (first urls) [])))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-1)))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-2)))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-3)))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-4)))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-5)))

    (is (= nil
           (read-edn/get-handler (first urls) not-found-url-as-vec-6)))))

(deftest should-find-handler-map
  (is (= {:post [:interceptors :some-handler]}
         (read-edn/get-handler-map ["/a" {:post [:interceptors :some-handler]}])))

  (is (= {:post [:interceptors :some-handler]
          :get [:interceptors :other-handler]
          :put [:interceptors :some-other-handler]
          :delete [:interceptors :last-handler]}
         (read-edn/get-handler-map ["/a"
                                    :interceptors
                                    {:post   [:interceptors :some-handler]
                                     :get    [:interceptors :other-handler]
                                     :put    [:interceptors :some-other-handler]
                                     :delete [:interceptors :last-handler]}
                                    ["/b" {:post [:interceptors :wrong-handler]}]]))))

(deftest should-return-correct-handler-map-from-edn-file-content-and-url
  (let [url-1 "/b/d/e"
        url-2 "/a"
        url-3 "/something/some-other/thing"]
    (is (= {"post" "some-handler"}
           (json/parse-string (read-edn/search-url edn-content [:a :b] url-1))))
    (is (= {"post"   "some-handler"
            "get"    "other-handler"
            "put"    "some-other-handler"
            "delete" "last-handler"}
           (json/parse-string (read-edn/search-url edn-content [:a :b] url-2))))
    (is (= {"post" "something"}
           (json/parse-string (read-edn/search-url edn-content-2 [:a :b] url-3))))))
