(ns genmo2014.engine
   (:require (bigml.sampling [simple :as sample-simple]
                             [reservoir :as sample-reservoir]
                             [stream :as sample-stream])
             [clojure.pprint]
             ))



(def example-character {:id :id-example-character
                        :name "El Cid"
                        :tags {}})

(def example-fight-scene {:actors []
                          :tags {}
                          :events []
                          })

(def example-action {:predicates {}
                     :outcome nil
                     :text nil})

(def example-actions [{:predicates {:range #(<= 1 %)}
                     :outcome nil
                     :text "I run away from you."}
                      {:predicates {:range #(>= 1 %)}
                     :outcome nil
                     :text "I run towards you."}
                      ])

;; TODO
(def predicate-translations
  {:range-min #(< (:range %1) %2)}
    )

;; TODO
(defn interpert-predicates
  "Takes a seq of predicate-labels, runs it against the list of translations,
  returns the predicates."
  [predicates])


(defn filter-actions
  "Return only the actions that are valid for the current event,
  as determined by comparing the map of predicates to the event-state."
  [actions event-state]
  (filter (fn [an-action]
           (reduce true?
                   (map #(((key %) (:predicates an-action)) (val %)) (:tags event-state))
           )
           )
        actions))

(filter-actions example-actions {:tags {:range 1}})

(defn run-action [action]
  {:outcome (:outcome action)
   :text (:text action)})

(defn choose-action [actions event-state]
  (run-action
    (first ; TODO: change to actual sampling
      (filter-actions actions event-state))))

;(choose-action example-actions {:tags {:range 1}})

(defn process-outcome [outcomes scene]
  )







(def t-fns {:range #(> % 2) :color #(= :blue %)})
(def t-data {:range 3 :color :blue})
(filter
 #(((key %) t-fns) (val %))
 t-data)

(defn filter-by-criterion [predicates data]
  (filter
   #(((key %) predicates) (val %))
   data))

(map :predicates example-actions)

(filter
 #(((key %) {:range (fn [x] (< 1 x))}) (val %))
 {:range 2})

(filter
 #(((key %) (first (map :predicates example-actions))) (val %))
 {:range 2})


(def example-acts [{:predicates {:range #(= 1 %)}}
          {:predicates {:range #(= 2 %)}}
          ])

(def example-state {:tags {:range 1}})




(def a-story
  {:characters [{:name "Scheherazade" :tags {:character-storyteller true}} {:name "Shahryar"}]
   :event-queue [:event-storytelling-begin]
   :tags {}})

(def storytelling-events
  [{:event-storytelling-begin nil
    :event-storytelling-end nil
    :event-storytelling-telling nil}])

(def storytelling-actions
  [{:predicates {:event #(= % :event-storytelling-begin)
                 :character-storyteller true?}
    :outcomes {:text "So she said, \"It is related, O august king, that...\""
               :events [:event-storytelling-telling]}
    }
   {:predicates {:event #(= % :event-storytelling-end)
                 :character-storyteller true?}
    :outcomes {:text "And she ended, saying, \"But there is another tale which is more marvelous still. \""
               :events [:event-storytelling-begin]}
    }
   {:predicates {:event #(= % :event-storytelling-telling)
                 :character-storyteller true?}
    :outcomes {:text "And she told them a story. "
               :events [:event-storytelling-end]}
    }])

(defn check-predicates [predicates state]


  )

(defn select-action [state-tags actions]
  (first
   (filter
      (fn [an-action]
      (not (some false?
                 (map (fn [predicate]
                        (let [state state-tags
                              tag ((key predicate) state)]
                          (if tag
                            ((val predicate) tag)
                            false)
                          ))
                      (:predicates an-action)))))
    actions)))


(let [an-action (first storytelling-actions)
      state-tags {:event :event-storytelling-begin :character-storyteller true}
      ]
  (map (fn [predicate]
                        (let [state state-tags
                              tag ((key predicate) state)]
                          (if tag
                            ((val predicate) tag)
                            false)
                          ))
                      (:predicates an-action)))

(select-action
 {:event :event-storytelling-begin :character-storyteller true}
 storytelling-actions)

(:predicates (first storytelling-actions))

((key (second (:predicates (first storytelling-actions))))
 {:event :event-storytelling-begin})


;(map #(> % 5) [3 4 5 6 7 8])
;(not (some false? (map #(> % 5) [6 7 8])))

(conj {:event 2 } {:tags 2})

(map (fn [x] ((key x) (:predicates (first storytelling-actions)))
                        (val x))
     {:character-storyteller false})

(defn process-events [story-module actions]
  (let [event-queue (:event-queue story-module)
        event (first event-queue)
        char-acts (map (fn [char]
                         (let [state-tags (conj {:event event} (:tags char))]
                           (select-action state-tags actions)))
                       (:characters story-module))]
    char-acts))

(process-events a-story storytelling-actions)
