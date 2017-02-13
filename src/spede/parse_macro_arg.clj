(ns spede.parse-macro-arg)

(defn is-arg-list? [form]
  (vector? form))

(defn get-arg-list [fdecl]
  (->> fdecl
       (drop-while (complement is-arg-list?))
       first))
