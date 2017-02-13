(ns spede.core
  (:require [clojure.spec :as s]
            [spede.args.core :as args]
            [spede.args.map]
            [spede.args.symbol]
            [spede.parse-macro-arg :as al]
            [spede.args.array]))

(defn- make-fdef [name arg-lists]
  (apply list
         (into ['clojure.spec/fdef name]
               (args/make-args arg-lists))))

(defmacro sdefn
  "Define a function and function spec. Use like you would a defn."
  {:arglists '([name doc-string? attr-map? [params*] prepost-map? body]
               [name doc-string? attr-map? ([params*] prepost-map? body)+ attr-map?])}
  [name & fdecl]
  (let [arg-lists (al/get-arg-list fdecl)]
    `(do
       ~(make-fdef name arg-lists)
       ~(apply list (into [`defn name]
                          fdecl)))))


