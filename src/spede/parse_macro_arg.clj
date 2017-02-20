(ns spede.parse-macro-arg
  (:require [spede.predicates :as preds]))

(defn drop-till-binding-or-multi-arg-bodies [fdecl]
  (drop-while (complement preds/is-binding-or-multi-arity?) fdecl))

(defn get-binding-or-multi-bodies
"Given a function declaration (past name symbol), extract a list which
  contains either the single binding vector or the multi-arity function
  bodies"
  [fdecl]
  (->> fdecl
       drop-till-binding-or-multi-arg-bodies
       (take-while preds/is-binding-or-multi-arity?)))



