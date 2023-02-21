(ns carvalhedo.inline-url.specs
  (:require [clojure.spec.alpha :as s]))

(s/def ::url-path (s/coll-of keyword?))
