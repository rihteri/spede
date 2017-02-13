(ns spede.defn-compatibility
  (:require  [clojure.test :as t]
             [spede.core :as sd]))

(sd/sdefn specced-fun
          "some doc-str"
          {:ugh 123}
          [a b]
          {:pre [(pos? a)]
           :post [(= % (* a a))]}
          (+ a b)
          (* a b))

(t/deftest fundef
  (t/is (= 1 (specced-fun 1 1)))
  (t/is (thrown? AssertionError (specced-fun 1 2)))
  (t/is (thrown? AssertionError (specced-fun -1 -1)))
  (let [md (meta #'specced-fun)]
    (t/is (= "some doc-str" (:doc md)))
    (t/is (= 123 (:ugh md)))))
