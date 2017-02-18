(ns spede.args.core
  (:require [clojure.spec :as s]
            [clojure.string :as str]
            [spede.defn :as defn]))

(defmulti parse-args
  (fn [symbol-and-spec]
    (-> symbol-and-spec first type)))



