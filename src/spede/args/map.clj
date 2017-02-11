(ns spede.args.map
  (:require [spede.args.core :as args]))

(defn get-keyword [[map-key map-val]]
  (when (keyword? map-val)
    map-val))

(defn is-namespaced-keyword? [k]
  (and (not (nil? (namespace k)))
       (keyword? k)))

(defn make-namespaced-keyword [symb]
  (keyword (str *ns*) (name symb)))

(defn parse-map [{:keys [:keys] :as arg}]
  (let [argname    (if (:as arg)
                     (keyword (name (:as arg)))
                     (keyword (gensym)))
        named-keys (->> arg
                        (mapv get-keyword)
                        (filter (complement nil?)))
        un-keys    (mapv make-namespaced-keyword
                         (filter (complement is-namespaced-keyword?) keys))
        keys       (map keyword (filter is-namespaced-keyword? keys))
        all-keys   (apply vector (concat named-keys keys))]
    [argname `(clojure.spec/keys :req ~all-keys :req-un ~un-keys)]))

(defmethod args/parse-args clojure.lang.PersistentHashMap [arg] (parse-map arg))
(defmethod args/parse-args clojure.lang.PersistentArrayMap [arg] (parse-map arg))

