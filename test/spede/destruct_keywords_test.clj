(ns spede.destruct-keywords-test
  (:require  [clojure.test :as t]
             [clojure.spec :as s]
             [spede.core :as es]))

(s/def ::a integer?)

(es/sdefn some-func [{a ::a :as somemap}]
          (* a a))

(t/deftest argname
  (let [param-name (-> (macroexpand-1 `(es/sdefn some-func [{a ::a :as somemap}] (* a a)))
                       second
                       last
                       second)]
    (t/is (= :somemap param-name))))

(t/deftest keywords-in-destruct-form
  (t/is (= 4 (some-func {::a 2}))))
