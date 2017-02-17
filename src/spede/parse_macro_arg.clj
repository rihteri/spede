(ns spede.parse-macro-arg
  (:require [spede.defn :as defn]
            [spede.predicates :as preds]))

(defn get-arg-list [fdecl]
  (let [defs             (->> fdecl
                              (drop-while (complement preds/is-binding-or-multi-arity?)))
        multi-arity-defs (->> defs
                              (take-while preds/is-multi-arity?)
                              (map first))
        single-arity-al (first defs)]
    (if (empty? multi-arity-defs)
      [single-arity-al]
      multi-arity-defs)))

(defn is-fdef-arg? [[a b]]
  (keyword? a))

(defn get-fdef-args [fdecl]
  (->> fdecl
       (take-while (complement preds/is-binding-or-multi-arity?))
       (partition 2 1)
       (filter is-fdef-arg?)
       (apply concat)
       (apply hash-map)))

(defn fix-multi-arity-args [arg]
  (->> (into [(defn/make-vanilla-arg-list (first arg))]
             (rest arg))
       (apply list)))

(defn fix-arg-list [arg-list-or-multi-bodies]
  (if (preds/is-binding? (first arg-list-or-multi-bodies))
    [(defn/make-vanilla-arg-list (first arg-list-or-multi-bodies))]
    (map fix-multi-arity-args arg-list-or-multi-bodies)))

(defn drop-till-binding-or-multi-arg-bodies [fdecl]
  (drop-while (complement preds/is-binding-or-multi-arity?) fdecl))

(defn get-defn-args [fdecl]
  (let [before-fdef               (->> fdecl
                                       (take-while (complement preds/is-binding-or-multi-arity?))
                                       (take-while (complement keyword?)))
        arg-lists-or-multi-bodies (->> fdecl
                                       drop-till-binding-or-multi-arg-bodies
                                       (take-while preds/is-binding-or-multi-arity?))
        after-fdef                (->> fdecl
                                       drop-till-binding-or-multi-arg-bodies
                                       (drop-while preds/is-binding-or-multi-arity?))]
    (concat before-fdef
            (fix-arg-list arg-lists-or-multi-bodies)
            after-fdef)))
