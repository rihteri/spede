(ns spede.parse-macro-arg-test
  (:require [spede.parse-macro-arg :as ma]
            [clojure.test :as t]))

(t/deftest arg-list-or-multi-arity
  (t/is (ma/is-arg-list-or-multi-arity? `[a b]))
  (t/is (ma/is-arg-list-or-multi-arity? `([a b] (* a b))))
  (t/is (not (ma/is-arg-list-or-multi-arity? #(* % 2)))))

