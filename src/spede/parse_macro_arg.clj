(ns spede.parse-macro-arg
  (:require [spede.defn :as defn]))

(defn is-arg-list? [form]
  (vector? form))

(defn is-multi-arity? [form]
  (and (seq? form)
       (-> form first is-arg-list?)))

(defn is-arg-list-or-multi-arity? [form]
  (or (is-multi-arity? form)
      (is-arg-list? form)))

(defn get-arg-list [fdecl]
  (let [defs             (->> fdecl
                              (drop-while (complement is-arg-list-or-multi-arity?)))
        multi-arity-defs (->> defs
                              (take-while is-multi-arity?)
                              (map first))
        single-arity-al (first defs)]
    (if (empty? multi-arity-defs)
      [single-arity-al]
      multi-arity-defs)))

(defn is-fdef-arg? [[a b]]
  (keyword? a))

(defn get-fdef-args [fdecl]
  (->> fdecl
       (take-while (complement is-arg-list-or-multi-arity?))
       (partition 2 1)
       (filter is-fdef-arg?)
       (apply concat)
       (apply hash-map)))

(defn get-defn-args [fdecl]
  (let [before-fdef               (->> fdecl
                                       (take-while (complement is-arg-list-or-multi-arity?))
                                       (take-while (complement keyword?)))
        arg-lists-or-multi-bodies (->> fdecl
                                       (drop-while (complement is-arg-list-or-multi-arity?))
                                       (take-while is-arg-list-or-multi-arity?))
        after-fdef                (->> fdecl
                                       (drop-while (complement is-arg-list-or-multi-arity?))
                                       (drop-while is-arg-list-or-multi-arity?))]
    (concat before-fdef
            (map defn/make-vanilla-arg-list arg-lists-or-multi-bodies)
            after-fdef)))
