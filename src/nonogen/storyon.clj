(ns nonogen.storyon
   (:require [clojure.pprint]
             [nonogen.generators :as gens]
             ))



;;;
;;; Storyons
;;;

(defn make-storyon
  ([]
   {:name nil
    :predicates nil
    :result [[:output ""]]})
  ([predicates result]
   {:name nil
    :predicates predicates
    :result result})
  ([{:keys [predicates result]}]
   {:name nil
    :predicates predicates
    :result result}))

(def example-storyons
  [(make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [[:output "So she said, \"It is related, O august king, that...\" "]]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [[:output "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n"]
             ;[:exit :outward]
             ]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [[:output "And she told them a story. "]]})
   (make-storyon
   {:predicates []
    :result [[:output "And then debug text was printed. "]]})

   ])

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


;; - Get current tags (includes tags for top event on queue)
;; - Filter the storyon-deck via the tags
;; - Select a storyon or storyons, create a vector of the chosen one(s)
;; - map #(get % :result) against the vector of selected storyons
;; TODO: - process the map for additional effects (like auto-popping the queue unlesss specifically surpressed)
;; - return the resulting vector of effecs

(defn events-to-effects
"  Takes a story and a deck of storyons and processes the story's event queue,
returning the vector of effects of the first event in the queue and popping
that event off the queue."
  [story storyon-deck]
  (let [tags (get story :tags)] ;todo: properly implement getting tags
    (reduce
     #(into %1 (get %2 :result))
     []
     (select-storyons (filter-storyons storyon-deck tags) tags))))


;;;
;;; Effects
;;;

;; --- Effects ---
;; - Pop Event Queue / Don't Pop Event Queue (determines if the event immidiately repeats)
;; - Replace Event (put the popped event back in the queue, to eventually run again)
;; - Insert Event (Stick an event at the bottom of the queue)
;; - Priority Event (Stick an event at the top of the queue, for an immidiate reaction)
;; - Exit: Inwards (At the generator level, return with :generator set to [this-current-state new-subgenerator]
;; - Exit: Outwards (At the generator level, return with an empty :generator result)
;; - Exit: In-Place (At the generator level, return with :generator set to the current state of this generator)
;; - Exit: Forwards (At the generator level, return with generator set to a completely different generator...which has the side-effect of dropping this one )
;; - Output (Add information to the output queue)
;; - Feedback (Add information to the feedback vector)
;; - Alter state (Most common effect, with many different varities.)
;; - Surpress output?
;; Effects can also call other Effects.

(defn story-effects [story]
  {:output (defn effect-output [output-text]
             (let [output-buffer (if (empty? (:output (:state story)))
                                   []
                                   (:output (:state story)))]
               (assoc-in story [:state :output] (conj output-buffer output-text))))
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
;; Take the vector of Effects
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
   (loop [s story
          el effects-list]
     (if (empty? el) ;empty vector? we're done
       s
       (let [first-effect (first el)
             head (first first-effect)
             effect-fn (if (keyword? head) (get (story-effects s) head) head)] ; if it's a keyword, grab it from the map; <- note that story-effects is scoped from above
         (if (ifn? effect-fn) ; if it isn't a function (because of, say, a failed effects-map lookup) then bail and return the unmodified story
           (recur (apply effect-fn (rest first-effect)) (rest el))
           (recur s (rest el)))))))

;;;
;;; Story Generation
;;;

(defn generate-story [story-generator]
  (call-effects story-generator
                (events-to-effects story-generator example-storyons)))

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

(def example-story
  (merge-with
   #(merge %1 %2)
   (make-story)
   {:state {:characters [{:name "Scheherazade" :tags {:stories [] :gender :female}} {:name "Shahryar" :tags {:gender :male}}]
          :scenes [{:current-character "Scheherazade" :scene :storytelling :storyteller "Scheherazade"}]
          }}))

example-story
(println "-----------------------------")
(println "-----------------------------")
(println "-----------------------------")
(clojure.pprint/pprint "Running test...")
(generate-story example-story)


(def example-predicate-list [:current-character-is-storyteller :test [:vector "test"]])
;(expand-predicates example-predicate-list predicate-conversions)


;((:output (story-effects example-story)) ["Test"])

(def example-story  {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    })

;((:output (story-effects example-story)) "test")
;(map #(((key %1) (story-effects example-story)) (val %1)) {:output "test"})

;(call-s example-story [[:output "test"] [:output "test2"]])

(defn test-effect [state]
  {:a (fn [x] (println "A") (+ x state))
   :b (fn [x] (println "B" )(- x state))})

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

;(call-test)
