(ns spede.defn
  (:require [spede.predicates :as preds]))

(defn make-vanilla-arg-list
  "Take a spec-decorated argument list and return the vanilla version"
  [arg]
  (cond
    (vector? arg) (->> arg
                       (filter (complement preds/is-spec-kw?))
                       (mapv make-vanilla-arg-list))
    (seq? arg) (->> (into [(make-vanilla-arg-list (first arg))]
                           (rest arg))
                     (apply list))
    ::else arg))
