(ns spede.fdef
  (:require [spede.args.core :as args]
            [clojure.spec :as s]))

(defn make-fdef [name arg-lists fdef-args]
  (apply list
         (-> ['clojure.spec/fdef name]
             (into (if (:args fdef-args)
                     [:args (apply list [`s/and
                                         (args/make-args arg-lists)
                                         (:args fdef-args)])]
                     [:args (args/make-args arg-lists)]))
             (into (->> (dissoc fdef-args :args)
                        (apply concat))))))
