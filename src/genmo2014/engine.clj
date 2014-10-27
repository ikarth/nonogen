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
    :outcome {:text "So she said, \"It is related, O august king, that...\" "
              :events [:event-storytelling-telling]}
    }
   {:predicates {:event #(= % :event-storytelling-end)
                 :character-storyteller true?}
    :outcome {:text "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n"
               :events [:event-storytelling-begin]}
    }
   {:predicates {:event #(= % :event-storytelling-telling)
                 :character-storyteller true?}
    :outcome {:text "And she told them a story. "
               :events [:event-storytelling-end]}
    }])

(defn check-predicates [predicates state])

(defn perform-action
  "Given an action, returns the outcome of performing that action."
  [action]
  (:outcome action))


(defn select-action
  "Given a map of tags (distilled from the current state) and a vector of actions,
  finds the actions which are valid for the current event and state and picks one."
  [state-tags actions]
  (first ; TODO: add weighted selection
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

(defn process-events
  "Takes the story-module and processes the first event in the queue.
  Returns a seq with the outcomes."
   [story-module actions]
   (let [event-queue (:event-queue story-module)
         event (first event-queue)
         result (remove nil? (map (fn [char]
                      (let [state-tags (conj {:event event} (:tags char))]
                         (select-action state-tags actions)))
                    (:characters story-module)))
         performed (map perform-action result)]
    (assoc story-module :outcome-queue performed)
    ))



(defn process-outcomes [story-module]
  (let [outcomes (:outcome-queue story-module)
        eq (into (flatten (map (fn [o] (:events o)) outcomes))
                 (rest (:event-queue story-module)))
        texts (map (fn [o] (:text o)) outcomes)]
    (assoc
      (assoc
        (assoc story-module :event-queue eq)
        :outcome-queue nil)
      :text-queue texts)))

(defn process-texts [story-module]
  (print (apply str (flatten (:text-queue story-module))))
  story-module)

(process-events a-story storytelling-actions)

(print "hello\n\n")

(defn engine-loop [a-story actions]
  ((comp process-texts process-outcomes process-events) a-story actions))

(engine-loop a-story storytelling-actions)

(defn s-loop [x] (engine-loop x storytelling-actions))

(s-loop (s-loop a-story))

(s-loop (s-loop (s-loop (s-loop (s-loop (s-loop a-story))))))

(defn tell-story [length]
  (loop [a storytelling-actions
         s a-story
         i 0]
    (if (< i length)
      (recur a (engine-loop s a) (inc i))
      s)))


(tell-story 9)

;(defn story-engine [a-story actions]
;  (recur a-story actions))

;(lazy-seq (story-engine a-story storytelling-actions))
;(story-engine a-story storytelling-actions)


(process-texts {:text-queue (seq '("test"))})

(conj '(1 2 3 4) '(5 6))

;(clojure.pprint/pprint (str (flatten (:test {:test 1}))))
