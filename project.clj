(defproject hagen "0.1.0"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "LGPL 3.0"
            :url "https://www.gnu.org/licenses/lgpl-3.0.en.html"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [hiccup "1.0.5"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.0"]
                 [garden "1.3.9"]
                 [nrepl "0.6.0"]
                 [org.slf4j/slf4j-simple "1.7.21"]
                 [org.webjars/bootstrap "4.2.1"]
                 [org.webjars/jquery "3.4.1"]
                 [org.webjars/bootswatch-flatly "4.2.1"]
                 [org.webjars/bootswatch-united "4.2.1"]
                 [org.webjars/bootswatch-litera "4.2.1"]
                 [org.webjars/bootswatch-yeti "4.2.1"]
                 [org.webjars/bootswatch-cosmo "4.2.1"]
                 [org.webjars/bootswatch-solar "4.2.1"]
                 [org.webjars/bootswatch-minty "4.2.1"]
                 [org.webjars/bootswatch-sketchy "4.2.1"]
                 [org.webjars/bootswatch-pulse "4.2.1"]
                 [org.webjars/bootswatch-spacelab "4.2.1"]
                 [ring/ring-jetty-adapter "1.8.0"]]
  :main ^:skip-aot hagen.core
  :plugins [[lein-ring "0.12.5"]]
  :resource-paths ["resources"]
  :repl-options {:init-ns hagen.core})
