(ns hagen.core
  (:require [ring.adapter.jetty :as jetty]
            [hiccup.page :refer [include-js include-css html5]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [ring.middleware.webjars :refer [wrap-webjars]]))

(def posts-vec (atom []))
(def db (atom
         (if (.exists (io/file "./db.clj"))
           (eval (read-string (slurp "./db.clj")))
           {})))
(def config (atom {:port 3003
                   :brand "Generic Blog"}))

(defn member? [a v]
  (loop [v v]
    (cond
      (empty? v) false
      (= a (first v)) true
      :else (recur (rest v)))))

(defn posts-head []
  [:head
   [:meta {:charset "utf-8"}]
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
     (include-css "/assets/bootswatch-litera/bootstrap.min.css"))])

(defn posts-page [& current-tag]
  (html5
   (posts-head)
   [:body
    [:nav.navbar.navbar-expand-sm.navbar-dark.bg-dark
     [:div.container
      [:a.navbar-brand {:href "/"} (:brand @config)]
      [:div#navbar_collapse.collapse.navbar-collapse
       [:ul.navbar-nav.mr-auto
        [:li.nav-item.dropdown
         [:a#MenuLink.nav-link.dropdown-toggle {:href "#"
                                                :data-toggle "dropdown"} "Tags " [:b.caret]]
         [:div.dropdown-menu {:aria-labelledby "tagsMenuLink"}
          (for [tag (sort (distinct (flatten (for [row @db] (:tags (val row))))))]
            [:a.dropdown-item {:href (str "/tags/" tag)} tag])]]
        [:li.nav-item
         [:a.nav-link {:href "/rss"} "RSS"]]]]]]
    [:div.container
     [:div.row
      [:div.content.col-md-12
       [:h1 "Welcome"]
       [:p (:sub-brand @config)]
       [:div
        (for [post (sort #(let [[m1 d1 y1] (clojure.string/split (:date (val %1)) #"/")
                                [m2 d2 y2] (clojure.string/split (:date (val %2)) #"/")]
                            (cond
                              (not (zero? (compare y2 y1))) (compare y2 y1)
                              (not (zero? (compare m2 m1))) (compare m2 m1)
                              (not (zero? (compare d2 d1))) (compare d2 d1)
                              :else 0)) @db)]
          (if (or (nil? current-tag) (member? (first current-tag) (:tags (val post))))
            [:article
             [:a {:name (clojure.string/replace (key post) #" " "_")}]
             [:headers
              [:h2.text-info (key post)]
              [:p.date-and-tags
               (:date (val post)) " :: "
               (for [tag (:tags (val post))]
                 [:span {:style "padding-right: 4px;"}
                  [:a {:href (str "/tags/" tag)} tag]])]
              [:p (:body (val post))]]]))]]]]
    (include-js "/assets/jquery/jquery.min.js")
    (include-js "/assets/bootstrap/js/bootstrap.bundle.min.js")]))

(defn handler [request]
  (let [tags (str/split (:uri request) #"\/")]
    (prn (second tags)))
  (cond
    (not (nil? (re-find #"\/tags\/" (:uri request))))
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (posts-page (let [tags (str/split (:uri request) #"\/")]
                         (nth tags 2)))}
    (= "/" (:uri request)) {:status 200
                            :headers {"Content-Type" "text/html"}
                            :body (posts-page)}
    :else {:status 404
           :body (html5 (posts-head)
                        [:body [:h2 "Page not found."]])}))

(defn -main [& args]
  (jetty/run-jetty (wrap-webjars handler) {:port (:port @config)}))

(defn start []
  (jetty/run-jetty (wrap-webjars handler) {:port (:port @config)}))

(defmacro defpost
  ([title body]
   `(let [date# (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date))]
      (if (nil? (get @db ~title))
        (do
          (swap! db assoc ~title {:date date# :body ~body})
          (spit "./db.clj" @db)))))
  ([title body & tags]
   `(let [date# (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date))]
      (if (nil? (get @db ~title))
        (do
          (swap! db assoc ~title {:date date# :body ~body :tags [~@tags]})
          (spit "./db.clj" @db))))))

(defn init [config-map]
  (doseq [keyval config-map]
    (swap! config assoc (key keyval) (val keyval))))

(init {:theme "sketchy"
       :brand "Shad's Blog"
       :sub-brand "Shad Gregory's Blog"})

(defpost "This is a title" "This is a body!" "history")
(defpost "This is another title" [:div "This is " [:b "another "] "body"] "court")
(defpost "Third Title" "My third post today" "foobar" "history")
