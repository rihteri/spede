(ns spede.core-test
  (:require [clojure.test :as t]
            [spede.core :as es]
            [clojure.spec.test :as st]))


(es/sdefn test-fun [a b c]
          (+ a b c))

(st/instrument `test-fun)

(t/deftest test-test-fun
  (t/is (= 6 (test-fun 1 2 3))))
