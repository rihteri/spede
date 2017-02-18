(ns spede.args.array
  (:require [spede.args.core :as args]
            [clojure.spec :as s]
            [spede.predicates :as preds]))

(defn is-qualifier?
"Check if the argument or first member of argument pair
  is a qualifier in list destructuring, e.g. :as"
  [pair-or-one]
  (let [a (if (seqable? pair-or-one)
            (first pair-or-one)
            pair-or-one)]
    (and (keyword? a)
         (nil? (namespace a)))))

(defn is-rest-placeholder? [sym]
  (= sym `&))

(defn make-seqable-predicate [arg]
  (let [is-unbounded (->> arg
                          (filter is-rest-placeholder?)
                          empty?
                          not)
        operator     (if is-unbounded `>= `=)
        min-len      (->> arg
                          (partition 2 1 [nil])
                          (take-while (complement is-qualifier?))
                          (map first)
                          (take-while (complement is-rest-placeholder?))
                          count)]
    `(s/and seqable?
            #(~operator (count %) ~min-len))))

(defn normal-symbol? [a]
  (and (not (is-qualifier? a))
       (not (is-rest-placeholder? a))))

(defn make-symbol-spec-pair [[a b]]
  (cond
    (or (not (normal-symbol? a))
        (preds/is-spec-kw? a)) nil
    (not (preds/is-spec-kw? b)) [a nil]
    :else [a b]))

(defn wrap-in-spec-if-regexp-op [[argname argspec]]
  (let [is-re (and (seq? argspec)
                   (= `s/cat (first argspec)))]
    [argname (if is-re
               `(s/spec ~argspec)
               argspec)]))

(defn make-cat-predicate [arg]
  (let [sym-spec-pairs (->> arg
                            (take-while normal-symbol?)
                            (partition 2 1 [nil])
                            (map make-symbol-spec-pair)
                            (filter (complement nil?)))
        rest-symb      (->> arg
                            (drop-while (complement is-rest-placeholder?))
                            (drop 1)
                            (take 2))
        rest-kw        (when (not (empty? rest-symb))
                         (-> rest-symb first name keyword))
        rest-spec (or (second rest-symb) `any?)]
    (->> (-> ['clojure.spec/cat]
             (into (->> sym-spec-pairs
                        (map args/parse-args)
                        (map wrap-in-spec-if-regexp-op)
                        (apply concat)))
             (into (filter (complement nil?) [rest-kw (when rest-kw
                                                        `(s/* ~rest-spec))])))
         (apply list))))

(defn not-empty? [a]
  (-> a empty? not))

(defn is-destruct? [a]
  (contains? #{clojure.lang.PersistentVector
               clojure.lang.PersistentArrayMap
               clojure.lang.PersistentHashMap}
               (type a)))

(defn contains-destruct-or-specs? [arg]
  (let [has-specs     (->> arg
                           (filter preds/is-spec-kw?)
                           not-empty?)
        has-destructs (->> arg
                           (filter is-destruct?)
                           not-empty?)]
    (or has-specs has-destructs)))

(defmethod args/parse-args clojure.lang.PersistentVector [[arg spec]]
  (let [qualifiers   (->> arg
                          (partition 2 1)
                          (filter is-qualifier?)
                          (apply concat)
                          (apply hash-map))
        argname      (if (:as qualifiers)
                       (-> qualifiers :as name keyword)
                       (-> (gensym) name keyword))]
    [argname
     (if (contains-destruct-or-specs? arg)
       (make-cat-predicate arg)
       (make-seqable-predicate arg))]))
