(defproject hagen "0.1.0"
  :description "Blogging Software"
  :url "https://wsgregory.us"
  :license {:name "LGPL 3.0"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.12.0"]
                 [hiccup "1.0.5"]
                 [ring-webjars "0.3.0"]
                 [ring/ring "1.13.0"]
                 [garden "1.3.10"]
                 [nrepl "1.3.0"]
                 [org.slf4j/slf4j-simple "2.0.16"]
                 [org.apache.logging.log4j/log4j-api "2.22.0"]
                 [org.apache.logging.log4j/log4j-core "2.22.0"]
                 [clj-log4j2/clj-log4j2 "0.4.0"]
                 [org.webjars.npm/bootstrap "5.3.3"]
                 [org.webjars/jquery "3.7.1"]
                 [org.webjars.npm/bootswatch "5.3.3"]
                 [ring-logger "1.1.1"]
                 [ring/ring-jetty-adapter "1.13.0"]]
  :main ^:skip-aot hagen.posts
  :jvm-opts ["-Dclojure.tools.logging.factory=clojure.tools.logging.impl/log4j2-factory"]
  :plugins [[lein-ring "0.12.5"]
            [hiccup-bridge "1.0.1"]
            [lein-kibit "0.1.8"]
            [lein-ancient "0.7.0"]]
  :resource-paths ["resources"]
  :repl-options {:init-ns hagen.core})
