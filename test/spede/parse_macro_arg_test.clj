(ns spede.parse-macro-arg-test
  (:require [spede.parse-macro-arg :as ma]
            [clojure.test :as t]
            [spede.predicates :as preds]))

(t/deftest arg-list-or-multi-arity
  (t/is (preds/is-binding-or-multi-arity? `[a b]))
  (t/is (preds/is-binding-or-multi-arity? `([a b] (* a b))))
  (t/is (not (preds/is-binding-or-multi-arity? #(* % 2)))))

