(ns spede.core-test
  (:require [clojure.test :as t]
            [spede.core :as es]
            [clojure.spec.test :as st]
            [spede.core :as sd]))


(sd/sdefn test-fun [a b c]
  (+ a b c))

(sd/sdefn unbounded [a b & c]
  (apply + c))

(st/instrument [`test-fun
                `unbounded])

(t/deftest test-test-fun
  (t/is (= 6 (test-fun 1 2 3))))

(t/deftest test-unbounded-fun
  (t/is (= 12 (unbounded 1 2 3 4 5))))
