(ns spede.args.array
  (:require [spede.args.core :as args]
            [clojure.spec :as s]))

(defn is-qualifier? [[a b]]
  (keyword? a))

(defn is-rest-placeholder? [sym]
  (= sym `&))

(defn make-predicate [min-len is-unbounded]
  (let [operator (if is-unbounded `>= `=)]
    `(s/and seqable?
            #(~operator (count %) ~min-len))))

(defmethod args/parse-args clojure.lang.PersistentVector [arg]
  (let [qualifiers   (->> arg
                          (partition 2 1)
                          (filter is-qualifier?)
                          (apply concat)
                          (apply hash-map))
        argname      (if (:as qualifiers)
                       (-> qualifiers :as name keyword)
                       (-> (gensym) name keyword))
        is-unbounded (->> arg
                          (filter is-rest-placeholder?)
                          empty?
                          not)
        min-len      (->> arg
                          (partition 2 1 nil)
                          (take-while (complement is-qualifier?))
                          (map first)
                          (take-while (complement is-rest-placeholder?))
                          count)]
    [argname (make-predicate min-len is-unbounded)]))
