(ns spede.destruct-keywords-test
  (:require  [clojure.test :as t]
             [clojure.spec :as s]
             [spede.core :as es]
             [spede.args.map :as map]
             [spede.test-utils :as tu]))

(s/def ::a integer?)

(es/sdefn some-func [{a ::a :as somemap}]
  (* a a))

(t/deftest argname
  (let [param-name (tu/get-name-of-first-arg `some-func)]
    (t/is (= :somemap param-name))))

(t/deftest keywords-in-destruct-form
  (t/is (= 4 (some-func {::a 2}))))
