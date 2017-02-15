(ns spede.test-utils
  (:require  [clojure.test :as t]
             [clojure.spec :as s]))

(def spec-err #"did not conform to spec")

(def get-spec-forms-in-map
  (comp (partial apply hash-map)
        (partial drop 1)
        s/form
        s/get-spec))

(defn get-name-of-first-arg [sym]
  (->> sym
       get-spec-forms-in-map
       :args
       (drop 1)
       first))

(defn get-spec-of-first-arg [sym]
  (->> sym
       get-spec-forms-in-map
       :args
       (drop 2)
       first))
