(ns spede.fdef
  (:require [spede.args.core :as args]
            [clojure.spec :as s]
            [spede.args.array :as arr]
            [clojure.string :as str]
            [spede.defn :as defn]))

(defn make-arg-kw-pair [arg-list]
  (let [kw (keyword (str/join "-" [(str (count (defn/make-vanilla-arg-list arg-list)))
                                   "arid"]))]
    [kw (arr/make-cat-predicate arg-list)]))

(defn make-args [arg-lists]
  (if (= 1 (count arg-lists))
    (arr/make-cat-predicate (first arg-lists))
    (apply list (into [`s/or]
                      (->> arg-lists
                           (map make-arg-kw-pair)
                           (apply concat))))))

(defn make-fdef [name arg-lists fdef-args]
  (apply list
         (-> ['clojure.spec/fdef name]
             (into (if (:args fdef-args)
                     [:args (apply list [`s/and
                                         (make-args arg-lists)
                                         (:args fdef-args)])]
                     [:args (make-args arg-lists)]))
             (into (->> (dissoc fdef-args :args)
                        (apply concat))))))
