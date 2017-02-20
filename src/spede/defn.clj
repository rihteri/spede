(ns spede.defn
  (:require [spede.predicates :as preds]
            [spede.parse-macro-arg :as al]))

(defn make-vanilla-arg-list
  "Turn a spec-decorated argument list into defn-compatible, recursively."
  [arg]
  (if (preds/is-binding? arg)
    (->> arg
         (filter (complement preds/is-spec-kw?))
         (mapv make-vanilla-arg-list))
    arg))


(defn fix-multi-arity-args [arg]
  (->> (into [(make-vanilla-arg-list (first arg))]
             (rest arg))
       (apply list)))

(defn fix-arg-list [binding-or-multi-bodies]
  (if (preds/is-binding? (first binding-or-multi-bodies))
    [(make-vanilla-arg-list (first binding-or-multi-bodies))]
    (map fix-multi-arity-args binding-or-multi-bodies)))

(defn get-defn-args [fdecl]
  (let [before-fdef             (->> fdecl
                                     (take-while (complement preds/is-binding-or-multi-arity?))
                                     (take-while (complement keyword?)))
        binding-or-multi-bodies (al/get-binding-or-multi-bodies fdecl)
        after-fdef              (->> fdecl
                                     al/drop-till-binding-or-multi-arg-bodies
                                     (drop-while preds/is-binding-or-multi-arity?))]
    (concat before-fdef
            (fix-arg-list binding-or-multi-bodies)
            after-fdef)))
