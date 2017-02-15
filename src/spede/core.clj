(ns spede.core
  (:require [clojure.spec :as s]
            [spede.args.core :as args]
            [spede.args.map]
            [spede.args.symbol]
            [spede.parse-macro-arg :as al]
            [spede.args.array]
            [spede.fdef :as fd]))

(defmacro sdefn
  "
Define a function and function spec. Use like you would a defn. Arguments in 'specs' will
be passed to fdef, i.e. those are keyword-spec pairs for keywords :args :ret and :fn. The :args
part will be combined with the destructuring specs with clojure.spec/and."
  {:arglists '([name doc-string? attr-map? specs [params*] prepost-map? body]
               [name doc-string? attr-map? specs ([params*] prepost-map? body)+ attr-map?])
   :style/indent :defn}
  [name & fdecl]
  (let [arg-lists (al/get-arg-list fdecl)
        fdef-args (al/get-fdef-args fdecl)
        defn-args (al/get-defn-args fdecl)]
    `(do
       ~(fd/make-fdef name arg-lists fdef-args)
       ~(apply list (into [`defn name]
                          defn-args)))))


