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

(defn is-fdef-arg? [[a b]]
  (keyword? a))

(defn get-fdef-args [fdecl]
  (->> fdecl
       (take-while (complement is-arg-list-or-multi-arity?))
       (partition 2 1)
       (filter is-fdef-arg?)
       (apply concat)
       (apply hash-map)))

(defn get-defn-args [fdecl]
  (let [before-fdef (->> fdecl
                         (take-while (complement is-arg-list-or-multi-arity?))
                         (take-while (complement keyword?)))
        after-fdef (drop-while (complement is-arg-list-or-multi-arity?) fdecl)]
    (concat before-fdef (when (not= fdecl before-fdef) after-fdef))))
