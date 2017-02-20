(ns spede.fdef
  (:require [spede.args.core :as args]
            [clojure.spec :as s]
            [spede.args.array :as arr]
            [clojure.string :as str]
            [spede.defn :as defn]
            [spede.parse-macro-arg :as al]
            [spede.predicates :as preds]))

(defn make-arity-keyword [binding]
  (->> (str/join "-" [(str (count (defn/make-vanilla-arg-list binding)))
                      "arid"])
       keyword))

(defn make-arg-kw-pair [arg-list]
  (let [kw (make-arity-keyword arg-list)]
    [kw (arr/make-cat-predicate arg-list)]))

(defn make-args [arg-lists]
  (if (= 1 (count arg-lists))
    (arr/make-cat-predicate (first arg-lists))
    (apply list (into [`s/or]
                      (->> arg-lists
                           (map make-arg-kw-pair)
                           (apply concat))))))

(defn get-arg-list [fdecl]
  (let [b-or-multi (al/get-binding-or-multi-bodies fdecl)]
    (if (preds/is-binding? (first b-or-multi))
      b-or-multi
      (map first b-or-multi))))

(defn get-fdef-args
"Given a spede-style defn declaration, extract a map
  which contains the keyword - spec pairs to be passed to fdef"
  [fdecl]
  (->> fdecl
       (take-while (complement preds/is-binding-or-multi-arity?))
       (partition 2 1)
       (filter preds/is-fdef-arg?)
       (apply concat)
       (apply hash-map)))

(defn make-fdef [name fdecl]
  (let [arg-lists (get-arg-list fdecl)
        fdef-args (get-fdef-args fdecl)]
    (apply list
           (-> ['clojure.spec/fdef name]
               (into (if (:args fdef-args)
                       [:args (apply list [`s/and
                                           (make-args arg-lists)
                                           (:args fdef-args)])]
                       [:args (make-args arg-lists)]))
               (into (->> (dissoc fdef-args :args)
                          (apply concat)))))))
