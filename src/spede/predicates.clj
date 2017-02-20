(ns spede.predicates)

(defn is-spec-kw? [a]
  (and (keyword? a)
       (not (nil? (namespace a)))))

(defn is-binding? [form]
  (vector? form))

(defn is-multi-arity? [form]
  (and (seq? form)
       (-> form first is-binding?)))

(defn is-binding-or-multi-arity? [form]
  (or (is-multi-arity? form)
      (is-binding? form)))

(defn is-fdef-arg? [[a b]]
  (keyword? a))
