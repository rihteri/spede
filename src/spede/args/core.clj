(ns spede.args.core
  (:require [clojure.spec :as s]
            [clojure.string :as str]))

(defmulti parse-args type)

(defn make-arg [arg-list]
  (apply list (into ['clojure.spec/cat]
                    (apply concat (map parse-args arg-list)))))

(defn make-arg-kw-pair [arg-list]
  (let [kw (keyword (str/join "-" [(str (count arg-list))
                                   "arid"]))]
    [kw (make-arg arg-list)]))

(defn make-args [arg-lists]
  [:args (if (= 1 (count arg-lists))
           (make-arg (first arg-lists))
           (apply list (into [`s/or]
                             (->> arg-lists
                                  (map make-arg-kw-pair)
                                  (apply concat)))))])

