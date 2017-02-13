(ns spede.parse-macro-arg)

(defn is-multi-arity? [form]
  (list? form))

(defn is-arg-list-or-multi-arity? [form]
  (or (is-multi-arity? form)
      (vector? form)))

(defn get-arg-list [fdecl]
  (let [defs             (->> fdecl
                              (drop-while (complement is-arg-list-or-multi-arity?)))
        multi-arity-defs (->> defs
                              (take-while is-multi-arity?)
                              (map first))
        single-arity-al (first defs)]
    (if (empty? multi-arity-defs)
      [single-arity-al]
      multi-arity-defs)))
