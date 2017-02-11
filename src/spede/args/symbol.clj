(ns spede.args.symbol
  (:require [spede.args.core :as args]
            [clojure.spec :as s]))

(defmethod args/parse-args clojure.lang.Symbol [arg]
  [(keyword (name arg)) any?])
