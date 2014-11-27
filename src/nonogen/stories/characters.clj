(ns nonogen.stories.characters
   (:require [nonogen.generators :as gens]
             [nonogen.random :as random]
             ))

(def thousand-nights-characters
  [{:name "Shahryar" :tags {:description "a king of Persia" :gender :male}}
   {:name "Dunyazad" :tags {:description "a sister of Scheherazade" :gender :female}}
   {:name "Scheherazade" :tags {:stories [] :description "a queen of Persia" :gender :female :can-tell-stories? true}} ])


(def prebuilt-characters
  [;{:name "Shahryar" :tags {:description "a king of Persia" :gender :male}}
   ;{:name "Dunyazad" :tags {:description "sister of Scheherazade" :gender :female}}
   {:name "Scheherazade" :tags {:stories [] :description "a queen of Persia" :gender :female :can-tell-stories? true}}
   {:name "Marco Polo" :tags {:stories [] :description "an explorer of Venice" :gender :male :can-tell-stories? true :curious true}}
   {:name "Kublai Khan" :tags {:stories [] :description "a Khagan of the Ikh Mongol Uls" :gender :male :can-tell-stories? true :curious true}}
   {:name "Homer" :tags {:stories [] :description "a blind poet" :gender :male :can-tell-stories? true :blind true}}
   {:name "Geoffery Chaucer" :tags {:stories [] :description "an English poet" :gender :male :can-tell-stories? true}}
   {:name "Murasaki Shikibu" :tags {:stories [] :description "a lady of the Imperial Court" :gender :female :can-tell-stories? true}}
   {:name "Dante Alighieri" :tags {:stories [] :description "a poet exiled from Florence" :gender :male :can-tell-stories? true}}
   {:name "Virgil" :tags {:stories [] :description "a poet of Rome" :gender :male :can-tell-stories? true}}
   {:name "Jorges Luis Borges" :tags {:stories [] :description "a blind librarian" :gender :male :can-tell-stories? true :blind true}}
   {:name "Little Nemo" :tags {:stories [] :description "a child trying to go to Slumberland" :gender :male :can-tell-stories? true :blind true}}
   ])

(defn make-character-list [how-many seed]
  (into [] (take how-many (nonogen.random/shuffle-randomly prebuilt-characters seed))))


(defn pick-storyteller
  "Given a vector of characters, pick one and make them the storyteller."
  [char-list seed]
  (let [f (filter #(:can-tell-stories? (:tags %)) char-list)
        sh (first (nonogen.random/shuffle-randomly f seed))]
    (println sh)
  (:name sh)))

(defn make-thousand-nights-character-list []
  thousand-nights-characters
  )
