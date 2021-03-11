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
        [:div.content.col-md-9
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
         ];;content
        [:div.content.col-md-3
         [:div [:h4 "Blog Roll"]]
         [:ul
          (for [blog (:blog-roll @config)]
            [:li [:a {:href (:url blog) :target "_blank"} (:title blog)]])]];;blog-roll
        ]]
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
    (re-find #"^\/tags" (:uri request)) {:status 200
                                         :headers {"Content-Type" "text/html"}
                                         :body (let [tag
                                                     (nth (clojure.string/split (:uri request) #"\/") 2)]
                                                 (posts-page {:tag tag}))}
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
   (wrap-file (wrap-webjars handler) "resources")
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
       :sub-brand "Shad Gregory's Blog"
       :blog-roll [{:title "Fix the Court"
                    :url "https://fixthecourt.com/"}
                   {:title "Planet Clojure"
                    :url "http://planet.clojure.in/"}
                   {:title "Pragmatic Emacs"
                    :url "http://pragmaticemacs.com/"}]})

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

;; (defpost "Judicial Review Scorecard 1: Marbury v. Madison"
;;   (html
;;    [:div
;;     [:p "Judicial review is the idea (at least in the United States) that court has the power to veto any law, or any part of any law, at any time. It's long been my suspicion that when the supreme court used judicial review against the states, it's a mixed bag. But when it uses judicial review against Congress, it's almost always bad. To test that, I've decided to start the judicial review scorecard."]
;;     [:p [:i "Marbury v. Madison"] " is ground zero for judicial review. A lot of "
;;      [:a {:href "https://en.wikipedia.org/wiki/Marbury_v._Madison"} "ink"]
;;      " has been spilled on this decision, so I won't spend a lot of time on it except to say that this one is neutral. "]]
;;    (scorecard "Marbury v. Madison" "Judiciary Act of 1789" 0 0 1))
;;   "supreme_court" "judicial_review")

;; (defpost "Judicial Review Scorecard 2: Dred Scott v. Sandford"
;;   (html
;;    [:div
;;     [:p ]
;;     (scorecard "Dred Scott v. Sandford" "Missouri Compromise of 1820" 0 1 1)]
;;    )
;;   "supreme_court" "judicial_review")

(defpost "Orient RE-AW0004S"
  (html
   [:div
    [:p [:img {:src "/img/RE-AW0004S_New.jpg"
               :height "300"
               :alt "Orient Mechanical Classic Watch"}]]
    [:p "Very attractive blue arabic numerals on a white dial. Love the power reserve complication and the 38.7 mm size. Around $800."]])
  "watches")

(defpost "Timex Marlin Hand-Wound"
  (html
   [:div
    [:p [:img {:src "/img/TW2R47900.png"
               :height "300"
               :alt "Timex Marlin"}]]
    [:p "Is it worth $200? Not sure, but nothing else looks like it. Love the mid-century numerals and the retro size"]])
  "watches")
