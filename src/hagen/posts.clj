(ns hagen.posts
  (:require
   [hiccup.core :refer [html]]
   [hiccup.util :refer [escape-html]]
   [hagen.core :refer [defpost watch-post scorecard init run]]))

(defn -main [& args]
  (run args))

;; https://bootswatch.com/
(init {:theme "sketchy"
       ;;:theme "flatly"
       ;;:theme "pulse"
       ;;:theme "cosmo"
       ;;:theme "cerulean"
       ;;:theme "journal"
       ;;:theme "yeti"
       ;;:theme "zephyr"
       ;;:theme "solar"
       ;;:theme "journal"
       :brand "Shad's Blog"
       :link "https://wsgregory.us"
       :brand-icon "chest.png"
       :about "Shad Gregory's blog."
       :description "A most wonderous blog."
       :sub-brand "Shad Gregory's Blog"
       :blog-roll [{:title "Sacha Chua"
                    :url "https://sachachua.com/blog/emacs/"}
                   {:title "Kevin Drum"
                    :url "https://jabberwocking.com/"}
                   {:title "Rob MacKillop"
                    :url "https://rmclassicalguitar.com/19th-century"}
                   {:title "No-Nail Guitar"
                    :url "https://nonailguitar.com"}
                   {:title "5-4 Pod"
                    :url "https://www.fivefourpod.com/"}
                   {:title "Follow me on Mastodon"
                    :url "https://mastodon.social/@shadgregory"
                    :rel "me"}
                   {:title "Hagen: Clojure Blogging"
                    :url "https://github.com/shadgregory/hagen"}
                   {:title "Planet Clojure"
                    :url "http://planet.clojure.in/"}]})

(defpost "Rob Mackillop"
  (html
    [:p
     "Rob Mackillop is such an inspiration to plucked string players. He plays every variation of guitar, lute, and banjo that you can come up with, and he's been a great advocate for the no-nails approach. I've found out recently that he's been ill and in the hospital. So let's wish him a speedy recovery and take some time to check out his "
     [:a {:href "https://www.youtube.com/@RobMacKillop1" :target "_blank"} "youtube page"] " and his "
     [:a {:href "https://rmclassicalguitar.com/" :target "_blank"} "classical guitar"] " website."])
  {"publish?" true}
  "guitar")

(defpost "Get Those Greek Letters!"
     (html
    [:pre
     [:code.language-emacs-lisp
      "M-x set-input-methed RET TeX"]]
    [:p
     [:ul
      [:li "Now typing \\theta gives you θ!"]
      [:li "Typing \\lambda gives you λ!"]
      [:li "\\nabla gives you ∇! Ta-da!"]]])
  {"publish?" true}
  "emacs" "scheme")

(defpost "The Little Learner"
  (html "I'm a big fan of the 'little' series of books, so I was excited to get my copy of " [:a {:href "https://mitpress.mit.edu/9780262546379/the-little-learner/"} "The Little Learner"] ". And boy oh boy, it's a thick-o. It's gotta be at least three times as thick as the 'Little Schemer.' I guess " [:i "Little"]" is more of a branding thing now. It still has the same two column format and goofy cartoons, so I'm happy.")
  {"publish?" true}
  "scheme")

(defpost "Enrica Savigni on the Romantic Guitar"
  (html
   [:body
    [:p "If you are a guitarist interested in historically informed performance (like me!), then you need to check out this "
     [:a {:href "https://www.youtube.com/watch?v=ubzgkS38daA" :target "_blank"} "presentation"] " by Enrica Savigni."]])
  {"publish?" true}
  "guitar")

(defpost "Emacs is pretty good at text processing."
  (html
    [:p "One of the many great tips at this "
     [:a {:href "https://howardism.org/Technical/Emacs/eshell-present.html"}
      "article"]
     " at howardism.org is to remember to use keep-lines/flush-lines instead of grep. One of the great features of eshell is ability to redirect output to a buffer."]
    [:pre
     [:code.language-bash
      "ls | sed G > #" (escape-html "<") "mybuffer" (escape-html ">")]]
    [:p "Now you can switch to mybuffer and use M-x keep-lines to find, say, all the text files."])
  {"publish?" true}
  "emacs")

(defpost "New and Improved Method for the Guitar"
  (html
   [:p "The best guitar method was written in 1853 and is available for free on the net. You can check it out on archive.org: "
    [:a {:href "https://archive.org/details/newimprovedmeth00carc/mode/2up"} "New and improved method for the guitar"]])
  {"publish?" true} 
  "guitar")

(defpost "Emacs backup files"
  (html
   "I've gotten a little sick of the \"~\" files littering my directories. Fortunately, adding the following to your init.el will send the backup files to one place in a single directory:"
      [:pre
       [:code.language-emacs-lisp
        "(setq backup-directory-alist '((\"\" . \"~/.emacs.d/backup\")))"]])
  {"publish?" true}
  "emacs")

;; (defpost "Get the Caps"
;;   (str (html [:p "...takes a string and returns a new string containing only the capital letters."]
;;              [:p "Java"]
;;              [:pre
;;               [:code.language-java
;;                (str
;;                 "public static String getTheCaps(String str) {"
;;                 "\n"
;;                 "  return str.chars()"
;;                 "\n"
;;                 "       .filter(Character::isUpperCase)"
;;                 "\n"
;;                 "       .collect(StringBuilder::new,"
;;                 "\n"
;;                 "                StringBuilder::appendCodePoint,"
;;                 "\n"
;;                 "                StringBuilder::append).toString();"
;;                 "\n"
;;                 "}")]]
;;              [:p "Clojure"]
;;              [:pre
;;               [:code.language-clojure
;;                (str
;;                 "(defn get-the-caps [str]"
;;                 "\n"
;;                 "  (clojure.string/replace str #\"[^A-Z]\" \"\"))")]])) "java" "clojure" "code-probs")

(defpost "FizzBuzz"
  (str (html [:p "If you're going to interview, you better know FizzBuzz:"]
             [:pre
              [:code.language-clojure
               (str
                "(defn fizzbuzz [n]"
                "\n"
                "  (doseq [x (range 1 n)]"
                "\n"
                "    (cond"
                "\n"
                "      (and (zero? (mod x 5))"
                "\n"
                "           (zero? (mod x 3))) (prn \"FizzBuzz\")"
                "\n"
                "      (zero? (mod x 3)) (prn \"Fizz\")"
                "\n"
                "      (zero? (mod x 5)) (prn \"Buzz\")"
                "\n"
                "      :else (prn x))))"
                "\n"
                "(fizzbuzz 10)")]]))
  {"publish?" true
   "date" "11/22/2022 12:32"}
  "clojure")

;; (defpost "Simple PGN Mode"
;;   [:div
;;    [:p "Keyword list"]
;;    [:pre
;;     [:code.language-emacs-lisp
;;      (str "(setq pgn-keywords '(\"Annotator\"\n"
;;           "                     \"Black\"\n"
;;           "                     \"BlackElo\"\n"
;;           "                     \"Date\"\n"
;;           "                     \"ECO\"\n"
;;           "                     \"Event\"\n"
;;           "                     \"EventDate\"\n"
;;           "                     \"EventType\"\n"
;;           "                     \"FEN\"\n"
;;           "                     \"Mode\"\n"
;;           "                     \"Result\"\n"
;;           "                     \"Round\"\n"
;;           "                     \"SetUp\"\n"
;;           "                     \"Site\"\n"
;;           "                     \"Termination\"\n"
;;           "                     \"Time\"\n"
;;           "                     \"TimeControl\"\n"
;;           "                     \"White\"\n"
;;           "                     \"WhiteElo\")\n"
;;           )
;;      ]
;;     ]
;;    ]
;;   "emacs")

(defpost "Orient RE-AW0004S"
  (watch-post "Orient RE-AW0004S"
              "/img/RE-AW0004S_New.jpg"
              "Very attractive blue arabic numerals on a white dial. Love the power reserve complication and the 38.7 mm size. Around $800.")
  {"publish?" false}
  "watches")

;; (defpost "Timex Marlin Hand-Wound"
;;   (watch-post "Timex Marlin Hand-Wound"
;;               "/img/TW2R47900.png"
;;               "Is it worth $200? Not sure, but nothing else looks like it. Love the mid-century numerals and the retro size")
;;   "watches")

;; (defpost "Junghans Max Bill Chronograph"
;;   (html
;;    (watch-post "Junghans Max Bill Chronograph"
;;                "/img/junghans_chrono.webp"
;;                "The super minimal design on this chronograph is a thing of beauty. $1995."))
;;   "watches")

(defpost "Grand Seiko SBGA413"
  (watch-post "Grand Seiko SBGA413"
              "/img/SBGA413G.png"
              "The Snowflake is a work of art, and the Shunbun takes it to the next level with it's subtle pink hues. Definitely a luxury watch at $6300.")
  {"publish?" false} "watches")

;; (defpost "Seiko Presage SRQ023"
;;   (watch-post "Seiko Presage SRQ023"
;;               "/img/seiko_SRQ023.png"
;;               "I really love the look of white enamel watches. And it's a mechanical chronograph too. $2400.")
;;   "watches")

(defpost "Seiko Presage SPB161"
  (watch-post "Seiko Presage SPB161"
              "/img/seiko_SPB161.jpg"
              "Love the blue hands on white enamel, and the small seconds and power reserve complications on top of the minimalist design is super neat. $1300.")
  {"publish?" false}
  "watches")

;; (defpost "Seiko Presage SRPE41"
;;   (watch-post "Seiko Presage SRPE41"
;;               "/img/seiko_SRPE41.png"
;;               "My favorite dial pattern on the cocktail line, and I don't have a red watch. $425.")
;;   "watches")

;; (defpost "Hemel HFT20"
;;   (watch-post "Hemel HFT20"
;;               "/img/Hemel_HFT20.jpg"
;;               "This doesn't seem like it should be my sort of thing, but I like it. Maybe it's the orange (salmon?) color. $450.")
;;   "watches")

;; (defpost "Breguet 7147"
;;   (watch-post "Breguet 7147BB/29/9WU"
;;               "/img/breguet_7147.webp"
;;               "Another enamel watch. Beautiful hand painted dial with a subtle small seconds. Only $21K."
;;               )
;;   "watches")

;; (defpost "Junghans Max Bill Handwinding"
;;   (watch-post "Junghans Max Bill Handwinding"
;;               "/img/junghans_maxbill_handwinding.png"
;;               "I really do need a small watch, and the max bill hand-winding at 34mm would do the trick. Only $795.")
;;   "watches")

;; (defpost "Junghans Meister Calendar"
;;   (watch-post "Junghans Meister Calendar"
;;               "/img/meister_calendar.jpg"
;;               "Moon phase watch with that cool rounded Junghans look. $2245.")
;;   "watches")


(defpost "Lifetime Appointments"
  (html
   [:div
    [:blockquote.blockquote
     [:p
      "The Judges, both of the supreme and inferior Courts, shall hold their Offices during good Behaviour..."]
     [:footer.blockquote-footer "Tha Constitution, Article III"]]
    [:p "And that short cryptic sentence is why we're stuck with lifetime appointments on the supreme court. But if you read closely, it doesn't actually say that justices can stay on the court until they drop dead. You could just as well argue that it means that justices are free to fulfill their terms of office as long as they behave themselves. You could argue a lot of things. It really is pretty vague."]
    [:p "And that's the thing about the Constitution's vagueness. There's a certain power there for those who are brave enough to exploit it. I remember when Antonin Scalia died. There was no way a Republican Congress would allow President Obama to name his replacement. What was crazy, though, was that there was never a vote. I mean the constitution says, " [:q "Advice and consent"] ", right? But it didn't happen. Merrick Garland never got a vote. Mitch McConnell clearly violated the Constitution. But then, so what? The Constitution doesn't tell us what to do when someone violates the Constitution. So we did nothing, and Neil Gorsuch, not Merrick Garland, replaced Scalia."]
    [:p "And this helped me realize that the Constitution is not a recipe. It's not a mathematical proof. The Constitution is just what we all agree that it means. Nothing more. John Adams wrote about a " [:q "A government of laws, and not of men..."] ", but really, let's be honest, that's just a lot of high-minded nonsense. Our government, like all governments, is a collection of human beings constantly negotiating over the boundaries of what is possible."]
    [:p "So if you hear someone say that term limits requires a constitutional amendment, please take the time to push back and ask them to show a little courage and to use a little creativity in reading the text of the constitution."]])
  {"publish?" false}
  "supreme_court")

(defpost "Interlude: The Impeachment of Samuel Chase"
  (html
   [:div
    [:p "By almost any measure, " [:i "Marbury"] " was a triumph. Marshall had split the baby, giving a partial win to both sides, as well as empowering the judicial branch in the process. Marshall would serve as the chief justice until his death in 1835. How is it that " [:i "Marbury"] " would be the first and last time that Marshall's court would wield the judicial review power?"]
    [:p "One answer might lie in what happened next. Roughly a year after " [:i "Marbury"] " the House voted to impeach Samuel Chase."]
    [:p "Over the years, judges have been impeached for being crazy and for being corrupt, but Chase is the only judge impeached for " [:q "the low purpose of an electioneering partizan."] " Chase was an ardent Federalist. He wrote opinion pieces denouncing Thomas Jefferson. He missed the entire August 1800 term because he was too busy campaigning for President Adams."]
    [:p "We can't be sure if " [:i "Marbury"] " was the impetus for the impeachment, but Marshall certainly thought so:"]
    [:blockquote.blockquote
     [:p "I think the modern doctrine of impeachment should yield to an appellate jurisdiction in the
   legislature. A reversal of those legal opinions considered unsound by the legislature would certainly
   better comport with the mildness or our character than a removal of the judge who has rendered them
   unknowing of his fault."]
     [:footer.blockquote-footer "John Marshall in a letter to Samuel Chase"]]
    [:p "Amazing. In the wake of his greatest triumph, Marshall was rattled enough by the impeachment to consider total capitulation on judicial review."]
    [:p "The impeachment of Samuel Chase is probably a pretty underrated episode in American history. Marshall's court would never again strike down a congressional statute, Congress would never again impeach a Supreme Court justice, and going forward, the court would hide it's grotesque partisanship behind a thick veneer of Constitutional goobledygook."]])
  {"publish?" false}
  "supreme_court")

(defpost "Judicial Review Scorecard 1: Marbury v. Madison"
  (html
   [:div
    [:p "Judicial review is the idea (at least in the United States) that court has the power to veto any law, or any part of any law, at any time. It's long been my suspicion that when the supreme court used judicial review against the states, it's a mixed bag. But when it uses judicial review against Congress, it's almost always bad. To test that, I've decided to start the judicial review scorecard."]
    [:p [:i "Marbury v. Madison"] " is ground zero for judicial review. A lot of "
     [:a {:href "https://en.wikipedia.org/wiki/Marbury_v._Madison"} "ink"]
     " has been spilled on this decision, so I'll try not to spend too much time on it. Basically, the outgoing Federalists tried to pack the courts, but they were a little slow in delivering the commissions. The incoming Jefferson administration voided the undelivered commissions. William Marbury filed suit and the case made it to the Supreme Court in Feburary 1803."]]
   [:p "The chief justice at that time was John Marshall, a Federalist, and the case left him in quite a pickle. If the court ruled in favor of the Democratic-Republican administration, it would infuriate his fellow Federalists. On the other hand, if the court ordered the administration to deliver the commissions, there was a real danger that Jefferson would simply ignore the order, thereby exposing the court as a powerless joke."]
   [:p "The cynical reading of " [:i "Marbury v. Madison"] " is that Marshall found himself in a political quandrey, he needed an out, and he found one by declaring the Judiciary Act of 1789 unconstitutional. The cynical reading can be reinforced when you learn that Marshall had pulled this trick before. Marshall was one of the members of the Virginia council of state:"]
   [:blockquote.blockquote
    [:p
     "...Virginia's governor, Benjamin Harrison, has asked the council for an opinion as to whether, under a recently passed law, a particular magistrate could be removed for alleged misdeeds while on the bench..."]
    [:p "Judges tended to be from the gentry, and removing one for malfeasance promised to be controversial. So, rather than ruling on the behavior of the judge, Marshall instead persuaded the council to look into whether the law violated Virginia's constitution."]
    [:footer.blockquote-footer "Lawrence Goldstone " [:i "The Activist"]]]
   [:p [:i "Marbury"] " would eventually lead to a lot of ugliness, but for now, I'm calling this neutral."]
   (scorecard "Marbury v. Madison" "Judiciary Act of 1789" 0 0 1))
  {"publish?" false}
  "supreme_court" "judicial_review")

;; (defpost "Judicial Review Scorecard 2: Dred Scott v. Sandford"
;;   (html
;;    [:div
;;     [:p "The court has made a lot of bad rulings in its history (and I do mean " [:i "a lot"] "). The court has struck down child labor laws, undermined Reconstruction, tried to blow up the New Deal, and basically made bribery legal. In spite of this long list of ignominious decisions, there is little doubt as to what the court's worst decisionis. " [:i "Dred Scott "] " is a terrible decision on so many levels. Of course, there's the inhumane idea that black people are not and can never be citizens of the United States. There's the remarkable arogance in the idea that the court could settle the slavery question. And then there's the sight of five lawyers bumbling into a hornet's nest of race, politics, and economics with little understanding of what they were unleashing. It's widely assumed that the 1857 Panic was set off by the decision. The next time some moron (*cough* Stephen Breyer *cough*) tells you how great the court is, be sure to throw " [:i "Dred Scott"] " in their face."]
;;     (scorecard "Dred Scott v. Sandford" "Missouri Compromise of 1820" 0 1 1)]
;;    )
;;   "supreme_court" "judicial_review"
;;   )

;; (defpost "Judicial Review Scorecard 3: Ex parte Garland"
;;   (html
;;    [:div
;;     [:p "Abraham Lincoln died on April 15, 1865, and Andew Johnson became president on the same day. Johnson promptly went pardon mad and by June 5 had issued 12,652 pardons. With his pardons, Johnson had welcomed the most radical white supremicists back into society and dealt reconstruction a fatal blow before it could even get off the ground."]
;;     [:p "One bulwark against the destructive force of the Johnson pardons was the " [:i "Congressional Act of January 24, 1865"] " (sorry, I don't know if it had a more memorable name), a law that effectively disbarred former members of the Confederate government. Preventing a bunch of treasonous white supremicists from taking over Southern courts might strike you as a good idea, but the court didn't think so and on January 14, 1867 declared the law unconstitutional."]
;;     [:p "Of course this decision was terrible. In addition to the damage it caused to Reconstruction, it also increased the power of the Presidential pardon to absurd proportions, paving the way for goodies like the Nixon pardon."]
;;     (scorecard "Ex parte Garland" "Congressional Act of January 24, 1865" 0 2 1)])
;;   "supreme_court" "judicial_review")

(defpost "House Votes to Remove Taney Bust"
  (html
   [:div
    [:p [:a {:href "https://www.cnn.com/2021/06/29/politics/house-vote-confederate-statues/index.html"} "It's"] " about damned time."]])
  {"publish?" true
   "date" "06/30/2021 00:00"}
  "supreme_court")
