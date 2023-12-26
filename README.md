# hagen
Clojure Blogging Software

Hagen is something I wrote for my personal site, and it's very tailored to my needs.

```clojure
(init {:theme "flatly" ;;Bootswatch Theme
       :brand "My Blog"
       :link "https://example.com"
       :brand-icon "brand.png"
       :about "A blog about stuff."
       :description "A most wonderous blog."
       :sub-brand "It's about the stuff!"
       :blog-roll [{:title "CNN"
                    :url "https://www.cnn.com/"}
                   {:title "Google"
                    :url "https://www.google.com/"}]})
(defpost "My Post"
  (str (html [:p "I had a very good day today. How about you?"]) "daily-post")
```
