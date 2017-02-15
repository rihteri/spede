(ns spede.func-spec-test
  (:require  [clojure.test :as t]
             [spede.core :as sd]
             [spede.test-utils :as tu]
             [clojure.spec.test :as st])
  (:import clojure.lang.ExceptionInfo))

(sd/sdefn fun
  "function which has function specs"
  :args #(= 0 (apply + %))
  [a b c]
  (* a b c))

(st/instrument `fun)

(t/deftest function-spec
  (t/is (= 6 (fun -1 -2 3)))
  (t/is (thrown-with-msg? ExceptionInfo tu/spec-err
                          (fun 1 2 3))))
