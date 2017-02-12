(ns spede.test-utils
  (:require  [clojure.test :as t]
             [clojure.spec :as s]))

(def spec-err #"did not conform to spec")

(defn get-name-of-first-arg [sym]
  (->> (s/get-spec sym)
       (s/form)
       (drop 1)
       (apply hash-map)
       :args
       (drop 1)
       first))
