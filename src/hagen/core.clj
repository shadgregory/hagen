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
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.webjars :refer [wrap-webjars]])
  (:import org.apache.logging.log4j.Logger
           org.apache.logging.log4j.LogManager))

(def db (atom
         (if (.exists (io/file "./db.clj"))
           (eval (read-string (slurp "./db.clj")))
           {})))

(def config (atom {:port 3003
                   :brand "Generic Blog"}))

(def log (. LogManager getLogger "main"))

(defn member? [a v]
  (loop [v v]
    (cond
      (empty? v) false
      (= a (first v)) true
      :else (recur (rest v)))))

(defn posts-head []
  [:head
   [:meta {:charset "utf-8"}]
   [:title (:brand @config)]
   (case (:theme @config)
     "cerulean" (include-css "/assets/bootswatch/dist/cerulean/bootstrap.min.css")
     "cosmo" (include-css "/assets/bootswatch/dist/cosmo/bootstrap.min.css")
     "cyborg" (include-css "/assets/bootswatch/dist/cyborg/bootstrap.min.css")
     "darkly" (include-css "/assets/bootswatch/dist/darkly/bootstrap.min.css")
     "flatly" (include-css "/assets/bootswatch/dist/flatly/bootstrap.min.css")
     "journal" (include-css "/assets/bootswatch/dist/journal/bootstrap.min.css")
     "litera" (include-css "/assets/bootswatch/dist/litera/bootstrap.min.css")
     "lumen" (include-css "/assets/bootswatch/dist/lumen/bootstrap.min.css")
     "lux" (include-css "/assets/bootswatch/dist/lux/bootstrap.min.css")
     "materia" (include-css "/assets/bootswatch/dist/materia/bootstrap.min.css")
     "minty" (include-css "/assets/bootswatch/dist/minty/bootstrap.min.css")
     "morph" (include-css "/assets/bootswatch/dist/morph/bootstrap.min.css")
     "pulse" (include-css "/assets/bootswatch/dist/pulse/bootstrap.min.css")
     "quartz" (include-css "/assets/bootswatch/dist/quartz/bootstrap.min.css")
     "sandstone" (include-css "/assets/bootswatch/dist/sandstone/bootstrap.min.css")
     "simplex" (include-css "/assets/bootswatch/dist/simplex/bootstrap.min.css")
     "sketchy" (include-css "/assets/bootswatch/dist/sketchy/bootstrap.min.css")
     "slate" (include-css "/assets/bootswatch/dist/slate/bootstrap.min.css")
     "solar" (include-css "/assets/bootswatch/dist/solar/bootstrap.min.css")
     "spacelab" (include-css "/assets/bootswatch/dist/spacelab/bootstrap.min.css")
     "superhero" (include-css "/assets/bootswatch/dist/superhero/bootstrap.min.css")
     "united" (include-css "/assets/bootswatch/dist/united/bootstrap.min.css")
     "vapor" (include-css "/assets/bootswatch/dist/vapor/bootstrap.min.css")
     "yeti" (include-css "/assets/bootswatch/dist/yeti/bootstrap.min.css")
     "zephyr" (include-css "/assets/bootswatch/dist/zephyr/bootstrap.min.css")
     (include-css "/assets/bootswatch-litera/bootstrap.min.css"))
   (include-css "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/styles/color-brewer.min.css")
   [:link {:rel "icon" :type "image/x-icon" :href "/img/favicon.ico"}]
   (include-css "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/themes/prism.min.css")

   (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/prism.min.js")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-clojure.min.js")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-lisp.min.js")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-java.min.js")
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/prism/1.29.0/components/prism-bash.min.js")
   [:style (css [:.clojure-body {:color "#000000"
                                 :background-color "#ffffff"}]
                [:.comment {:color "#8c8c8c"
                            :font-style "italic"}]
                [:.comment-delimiter {:color "#8c8c8c"
                                      :font-style "italic"}]
                [:.builtin {:color "#228b22"}]
                [:.function-name {:color "#6a5acd"}]
                [:.type {:color "#36648b"}]
                [:.variable-name {:color "#b8860b"}]
                [:.keyword {:color "#00008b"}])]
   (include-js "https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.2.0/highlight.min.js")])

(defn navbar []
  [:nav.navbar.navbar-expand-lg.navbar-light.bg-light
   [:div.container
    [:a.navbar-brand {:href "/"}
     [:img {:src (str "/img/" (:brand-icon @config)) :style "padding-right:2px;"}] (:brand @config)]
    [:div#navbar_collapse.collapse.navbar-collapse
     [:ul.navbar-nav.mr-auto
      [:li.nav-item.dropdown
       [:a#MenuLink.nav-link.dropdown-toggle {:href "#"
                                              :data-toggle "dropdown"
                                              :data-bs-toggle "dropdown"
                                              :aria-haspopup "true"
                                              :aria-expanded "false"} "Tags " [:b.caret]]
       [:div.dropdown-menu {:aria-labelledby "tagsMenuLink"}
        [:a.dropdown-item {:href "/"} "All"]
        (for [tag (sort (distinct (flatten (for [row @db] (:tags (val row))))))]
          [:a.dropdown-item {:href (str "/tags/" tag)} tag])]]
      [:li.nav-item
       [:a.nav-link {:href "/about"} "About"]]
      [:li.nav-item
       [:a.nav-link {:target "_blank" :href "/rss"} "RSS"]]]]]])

(defn posts-page [& args]
  (let [start (Integer. (:start (first args) 1))
        tag (:tag (first args))]
    (html5
     (posts-head)
     [:body
      [:div.container
       (navbar)
       [:div.row
        [:div.content.col-md-9
         [:h1 "Welcome"]
         [:p (:sub-brand @config)]
         [:div
          (let [the-db (filter #(or (nil? tag) (member? tag (:tags (val %))))
                               @db)
                curr-db (for [x (range (dec start) (if (< (+ start 4) (count the-db))
                                                     (+ start 4)
                                                     (count the-db)))]
                          (nth (sort #(let [[date1 t1] (str/split (:date (val %1)) #" ")
                                            [date2 t2] (str/split (:date (val %2)) #" ")
                                            [m1 d1 y1] (str/split date1 #"/")
                                            [m2 d2 y2] (str/split date2 #"/")]
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
                    [:a.page-link {:href
                                   (if (nil? tag)
                                     (str "/?start=" (inc (* 5 (dec x))))
                                     (str "/tags/" tag "?start=" (inc (* 5 (dec x)))))}
                     x]])]])])]]
        [:div.content.col-md-3.pt-2
         [:div.card.border-info
          [:div.card-header
           [:h4 "Blog Roll"]]
          [:div.card-body
           [:ul
            (for [blog (:blog-roll @config)]
              [:li [:a {:href (:url blog) :target "_blank"} (:title blog)]])]]]];;blog-roll
        ]]
      (include-js "/assets/jquery/jquery.min.js")
      (include-js "/assets/bootstrap/dist/js/bootstrap.bundle.min.js")])))

(defn handler [request]
  (. log info (str "REQUEST : "  (:uri request)))
  (cond
    (= "/about" (:uri request)) {:status 200
                                 :headers {"Content-Type" "text/html"}
                                 :body (html5
                                        (posts-head)
                                        [:body
                                         [:div.container
                                          (navbar)
                                          [:div.row
                                           [:div#content.col-md-12
                                            [:h1.text-success "About"]
                                            [:p (:about @config)]]
                                           [:footer
                                            [:hr]
                                            [:p
                                             "Site generated by " [:a {:target "_blank" :href "https://github.com/shadgregory/hagen"} "hagen"] "."]]]]
                                         [:div
                                          (include-js "/assets/jquery/jquery.min.js")
                                          (include-js "/assets/bootstrap/dist/js/bootstrap.bundle.min.js")]])}
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
    (re-find #"^\/tags\/\w+" (:uri request)) (let [args (if (not (nil? (:query-string request)))
                                                          (keywordize-keys (form-decode (:query-string request))))]
                                               {:status 200
                                                :headers {"Content-Type" "text/html"}
                                                :body (let [tag
                                                            (nth (clojure.string/split (:uri request) #"\/") 2)]
                                                        (posts-page {:tag tag
                                                                     :start (:start args 1)}))})
    (or (= "/" (:uri request))
        (= "/tags" (:uri request))
        (= "/tags/" (:uri request))) (let [args (if (not (nil? (:query-string request)))
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

(defn run [& args]
  (. log info "START In main...")
  (jetty/run-jetty
   (wrap-reload (wrap-file (wrap-webjars handler) "resources"))
   {:port (:port @config)}))

(defn defpost
  ""
  ([title body publish?]
   (let [date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy HH:mm") (new java.util.Date))]
     (cond
       (not publish?) (do
                        (swap! db dissoc title)
                        (spit "./db.clj" @db))
       (nil? (get @db title)) (do
                                (swap! db assoc title {:date date :body body})
                                (spit "./db.clj" @db))
       :else
       (do
         (swap! db assoc title {:date (:date (get @db title)) :body body})
         (spit "./db.clj" @db)))))
  ([title body publish? & tags]
   (let [date (.format (java.text.SimpleDateFormat. "MM/dd/yyyy HH:mm") (new java.util.Date))]
     (cond
       (not publish?) (do
                        (swap! db dissoc title)
                        (spit "./db.clj" @db))
       (nil? (get @db title)) (do
                                (swap! db assoc title {:date date :body body :tags (reduce conj [] tags)})
                                (spit "./db.clj" @db))
       :else
       (do
         (swap! db assoc title {:date (:date (get @db title)) :body body :tags (reduce conj [] tags)})
         (spit "./db.clj" @db))))))

(defn init [config-map]
  (doseq [keyval config-map]
    (swap! config assoc (key keyval) (val keyval))))

(defn scorecard [title overturned good bad neutral]
  [:div
   [:div.card.text-white.bg-info.mb-3
    [:div.card-header "Judicial Review Scorecard"]
    [:div.card-body
     [:h4.card-title title]
     [:p.card-text
      [:p [:h5 "Overturned: " overturned]]
      [:p (str "Good: " good)]
      [:p (str "Bad: " bad)]
      [:p (str "Neutral: " neutral) ]]]]])

(defn watch-post [title url content]
  (html
   [:div.row
    [:div.col-5
     [:img {:src url
            :height "300"
            :alt title}]]
    [:div.col-5
     [:div.card.border-success.mb-3
      [:div.card-body
       content]]]]))
