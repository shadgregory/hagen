(ns hagen.core
  (:require [ring.adapter.jetty :as jetty]
            [hiccup.page :refer [include-js include-css html5]]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(def posts-vec (atom []))
(def db (atom (eval (read-string (slurp "./db.clj")))))
(def config (atom {:port 3003
                   :brand "Generic Blog"}))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (html5
          [:head
           (case (:theme @config)
             "yeti" (include-css "/assets/bootswatch-yeti/bootstrap.min.css")
             "flatly" (include-css "/assets/bootswatch-flatly/bootstrap.min.css")
             "pulse" (include-css "/assets/bootswatch-pulse/bootstrap.min.css")
             "spacelab" (include-css "/assets/bootswatch-spacelab/bootstrap.min.css")
             "cosmo" (include-css "/assets/bootswatch-cosmo/bootstrap.min.css")
             "minty" (include-css "/assets/bootswatch-minty/bootstrap.min.css")
             "sketchy" (include-css "/assets/bootswatch-sketchy/bootstrap.min.css")
             "solar" (include-css "/assets/bootswatch-solar/bootstrap.min.css")
             "united" (include-css "/assets/bootswatch-united/bootstrap.min.css")
             (include-css "/assets/bootswatch-litera/bootstrap.min.css"))]
          [:body
           [:nav.navbar.navbar-expand-sm.navbar-dark.bg-dark
            [:div.container
             [:a.navbar-brand {:href "#"} (:brand @config)]]]
           [:div.container
            [:div.row
             [:div.content.col-md-12
              [:h1 "Welcome"]
              [:p (:sub-brand @config)]
              [:div
               (for [post @posts-vec]
                 post)]]]]])})

(defn -main [& args]
  (jetty/run-jetty (wrap-webjars handler) {:port (:port @config)}))

(defn start []
  (jetty/run-jetty (wrap-webjars handler) {:port (:port @config)}))

(defmacro defpost [title body]
  `(let [date# (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date))]
     (if (nil? (get @db ~title))
       (do
         (swap! db assoc ~title {:date date# :body ~body})
         (spit "./db.clj" @db)))
     (swap! posts-vec conj
            [:article
             [:header
              [:h2.text-info ~title]
              [:p.date-and-tags
               (:date (get @db ~title))]]
             [:p
              (:body (get @db ~title))]])))

(defn init [config-map]
  (doseq [keyval config-map]
    (swap! config assoc (key keyval) (val keyval))))

(init {:theme "sketchy"
       :brand "Shad's Blog"
       :sub-brand "Shad Gregory's Blog"})

(defpost "This is a title" "This is a body!")
(defpost "This is another title" "This is another body!")
(defpost "Third Title" "My third post today")
