(ns spede.predicates)

(defn is-spec-kw? [a]
  (and (keyword? a)
       (not (nil? (namespace a)))))
