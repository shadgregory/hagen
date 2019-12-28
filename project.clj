(defproject hagen "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [hiccup "1.0.5"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.8.0"]
                 [garden "1.3.9"]
                 [org.slf4j/slf4j-simple "1.7.21"]
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
  :repl-options {:init-ns hagen.core})
