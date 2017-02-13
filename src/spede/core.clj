(ns spede.core
  (:require [clojure.spec :as s]
            [spede.args.core :as args]
            [spede.args.map]
            [spede.args.symbol]
            [spede.parse-macro-arg :as al]
            [spede.args.array]))

(defn- make-arg [arg-list]
  [:args
   (apply list (into ['clojure.spec/cat]
                     (apply concat (map args/parse-args arg-list))))])

(defn- make-fdef [name arg-list]
  (apply list
         (into ['clojure.spec/fdef name]
               (make-arg arg-list))))

(defmacro sdefn
  "Define a function and function spec."
  {:arglists '([name doc-string? attr-map? [params*] prepost-map? body]
               #_[name doc-string? attr-map? ([params*] prepost-map? body)+ attr-map?])}
  [name & fdecl]
  (let [arg-list (al/get-arg-list fdecl)]
    `(do
       ~(make-fdef name arg-list)
       ~(apply list (into [`defn name]
                          fdecl)))))


