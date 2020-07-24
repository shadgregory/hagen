(ns hagen.core
  (:require [ring.adapter.jetty :as jetty]
            [hiccup.page :refer [include-js include-css html5]]
            [hiccup.core :refer [html]]
            [garden.core :refer [css]]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.walk :refer [keywordize-keys]]
            [ring.util.codec :refer [form-decode]]
            [ring.middleware.file :refer [wrap-file]]
            [ring.middleware.webjars :refer [wrap-webjars]]))

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

(defn posts-page [& args]
  (let [start (Integer. (:start (first args) 1))
        tag (:tag (first args))]
    (html5
     (posts-head)
     [:body
      [:nav.navbar.navbar-expand-sm.navbar-light.bg-light
       [:div.container
        [:a.navbar-brand {:href "/"}
         [:img {:src (str "/img/" (:brand-icon @config)) :style "padding-right:2px;"}] (:brand @config)]
        [:div#navbar_collapse.collapse.navbar-collapse
         [:ul.navbar-nav.mr-auto
          [:li.nav-item.dropdown
           [:a#MenuLink.nav-link.dropdown-toggle {:href "#"
                                                  :data-toggle "dropdown"} "Tags " [:b.caret]]
           [:div.dropdown-menu {:aria-labelledby "tagsMenuLink"}
            [:a.dropdown-item {:href "/"} "All"]
            (for [tag (sort (distinct (flatten (for [row @db] (:tags (val row))))))]
              [:a.dropdown-item {:href (str "/tags/" tag)} tag])]]
          [:li.nav-item
           [:a.nav-link {:target "_blank" :href "/rss"} "RSS"]]]]]]
      [:div.container
       [:div.row
        [:div.content.col-md-12
         [:h1 "Welcome"]
         [:p (:sub-brand @config)]
         [:div
          (let [the-db (filter #(or (nil? tag) (member? tag (:tags (val %))))
                               @db)
                curr-db (for [x (range (dec start) (if (< (+ start 4) (count the-db))
                                                     (+ start 4)
                                                     (count the-db)))]
                          (nth (sort #(let [[m1 d1 y1] (str/split (:date (val %1)) #"/")
                                            [m2 d2 y2] (str/split (:date (val %2)) #"/")]
                                        (cond
                                          (not (zero? (compare y2 y1))) (compare y2 y1)
                                          (not (zero? (compare m2 m1))) (compare m2 m1)
                                          (not (zero? (compare d2 d1))) (compare d2 d1)
                                          :else 0)) the-db) x))]
            [:div
             (for [post curr-db]
               [:article
                [:a {:name (clojure.string/replace (key post) #" " "_")}]
                [:headers
                 [:h2.text-info (key post)]
                 [:p.date-and-tags
                  (:date (val post)) " :: "
                  (for [tag (:tags (val post))]
                    [:span {:style "padding-right: 4px;"}
                     [:a.text-warning {:href (str "/tags/" tag)} tag]])]
                 [:p (:body (val post))]]])
             (if (> (count the-db) 5)
               [:nav {:aria-label "..."}
                [:ul.pagination
                 (for [x (range 1 (inc (int (Math/ceil (/ (count the-db) 5)))))]
                   [:li
                    (if (= x (int (Math/ceil (/ start 5))))
                      {:class "page-item active"}
                      {:class "page-item"})
                    [:a.page-link {:href (str "/?start=" (inc (* 5 (dec x))))} x]])]])]
            );; let
          ];;div
         ]]]
      (include-js "/assets/jquery/jquery.min.js")
      (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.17.1/components/prism-clojure.js")
      (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.17.1/components/prism-lisp.min.js")
      (include-js "/assets/bootstrap/js/bootstrap.bundle.min.js")])))

(defn handler [request]
  (cond
    (= "/rss" (:uri request)) {:status 200
                               :headers {"Content-Type" "text/xml"}
                               :body (str "<?xml version='1.0' encoding='UTF-8' ?>"
                                          "<rss version='2.0'>"
                                          "<link>"
                                          (:link @config)
                                          "</link>"
                                          "<description>"
                                          (:description @config)
                                          "</description>"
                                          "<channel>"
                                          (loop [posts (sort #(let [[m1 d1 y1] (clojure.string/split (:date (val %1)) #"/")
                                                                    [m2 d2 y2] (clojure.string/split (:date (val %2)) #"/")]
                                                                (cond
                                                                  (not (zero? (compare y2 y1))) (compare y2 y1)
                                                                  (not (zero? (compare m2 m1))) (compare m2 m1)
                                                                  (not (zero? (compare d2 d1))) (compare d2 d1)
                                                                  :else 0)) @db)
                                                 result ""]
                                            (cond
                                              (empty? posts) result
                                              :else (recur (rest posts) (str result "<item>"
                                                                             "<title>"
                                                                             (key (first posts))
                                                                             "</title>"
                                                                             "<description>"
                                                                             (clojure.string/escape (:body (val (first posts)))
                                                                                                    {\< "&lt;", \> "&gt;", \& "&amp;"})
                                                                             "</description>"
                                                                             "</item>"))))
                                          "</channel>"
                                          "</rss>")}
    (re-find #"^\/tags" (:uri request)) (do
                                          {:status 200
                                           :headers {"Content-Type" "text/html"}
                                           :body (let [tag
                                                       (nth (clojure.string/split (:uri request) #"\/") 2)]
                                                   (posts-page {:tag tag}))})
    (= "/" (:uri request)) (let [args (if (not (nil? (:query-string request)))
                                        (keywordize-keys (form-decode (:query-string request))))]
                             {:status 200
                              :headers {"Content-Type" "text/html"}
                              :body (posts-page args)})
    (not (nil? (re-find #"\/tags\/" (:uri request))))
    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (posts-page {:tag (let [tags (str/split (:uri request) #"\/")]
                               (nth tags 2))})}
    :else {:status 404
           :body (html5 (posts-head)
                        [:body [:h2 "Page not found."]])}))

(defn -main [& args]
  (jetty/run-jetty
   (wrap-file(wrap-webjars handler) "public")
   {:port (:port @config)}))

(defn defpost
  ([title body]
   (let [date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date))]
     (if (nil? (get @db title))
       (do
         (swap! db assoc title {:date date :body body})
         (spit "./db.clj" @db))
       (do
         (swap! db assoc title {:date (:date (get @db title)) :body body})
         (spit "./db.clj" @db)))))
  ([title body & tags]
   (let [date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy") (new java.util.Date))]
     (if (nil? (get @db title))
       (do
         (swap! db assoc title {:date date :body body :tags (reduce conj [] tags)})
         (spit "./db.clj" @db))
       (do
         (swap! db assoc title {:date (:date (get @db title)) :body body :tags (reduce conj [] tags)})
         (spit "./db.clj" @db))))))

(defn init [config-map]
  (doseq [keyval config-map]
    (swap! config assoc (key keyval) (val keyval))))

(init {:theme "sketchy"
       :brand "Shad's Blog"
       :link "https://wsgregory.us"
       :brand-icon "chest.png"
       :description "A most wonderous blog."
       :sub-brand "Shad Gregory's Blog"})

(defpost "This is a title" "This is a body!" "history")
(defpost "Back to Hagen!" "Starting Hagen again today!" "meta")
(defpost "Keep on Keeping on!" "Need to never stop. Don't stop no." "inspiration" "meta")
(defpost "Blah Blah Blah" "Blah blah blah blah")
(defpost "Hagen is My Inspiration!" "It keeps me keeping on." "inspiration")
(defpost "Coronavirus Blues" "Stuck inside for another day. Booo!!! Booooo!" "complaining")
(defpost "Intriguing Questions" "<p>Can I?<p><p>use paragraphs?</p>" "meta")
(defpost "Hiccup Test" (html [:div
                              [:p "Hiccup Test"]
                              [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla non urna ut sapien consequat pharetra. Fusce eleifend turpis risus, eu facilisis neque eleifend vitae."]]) "meta")
;;(defpost "This is another title" [:div "This is " [:b "another "] "body"] "court")
;;(defpost "Third Title" "My third post today" "foobar" "history")
