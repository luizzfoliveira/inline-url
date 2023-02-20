(ns carvalhedo.inline-url.search-url
  (:require [carvalhedo.inline-url.util :as util]
            [clojure.edn :as edn]
            [cheshire.core :as json]))

(defn is-edn? [^String file-name]
  (let [file-path (clojure.string/split file-name #"\.")
        file-extension (-> file-path
                           last
                           clojure.string/lower-case)]
    (and (> (count file-path) 1)
         (= "edn" file-extension))))

(defn get-handler-map [vec]
  (-> (filter map? vec)
      first
      (select-keys [:get :post :put :delete])))

(defn get-handler
  ([urls url-as-vec]
   (if (or (empty? url-as-vec)
           (empty? urls))
     nil
     (let [current-url-path (first url-as-vec)]
       (if (= current-url-path (-> urls
                                   flatten
                                   first))
         (if (>= 1 (count url-as-vec))
           (let [handler-map (->> urls
                                  first
                                  get-handler-map)]
             (reduce (fn [acc [verb interceptors]]
                       (assoc acc verb (if (coll? interceptors)
                                         (last interceptors)
                                         interceptors)))
                     {}
                     handler-map))
           (get-handler (util/get-children (first urls)) (rest url-as-vec)))
         (get-handler (rest urls) url-as-vec))))))

(defn search-url [^String edn-content path ^String url]
  (let [edn-as-map (-> (util/sanitize-edn edn-content)
                       edn/read-string)
        urls (get-in edn-as-map path)
        url-as-vec (->> (clojure.string/split url #"/")
                        rest
                        (map #(str "/" %)))]
    (-> (get-handler (first urls) url-as-vec)
        json/generate-string)))

