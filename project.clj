(defproject spede "0.1.0-SNAPSHOT"
  :description "Spec ex Destructuring"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]]
  :main ^:skip-aot spede.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
