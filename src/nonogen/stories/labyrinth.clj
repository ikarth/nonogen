(ns nonogen.stories.labyrinth
  (:require [clojure.zip :as zip]
            [clojure.pprint]
            [nonogen.random]
            [nonogen.stories.storyon]
            ))

;;;
;;; Labyrinth is about exploring data structures and describing it as a character
;;; exploring a maze.
;;;


;"She entered..."
;"There she saw..."
;"She beheld..."

;[["a colonnade running between " "a bridge over "]]

(defn recursively-convert-to-vectors [tree]
  (loop [item tree]
    (cond
     (list? item) (recur (into [] item))
     (map? item) (recur (into [] item))
     (vector? item) (into [] (map recursively-convert-to-vectors item))
     (coll? item) (recur (into [] item))
     (seq? item) (recur (into [] item))
     :else item
  )))

(recursively-convert-to-vectors {:a 1 :b [:a :b :c [:d {:e 4 :f 5 :g {:h 8}}]]})

(def room-description
  '(["a "]
        ["rough " "luxurious " "wide and low " "cramped and narrow " "high " "marble " "brick-walled " "marble-floored " "Churrigueresque " "rococo " "Baroque " "neoclassic " "art deco " "primitive " "archaic " "ominous " "twilit " "shadowy " "looming "]
        ["peristyle" "tetrasoon" "terrace" "liwan" "kiva" "fogou" "hall of mirrors" "hall of doors" "arborium" "hedge maze" "atrium" "tablinum" "cavaedium" "portico" "sudatorium" "tepidarium" "triclinium" "사랑방" "atelier" "antechamber" "anatomical theatre" "almonry" "library" "equatorial room" "darbazi" "picture gallery" "cyzicene hall" "lumber room" "twilit solar" "still room" "spicery" "cryptoporticus" "rotunda" "colonnade"]
        [", "]
        ["dominated by " "tastefully offset by " "decorated with " "accented by " "watched over by " "containing " "that had " "within which was found "]
        ["divans lining the perimeter" "a fallen column" "a koi pond" "a moasic" "a trompe-l'oeil fresco" "a lararium" "a fountain" "a fireplace" "moki steps" "a sipapu" "an obelisk" "a gargoyle" "a quatrefoil inscribed in the ground" "a quatrefoil carved into the wall" "a large fresco of a garden with two paths dividing" "a beautiful fresco" "a labyrinth pattern inscribed on the floor" "a parquet floor" "a curved staircase" "xoanon" "an empty cartouche" "a cartouche with a mirror inside" "a gilt-framed mirror" "a stone-framed mirror" "a glass-framed mirror" "a wood-framed mirror" "a crumbling mound of earth" "a fire in a low basin" "a great many columns" "a glass chandelier" "a false door" "an abat-son" "a monolith" "a standing stone inlayed with gold and" "a semi-dome" "an exedra" "an alcove" "a pair of komaninu" "many solomonic columns"]
        [([" "] ["framed by a pattern of " "with a design of " "which was lined with a repeated pattern of "]["acanthus" "arabseque" "egg-and-dart" "three hares" "a dizzying spiral pattern" "palmettes" "taijitu" "complex interlacing" "winding knots" "chevrons" "buta motifs" "guilloché" "blue stones" "red gems" "pearl inlay" "wooden carvings" "imbrication" "carved runes" "scratched markings"]) ""]
        [". "]))

(defn describe-room
  ([node]
   (describe-room node "" room-description))
  ([node string]
   (describe-room node string room-description))
  ([node string room-desc]
   (let [seed (hash node)]
     (apply str
      [(apply str (flatten (nonogen.random/variation
        room-desc seed)))
       string]))))

;(def test-data-structure   [[:one [:two :three] {:four 4} {:five [:nine [:ten []]] :six [:seven :eight]}]])

(defn add-zip [story z]
  (assoc story :zip z))

(defn display-current-node [z]
  (describe-room (first z)))

(defn drunkards-walk-zipper
  "Given a zipper, pick a random direction and walk in it."
  ([z] (drunkards-walk-zipper z nil))
  ([z seed]
   (if (= (second z) :end)
     z
     (loop [acts (into [] (nonogen.random/shuffle-randomly [zip/up zip/left zip/right zip/down zip/next zip/next zip/next zip/next zip/next zip/next] seed))] ;[zip/up zip/left zip/right zip/down zip/next zip/next zip/next zip/next zip/next zip/next]
       (if (empty? acts)
         acts
         (let [result ((peek acts) z)]
        (if (not (nil? result))
          result
          (recur (pop acts)))))))))


(nth
 (iterate zip/next (zip/vector-zip [[][[][[[]]]][[[]]]]))
 288
)
;(->
; test-data-structure
; clojure.zip/vector-zip
; zip/down
; drunkards-walk-zipper
; drunkards-walk-zipper
; drunkards-walk-zipper
; drunkards-walk-zipper
; drunkards-walk-zipper
;  )

;(zip/vector-zip test-data-structure)
;((peek (into [] (nonogen.random/shuffle-randomly [zip/down] nil)))
; (zip/vector-zip test-data-structure))
(defn enter-labyrinth [story]
  (add-zip story (zip/zipper coll?
                             seq
                             (fn [node children] (with-meta children (meta node)))
                             (:state story))))

(defn enter-labyrinth [story]
  ;(clojure.pprint/pprint (zip/vector-zip (:state story)))
  ;(clojure.pprint/pprint story)
  (add-zip story (zip/vector-zip (recursively-convert-to-vectors (:state story)))))

(defn check-for-labyrinth-exit [story]
  (if (= :end (last (:zip story)))
    {:labyrinth-exit true}
    {:labyrinth-exit false}))

(defn explore-labyrinth [story]
  (if (nil? (:zip story))
    (enter-labyrinth story) ; need it to be zipped first!
      (if (= (check-for-labyrinth-exit story) {:labyrinth-exit true})
      (assoc-in story [:state :tags]
        (merge {} (get-in story [:state :tags]) {:labyrinth-exit true}))
      (let [z (:zip story)
            d (drunkards-walk-zipper z (:seed (:state story)))]

        (if (nil? d);(or (nil? d) (= :end (second d)))
          story
          (assoc story :zip d))))))

(defn run-through-labyrinth [story]
  (nth (iterate explore-labyrinth story)
       (nonogen.random/die-roll 30 (get story [:state :seed]))))

;(explore-labyrinth {:state {:test "test"} :zip (zip/vector-zip [])})


;(enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}})
;(explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}}))
;(:zip (explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}})))
;(zip/up (:zip (explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}}))))
;(explore-labyrinth (explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}})))
;(:zip (explore-labyrinth (explore-labyrinth (explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}})))))
;(:zip (explore-labyrinth (explore-labyrinth (explore-labyrinth (enter-labyrinth {:state {:seed 0 :tags [:test :one :two]}})))))



(defn describe-inside-labyrinth [story]
  (display-current-node (:zip story)))

(defn introduce-labyrinth []
  (fn [story] (apply str
                     (nonogen.random/variation
                      '(["a vast and perilous " "a mysterious " "an engmatic " "a recursive " "a " "a twilight " "an expansive " "a twisted " "a cybertextual " "an architectural "]
                        ["maze" "labyrinth" "labyrinth" "house of many doors" "library" "dimention in space" "zone" "garden" "data structure" "forest"]
                        [", a place where many had become lost" " from which few emerged" " that had never known the light of the sun" " that contained the entire world" " that was a map of itself" ", which is the world" " with many forking paths" " that lived in eternal twilight" ", the place that can sometimes be glimpsed through mirrors" " that was also this story as I tell it to you" " just on the other side of the garden wall" " that some call the unknown"]
                        ". ")
                      (get-in story [:state :seed])))))

;((introduce-labyrinth) {:state {:seed nil}})


(def example-story {:state
 {:subgenerator nil,
  :scene {:qualities {:anticipation 0}},
  :current-character
  {:name "Scheherazade",
   :tags
   {:stories [],
    :description "a queen of Persia",
    :gender :female,
    :can-tell-stories? true}},
  :seed 609,
  :characters
  [{:name "Shahryar",
    :tags {:description "a king of Persia", :gender :male}}
   {:name "Dunyazad",
    :tags {:description "a sister of Scheherazade", :gender :female}}
   {:name "Scheherazade",
    :tags
    {:stories [],
     :description "a queen of Persia",
     :gender :female,
     :can-tell-stories? true}}],
  :scenes [{:tags {:storyteller "Scheherazade", :reality-prime true}}],
  :events
  [{:tags {:event :storytelling-ending, :singular-selection true}}],
  :output ["This is the story that Scheherazade told:\n\n"],
  :exit nil},
 :generator
 nil})

(:zip (enter-labyrinth example-story))
(nth
 (iterate zip/next (:zip (enter-labyrinth example-story))
         ) 200)
