(ns spede.defn-compatibility-multi-arity
  (:require  [clojure.test :as t]
             [spede.core :as sd]
             [clojure.spec :as s]
             [clojure.spec.test :as st]))

(sd/sdefn sf
          "docses"
          {:ugh 123}
          ([a] (* a a))
          ([a [b]] (* a b)))

(st/instrument `sf)

(t/deftest multi-arity-fn-calls
  (t/is (= 4 (sf 2)))
  (t/is (= 6 (sf 2 [3]))))

