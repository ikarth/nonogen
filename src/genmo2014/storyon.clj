(ns genmo2014.storyon
   (:require [clojure.pprint]
             [genmo2014.generators :as gens]
             ))



;;;
;;; Storyons
;;;

(defn make-storyon
  ([]
   {:name nil
    :predicates nil
    :result [[effect-output ""]]})
  ([predicates result]
   {:name nil
    :predicates predicates
    :result result})
  ([{:keys [predicates result]}]
   {:name nil
    :predicates predicates
    :outcome result}))

(def example-storyons
  [(make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "So she said, \"It is related, O august king, that...\" ")]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n")
             [:exit :outward]
             ]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "And she told them a story. ")]})])

;;;
;;; Predicates
;;;

(def predicate-conversions
  {:current-character-is-storyteller #(= (:storyteller %) (:current-character %))
   :test :current-character-is-storyteller

   })

(defn expand-predicates [predicates keyword-conversions]
  (map
   (fn [pred]
     (loop [p pred]
       (if (fn? p)
         p
         (if (or (vector? p))
           p
           (recur (p keyword-conversions))))))
   predicates))

;;;
;;; Filtering and Selecting
;;;

(defn filter-storyons [storyon-deck tags]
  storyon-deck) ;todo

(defn select-storyons [storyon-deck tags]
  storyon-deck) ;todo

;(defn select-action
;  "Given a map of tags (distilled from the current state) and a vector of actions,
;  finds the actions which are valid for the current event and state and picks one."
;  [state-tags actions]
;  (first ; TODO: add weighted selection
;   (filter
;      (fn [an-action]
;      (not (some false?
;                 (map (fn [predicate]
;                        (let [state state-tags
;                              tag ((key predicate) state)]
;                          (if tag
;                            ((val predicate) tag)
;                            false)
;                          ))
;                      (:predicates an-action)))))
;    actions)))

;;;
;;; Events
;;;

;; --- Process Event Queue ---
;; Called with the story-generator
;; Take the event at the top of the event queue (if there isn't one, send the exit effect or a special event that triggers the exit storyon)
;; Filter the storyon deck: from the active deck of storyons take the filtered set of valid storyons for this event+state
;; Select a storyon or storyons: build a vector of the chosen storyons
;; Map across the storyons, building a vector of their effects
;; Return the vector of effects

(defn events-to-effects [story storyon-deck]
  (let [tags (get story :tags)] ;todo: properly implement getting tags
    (map #(get % :result)
         (select-storyons
          (filter-storyons storyon-deck tags)
          tags))))

;(defn process-events
;  "Takes the story-module and processes the first event in the queue.
;  Returns a seq with the outcomes."
;   [story-module actions]
;   (let [event-queue (:event-queue story-module)
;         event (first event-queue)
;         result (remove nil? (map (fn [char]
;                      (let [state-tags (conj {:event event} (:tags char))]
;                         (select-action state-tags actions)))
;                    (:characters story-module)))
;         performed (map perform-action result)]
;    (assoc story-module :outcome-queue performed)
;    ))


;(defn process-outcomes
;  "Go through the outcome-queue of the story module and execute the outcomes."
;  [story-module]
;  (let [outcomes (:outcome-queue story-module)
;        eq (into (flatten (map (fn [o] (:events o)) outcomes))
;                 (rest (:event-queue story-module)))
;        texts (map (fn [o] (:text o)) outcomes)]
;    (assoc
;      (assoc
;        (assoc story-module :event-queue eq)
;        :outcome-queue nil)
;      :text-queue texts)))


;;;
;;; Effects
;;;

;; --- Process the Effects ---
;; Called with the argument of the story-generator
;; Take the vector of effects
;; Go through the vector of effects, applying their effects in order.
;; Return the result of the changes

;; --- Effects ---
;; - Pop Event Queue / Don't Pop Event Queue (determines if the event immidiately repeats)
;; - Replace Event (put the popped event back in the queue, to eventually run again)
;; - Insert Event (Stick an event at the bottom of the queue)
;; - Priority Event (Stick an event at the top of the queue, for an immidiate reaction)
;; - Exit: Inwards (At the generator level, return with :generator set to [this-current-state new-subgenerator]
;; - Exit: Outwards (At the generator level, return with an empty :generator result)
;; - Exit: In-Place (At the generator level, return with :generator set to the current state of this generator)
;; - Exit: Forwards (At the generator level, return with generator set to a completely different generator...which has the side effect of dropping this one )
;; - Output (Add information to the output queue)
;; - Feedback (Add information to the feedback vector)
;; - Alter state (Most common effect, with many different varities.)
;; - Surpress output?
;; Effects can also call other effects.

(defn story-effects [story]
  {:output (defn effect-output [output-text]
             (let [output-buffer (:output (:state story))]
               (assoc-in story [:state :output] (into output-buffer output-text))))
   :pop-event nil
   :surpress-pop nil
   :insert-event (defn insert-event [event-to-insert]
                   (let [event-queue (:events (:state story))]
                     (assoc-in story [:state :events] (into event-queue event-to-insert))))
   :exit (defn exit-command [command]
           (assoc-in story [:state :exit] command))
   :feedback nil
   })

(defn format-output [text]
  [:output text])


;; --- Process the Effects ---
;; Called with the argument of the story-generator
;; Take the vector of effects
;; Go through the vector of effects, applying their effects in order.
;; Return the result of the changes

(defn call-effects
  "Processes an effects list and applies the changes to the story. Takes
a story (to be returned when altered) and an ordered vector of effects,
and returns the new story.
  The effects-list is a vector of vectors. Each subvector is an effect,
and should start with a function or a keyword that reduces to a function
via the story-effects map. The rest of the vector is passed to the fn as
the effect's argument."
  [story effects-list]
  (reduce
   (fn [a-story effect-vec]
     (if (empty? effect-vec) ; skip empty vectors
       a-story
       (let [effect-fn (if (keyword? (first effect-vec)) ; if it's a keyword, grab it from the map
                         (get (story-effects a-story) (first effect-vec)) ; <- note that story-effects is scoped from above
                         (first effect-vec))]
         (if (fn? effect-fn) ; if it isn't a function (from, say, a failed effects-map lookup) then bail and return the unmodified story
           (apply effect-fn (rest effect-vec))
           a-story))))
   story
   effects-list))

;;;
;;; Story Generation
;;;

(defn generate-story [story-generator]
  (call-effects story-generator (events-to-effects story-generator example-storyons)))

;;;
;;; Sketching
;;;

(defn make-story []
  (gens/make-generator
   {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    :generator nil}))

(def example-story (assoc (make-story)
  :state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
          }))

example-story
;(generate-story example-story)


(def example-predicate-list [:current-character-is-storyteller :test [:vector "test"]])
(expand-predicates example-predicate-list predicate-conversions)


((:output (story-effects example-story)) "Test")

(def example-story  {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    })

;((:output (story-effects example-story)) "test")
;(map #(((key %1) (story-effects example-story)) (val %1)) {:output "test"})

(call-effects example-story [[:output "test"] [:output "test2"]])

(defn test-effect [state]
  {:a (fn [x] (+ x state))
   :b (fn [x] (- x state))})

(defn call-test
  []
  (reduce
   (fn [story effect-vec]
     (let [effect-fn (first effect-vec)]
       (apply (if (keyword? effect-fn)
                (get (test-effect story) effect-fn)
                effect-fn)
              (rest effect-vec))))
   9
   [[:b 7][:a 5]]))

(call-test)
