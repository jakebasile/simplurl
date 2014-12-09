(defproject simplurl "0.1.0-SNAPSHOT"
  :description "A stupid URL shortener thing."
  :url "https://jkbsl.org/simplurl"
  :license {:name "Simplified BSD"
            :url "http://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.datomic/datomic-free "0.9.5067"]
                 [ring "1.3.2"]
                 [compojure "1.3.1"]]
  :plugins [[lein-ring "0.8.13"]
            [lein-marginalia "0.8.0"]]
  :ring {:handler simplurl.server/simplurl
         :init simplurl.server/init}
  :main ^:skip-aot simplurl.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
