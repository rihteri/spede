(ns spede.args.map
  (:require [spede.args.core :as args]))

(defn get-keyword [[map-key map-val]]
  (when (keyword? map-val)
    map-val))

(defn parse-map [arg]
  (let [argname (if (:as arg)
                  (keyword (name (:as arg)))
                  (keyword (gensym)))]
    [argname `(clojure.spec/keys :req ~(->> arg
                                            (mapv get-keyword)
                                            (filter (complement nil?))))]))

(defmethod args/parse-args clojure.lang.PersistentHashMap [arg] (parse-map arg))
(defmethod args/parse-args clojure.lang.PersistentArrayMap [arg] (parse-map arg))

