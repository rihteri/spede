(ns spede.args.array
  (:require [spede.args.core :as args]
            [clojure.spec :as s]))

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

(defn make-seqable-predicate [min-len is-unbounded]
  (let [operator (if is-unbounded `>= `=)]
    `(s/and seqable?
            #(~operator (count %) ~min-len))))

(defn normal-symbol? [a]
  (and (not (is-qualifier? a))
       (not (is-rest-placeholder? a))))

(defn is-spec-kw? [a]
  (and (keyword? a)
       (not (nil? (namespace a)))))

(defn make-symbol-spec-pair [[a b]]
  (cond
    (or (not (normal-symbol? a))
        (is-spec-kw? a)) nil
    (not (is-spec-kw? b)) [a nil]
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
                            (take 2))]
    (->> (-> ['clojure.spec/cat]
             (into (->> sym-spec-pairs
                        (map args/parse-args)
                        (map wrap-in-spec-if-regexp-op)
                        (apply concat))))
         (apply list))))

(defn not-empty? [a]
  (-> a empty? not))

(defn is-destruct? [a]
  (contains? #{clojure.lang.PersistentVector
               clojure.lang.PersistentArrayMap
               clojure.lang.PersistentHashMap}
               (type a)))

(defn contains-destruct-or-specs? [arg]
  (let [has-specs (->> arg
                       (filter is-spec-kw?)
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
                       (-> (gensym) name keyword))
        is-unbounded (->> arg
                          (filter is-rest-placeholder?)
                          empty?
                          not)
        min-len      (->> arg
                          (partition 2 1 [nil])
                          (take-while (complement is-qualifier?))
                          (map first)
                          (take-while (complement is-rest-placeholder?))
                          count)]
    [argname
     (if (contains-destruct-or-specs? arg)
       (make-cat-predicate arg)
       (make-seqable-predicate min-len is-unbounded))]))
