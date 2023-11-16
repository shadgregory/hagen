(defproject hagen "0.1.0"
  :description "Blogging Software"
  :url "https://wsgregory.us"
  :license {:name "LGPL 3.0"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [hiccup "1.0.5"]
                 [ring-webjars "0.2.0"]
                 [ring/ring "1.10.0"]
                 [garden "1.3.10"]
                 [nrepl "1.1.0"]
                 [org.slf4j/slf4j-simple "2.0.9"]
                 [org.webjars.npm/bootstrap "5.3.2"]
                 [org.webjars/jquery "3.7.1"]
                 [org.webjars.npm/bootswatch "5.3.2"]
                 [ring/ring-jetty-adapter "1.10.0"]]
  :main ^:skip-aot hagen.posts
  :plugins [[lein-ring "0.12.5"]
            [hiccup-bridge "1.0.1"]
            [lein-kibit "0.1.8"]
            [lein-ancient "0.7.0"]]
  :resource-paths ["resources"]
  :repl-options {:init-ns hagen.core})
