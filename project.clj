(defproject hagen "0.1.0"
  :description "Blogging Software"
  :url "https://wsgregory.us"
  :license {:name "LGPL 3.0"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [hiccup "1.0.5"]
                 [ring-webjars "0.2.0"]
                 [ring/ring "1.9.5"]
                 [garden "1.3.10"]
                 [nrepl "0.9.0"]
                 [org.slf4j/slf4j-simple "1.7.36"]
                 [org.webjars.npm/bootstrap "5.1.3"]
                 [org.webjars/jquery "3.6.0"]
                 [org.webjars.npm/bootswatch "5.1.3"]
                 [ring/ring-jetty-adapter "1.9.5"]]
  :main ^:skip-aot hagen.posts
  :plugins [[lein-ring "0.12.5"]
            [hiccup-bridge "1.0.1"]
            [lein-kibit "0.1.8"]
            [lein-ancient "0.7.0"]]
  :resource-paths ["resources"]
  :repl-options {:init-ns hagen.core})
