(ns genmo2014.engine
   (:require [clojure.pprint]
             ))




(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:current-character %))
   :test :current-character-is-storyteller

   })

(def example-predicate-list [:current-character-is-storyteller :test])

(defn expand-predicates [predicates conversions]
  (map
   (fn [pred]
     (loop [p pred]
       (if (fn? p)
         p
         (recur (p conversions)))))
   predicates))

(vector? (first {:one 1}))

(if (fn? :one)
  true
  false)

;(map
; (fn [p] p)
; predicate-conversions)

(let [predicate (first example-predicate-list)]
  (if (fn? predicate)
    predicate
    (predicate predicate-conversions)))

(expand-predicates
 example-predicate-list
 predicate-conversions)

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
   })

(def events-list
  [{:predicates {:scene #(= % :storytelling)}
    :outcome []
    }])

(defn make-event [])
;(defn make-action [])

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

(get-tags a-story)

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
  "Filter the storylet list for only the ones valid in the currrent state."
  [story storylet]
  (let [tags (get-tags story)]
    (not (some false?
               (map
                (fn [p] (p tags))
                (:predicates storylet))))))

(defn filter-storylets [story storylets]
  (filter #(storylet-active? story %1) storylets))

(map
 (fn [p] (p (get-tags a-story)))
 (:predicates (first example-storylets)))

(first example-storylets)

((first (:predicates (first example-storylets)))
  (get-tags a-story))

(storylet-active? a-story (first example-storylets))
(filter-storylets a-story example-storylets)

(defn process [])

;(defn make-effect [])

(defn valid-effect? [effect]
  true)

;(defn apply-effect [state effect]
;  (if (valid-effect? effect)
;    (effect state event)
;    state))
