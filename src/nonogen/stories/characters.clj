(ns nonogen.stories.characters
   (:require [nonogen.generators :as gens]
             [nonogen.random :as random]
             [clojure.set]
             ))

(def thousand-nights-characters
  [{:name "Shahryar" :tags {:description "a king of Persia" :gender :male}}
   {:name "Dunyazad" :tags {:description "a sister of Scheherazade" :gender :female}}
   {:name "Scheherazade" :tags {:stories [] :description "a queen of Persia" :gender :female :can-tell-stories? true}} ])


(def prebuilt-characters
  #{{:name "Shahryar" :tags {:stories [] :description "a king of Persia" :gender :male :can-tell-stories? true}}
   {:name "Dunyazad" :tags {:stories [] :description "the sister of Scheherazade" :gender :female :can-tell-stories? true}}
   {:name "Scheherazade" :tags {:stories [] :description "a queen of Persia" :gender :female :can-tell-stories? true}}
   {:name "Marco Polo" :tags {:stories [] :description "an explorer of Venice" :gender :male :can-tell-stories? true :curious true}}
   {:name "Kublai Khan" :tags {:stories [] :description "a Khagan of the Ikh Mongol Uls" :gender :male :can-tell-stories? true :curious true}}
   {:name "Homer" :tags {:stories [] :description "a blind poet" :gender :male :can-tell-stories? true :blind true}}
   {:name "Geoffery Chaucer" :tags {:stories [] :description "an English poet" :gender :male :can-tell-stories? true}}
   {:name "Murasaki Shikibu" :tags {:stories [] :description "a lady of the Imperial Court" :gender :female :can-tell-stories? true}}
   {:name "Dante Alighieri" :tags {:stories [] :description "a poet exiled from Florence" :gender :male :can-tell-stories? true}}
   {:name "Virgil" :tags {:stories [] :description "a poet of Rome" :gender :male :can-tell-stories? true}}
   {:name "Jorge Luis Borges" :tags {:stories [] :description "a blind librarian" :gender :male :can-tell-stories? true :blind true}}
   {:name "Little Nemo" :tags {:stories [] :description "a child trying to go to Slumberland" :gender :male :can-tell-stories? true :blind true}}
   {:name "Socrates" :tags {:stories [] :description "a philosopher" :gender :male :can-tell-stories? true :blind true}}
   {:name "Asterion" :tags {:stories [] :description "a member of royalty" :gender :male :can-tell-stories? true}}
   {:name "Alice" :tags {:stories [] :description "a young English girl" :gender :female :can-tell-stories? true}}
   })

(defn make-character-list [how-many seed]
  (into [] (take how-many (nonogen.random/shuffle-randomly prebuilt-characters seed))))


(defn pick-storyteller
  "Given a vector of characters, pick one and make them the storyteller."
  [char-list seed]
  ;(println char-list " " seed)
  (let [f (filter #(:can-tell-stories? (:tags %)) char-list)
        sh (first (nonogen.random/shuffle-randomly f seed))]
    ;(println sh)
  (:name sh)))

(defn make-thousand-nights-character-list []
  thousand-nights-characters
  )

(defn additional-storytelling-character [story]
  (let [seed (get-in story [:state :seed])
        existing (into #{} (filter #(:can-tell-stories? (:tags %)) (get-in story [:state :characters])))
        possible (clojure.set/difference prebuilt-characters existing)
        addition (first (nonogen.random/shuffle-randomly possible seed))]
    (if (nil? addition)
      story
      (assoc-in (assoc-in story [:state :characters]
                          (conj (into [] (get-in story [:state :characters])) addition))
                [:state :scene-characters]
                (conj (into [] (get-in story [:state :scene-characters])) addition)))))

(defn additional-character [story]
  (let [seed (get-in story [:state :seed])
        existing (into #{} (get-in story [:state :characters]))
        possible (clojure.set/difference prebuilt-characters existing)
        addition (first (nonogen.random/shuffle-randomly possible seed))]
    (if (nil? addition)
      story
      (assoc-in (assoc-in story [:state :characters]
                          (conj (into [] (get-in story [:state :characters])) addition))
                [:state :scene-characters]
                (conj (into [] (get-in story [:state :scene-characters])) addition)))))


(defn remove-character [story c]
  (let [current (map :name (get-in story [:state :characters]))
        diff (clojure.set/difference (into #{} current) #{c})
        f (clojure.set/select #(clojure.set/subset? #{(:name %)} diff) (into #{} (get-in story [:state :characters])))]
    (assoc-in story [:state :characters] (into [] f))
    ))

(defn remove-scene-characters [story]
  ;(println "Remove scene characters:" story)
  (let [removing (get-in story [:state :scene-characters])
        goodbye (reduce #(remove-character %1 (:name %2)) story removing)
        gone (assoc-in goodbye [:state :scene-characters] nil)]
    gone))

(def testac (additional-character (additional-character {})))
(remove-character (remove-character testac "Virgil") "Homer")
(map #(:name %1) (get-in testac [:state :scene-characters]))
(reduce #(remove-character %1 (:name %2)) testac ["Homer" "Virgil"])
(reduce #(remove-character %1 (:name %2)) testac (get-in testac [:state :scene-characters]))






(let [f (filter #(:can-tell-stories? (:tags %)) prebuilt-characters)
        sh (nonogen.random/shuffle-randomly f nil)]
    ;(println sh)
  (map :name sh))
