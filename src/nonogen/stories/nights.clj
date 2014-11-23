(ns nonogen.stories.nights
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.stories.events]
             [nonogen.stories.effects]
             [nonogen.stories.storyon]
             ))

;;;;;
;;;;; Generators to output stories
;;;;;


;;;
;;; Story Generation Process
;;;

(defn clear-state
  "Clears out bits of the story state that should not carry over from last time."
  [story-generator]
  (assoc-in (assoc-in story-generator [:state :output] [])
            [:state :exit]
            nil))

(declare example-storyons)

(defn generate-story
  "Runs the story generator "
  [story-generator]
  (let [story-gen (clear-state story-generator)]
    (nonogen.stories.effects/call-effects story-gen
                                          (nonogen.stories.events/events-to-effects story-gen example-storyons))))

;; --- Process story ---
;; Passed a story (& maybe the storyon list?)
;;  Figure out the current tag set
;;   Call Process Event Queue
;;   Call Process Effects
;; Check the state of the story
;;   If the story's state includes an exit command, return (with :generator [] set appropreately)
;;   Otherwise, recur

(defn exit-state [story-generator]
  (let [exit (:exit (:state story-generator))]
    (case exit
      :outward nil
      :inplace [story-generator]
      :inward [(assoc-in story-generator [:state :subgenerator] nil)
               (assoc-in (get-in story-generator [:state :subgenerator]) [:state :seed] (get-in story-generator [:state :seed])) ] ; the order is important!
      :forward [(:inward (:state story-generator))]
      [story-generator])))

(defn story [story-generator]
  (let [output
    (let [story-gen (generate-story story-generator)]
      ;(clojure.pprint/pprint story-gen)
      {:output (:output (:state story-gen))
       :generator (exit-state story-gen)
       :feedback nil})]
    output))

;;;
;;; Stories
;;;

(defn make-story
  ([]
   (make-story {:characters [] :scenes [] :events [] :output []} (fn [g] (story g))))
  ([characters]
   (make-story {:characters characters :scenes [] :events [] :output []} (fn [g] (story g))))
  ([{:keys [characters scenes events output depth]} generator]
   (gens/make-generator
    {:state {
             :seed -1;(rand 99999999)
             :characters characters
             :scenes scenes
             :events events
             :output output
             :exit nil             }
     :generator generator})))

(defn add-generator [story generator]
  (assoc story :generator generator))

(defn add-character [story character]
  (assoc-in story [:state :characters] (conj (:characters (:state story)) character)))

(defn add-to-story [story thing-type thing]
  (assoc-in story thing-type (conj (get-in story thing-type) thing)))

(defn add-event [story event]
  ((partial add-to-story story [:state :events]) event))

(defn add-tag [story thing]
  (assoc-in story [:state :tags] (merge (get-in story [:state :tags]) thing)))

(defn add-scene [story scene]
  ((partial add-to-story story [:state :scenes]) scene))

; todo: Randomly generate some characters, or create characters from a set of templates
(defn make-characters
  "Returns a vector of newly-created characters."
  []
  [{:name "Shahryar" :tags {:gender :male}}
   {:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}} ])

(defn make-basic-story []
  (add-event (add-scene (make-story (make-characters))
                        {:tags {:storyteller "Scheherazade"}})
             {:tags {:event :story-introduction}}))



(defn make-event [event-map]
  event-map)
  ;(merge event-map {:seed 7}));(hash (str event-map))}))

;;;
;;; Storyon Library
;;;


(def example-storyons
  [(nonogen.stories.storyon/make-storyon
   {:predicates [:at-least-one-character [:event :story-introduction]]
    :result [[:output "Once upon a time, there was [main-character-description] named [main-character-name]. " ]
             [:pop-event true]
             [:add-event (make-event {:tags {:event :pick-next-scene :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :pick-next-scene]]
    :result [[:output "[She] suggested that [she] should tell a story, because it was Alex's birthday. " ]
             [:pop-event true]
             [:add-event (make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:not-current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output "[She] begged [character-storyteller-name] to tell the story. "]
             [:quality :increment :scene :anticipation]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output "So [she] began, \"It is related, O august king, that...\" "]
             [:pop-event true]
             [:quality :erase :scene :anticipation]
             [:add-event (make-event {:tags {:event :storytelling-ready-to-tell :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "Then [she] told the following story:\n\n"]
             [:pop-event true]
             [:add-event (make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward (nonogen.stories.nights/make-basic-story)]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "And [she] told a very exciting story.\n\n"]
             [:pop-event true]
             [:add-event (make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending] [:not-tag :reality-prime]]
    :result [[:output "\"And that was how it happened,\" [she] said, ending [her] story.\n\n"]
             [:pop-event true]
             [:exit :outward]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending]]
    :result [[:output "Then [she] ended [her] story, saying, \"But there is another tale which is more marvelous still.\"\n\n"]
             [:pop-event true]
             [:add-event (make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             [:exit :inplace]
             ]})
   ])








;;;
;;; Sketching
;;;


 ;(clojure.inspector/inspect-tree

  (nth
  (iterate gens/process
  (gens/insert (gens/make-generator-stack)
               (add-event
                (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}})
                {:tags {:event :story-introduction}}
               )))
   6)



;; (let [astory (make-story (make-characters))
;;       tags (nonogen.stories.predicates/get-story-tags astory)]
;;   (nonogen.stories.storyon/select-storyons
;;    (nonogen.stories.storyon/filter-storyons nonogen.stories.storyon-library/example-storyons
;;                                                                                     astory)
;;                                            astory)

;;   )



;; (nonogen.stories.predicates/get-story-tags (make-story (make-characters)))


;; (nonogen.stories.predicates/get-story-tags
;;  (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}}))

;; (let [gen-story (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}})]
;;   (nonogen.stories.effects/call-effects gen-story
;;    (nonogen.stories.events/events-to-effects
;;     gen-story
;;   nonogen.stories.storyon-library/example-storyons)))



;; ;(def example-story (assoc (make-story)
;; ;  :state {:characters [{:name "Shahryar" :tags {:gender :male}}
;; ;   {:name "Scheherazade" :tags {:stories [] :gender :female :can-tell-stories? true}}]
;; ;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;; ;          :output []
;; ;          }))
;; (def example-story (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}}))
;; ((get example-story :generator) example-story)


















;(def example-story (assoc (make-story)
;  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
;;          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
;          :output []
;          }))
;example-story



;((get example-story :generator) example-story)
;(genmo2014.storyon/generate-story example-story)


;(def gen-stack (genmo2014.generators/make-generator-stack))
;(gens/insert gen-stack example-story)
;(gens/process (gens/insert gen-stack example-story))
;(gens/process (gens/insert (gens/insert gen-stack example-story) example-story))
;(gens/process (gens/process (gens/insert (gens/insert gen-stack example-story) example-story)))

;(nth (iterate gens/process (gens/insert gen-stack example-story)) 15)

;(clojure.pprint/pprint (nth (iterate gens/process (gens/insert gen-stack example-story)) 15))

