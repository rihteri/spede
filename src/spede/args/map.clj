(ns spede.args.map
  (:require [spede.args.core :as args]))

(defn parse-map [arg]
  [(keyword (gensym)) `(clojure.spec/keys :req ~(mapv second arg))])

(defmethod args/parse-args clojure.lang.PersistentHashMap [arg] (parse-map arg))
(defmethod args/parse-args clojure.lang.PersistentArrayMap [arg] (parse-map arg))

