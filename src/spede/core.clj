(ns spede.core
  (:require [clojure.spec :as s]
            [spede.args.core :as args]
            [spede.args.map]
            [spede.args.symbol]))

(defn make-arg [arg-list]
  [:args
   (apply list (into ['clojure.spec/cat]
                     (apply concat (map args/parse-args arg-list))))])

(defn make-fdef [name arg-list]
  (apply list
         (into ['clojure.spec/fdef name]
               (make-arg arg-list))))

(defmacro sdefn [name arg-list form]
  `(do
     ~(make-fdef name arg-list)
     (defn ~name ~arg-list
       ~form)))


