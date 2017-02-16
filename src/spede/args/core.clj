(ns spede.args.core
  (:require [clojure.spec :as s]
            [clojure.string :as str]
            [spede.defn :as defn]))

(defmulti parse-args
  (fn [symbol-and-spec]
    (-> symbol-and-spec first type)))

(defn make-arg-kw-pair [arg-list]
  (let [kw (keyword (str/join "-" [(str (count (defn/make-vanilla-arg-list arg-list)))
                                   "arid"]))]
    [kw (second (parse-args [arg-list nil]))]))

(defn make-args [arg-lists]
  (if (= 1 (count arg-lists))
    (second (parse-args [(first arg-lists) nil]))
    (apply list (into [`s/or]
                      (->> arg-lists
                           (map make-arg-kw-pair)
                           (apply concat))))))

