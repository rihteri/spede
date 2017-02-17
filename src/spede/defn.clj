(ns spede.defn
  (:require [spede.predicates :as preds]))

(defn make-vanilla-arg-list
  "Turn a spec-decorated argument list into defn-compatible, recursively."
  [arg]
  (if (preds/is-binding? arg)
    (->> arg
         (filter (complement preds/is-spec-kw?))
         (mapv make-vanilla-arg-list))
    arg))


