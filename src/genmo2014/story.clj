(ns genmo2014.story
   (:require [clojure.pprint]
             ))


;;;
;;; Predicates
;;;

(def example-predicate-list [:current-character-is-storyteller :test])

(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:current-character %))
   :test :current-character-is-storyteller

   })

(defn expand-predicates [predicates conversions]
  (map
   (fn [pred]
     (loop [p pred]
       (if (fn? p) p (recur (p conversions)))))
   predicates))


;;;
;;; Stories
;;;

(defn valid? [story]
  true)

(defn make-story []
  {:characters []
   :scenes []
   :output []}
  )

(def a-story
  {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
   :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
   :output []
   :places []
   })

(def events-list
  [{:predicates {:scene #(= % :storytelling)}
    :outcome []
    }])

(defn make-output-text [text]
  text)

(defn current-scene [story]
  (peek (:scenes story)))

(defn current-character [story]
  (first (filter #(= (:name %1) (:current-character (current-scene story))) (:characters story))))

(defn get-tags
  "Walks through the structure, getting all of the tags for the current moment."
  [story]
  (let [scene-tags (current-scene story)
        character-tags (:tags (current-character story))
        ]
    (merge scene-tags character-tags)))

;(get-tags a-story)

;;;
;;; Storylets
;;;


(defn make-storylet [[predicates outcome]]
  {:name nil
   :predicates predicates
   :outcome outcome})

(def a-storylet
  {:name nil
   :predicates [#(= (:storyteller %) (:current-character %))]
   :outcome {:output (make-output-text "So she said, \"It is related, O august king, that...\" ")
             :events []}
    })

(def example-storylets
  [{:name nil
   :predicates [#(= (:storyteller %) (:current-character %))]
   :outcome {:output (make-output-text "So she said, \"It is related, O august king, that...\" ")
             :events []}
    }
   {:name nil
   :predicates [#(= (:storyteller %) (:current-character %))]
   :outcome {:output (make-output-text "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n")
             :events []}
    }
   {:name nil
   :predicates [#(= (:storyteller %) (:current-character %))]
   :outcome {:output (make-output-text "And she told them a story. ")
             :events []}
    }])


(defn storylet-active?
  "Is the storylet valid in the current story state?"
  [story storylet]
  (let [tags (get-tags story)]
    (not (some false?
               (map
                (fn [p] (p tags))
                (:predicates storylet))))))

(defn filter-storylets
  "Filter the storylet list for only the ones valid in the currrent state."
  [story storylets]
  (filter #(storylet-active? story %1) storylets))

;(storylet-active? a-story (first example-storylets))
;(filter-storylets a-story example-storylets)
