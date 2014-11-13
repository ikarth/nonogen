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
;; Take the event at the top of the event queue (if there isn't one, send the exit  or a special event that triggers the exit storyon)
;; Filter the storyon deck: from the active deck of storyons take the filtered set of valid storyons for this event+state
;; Select a storyon or storyons: build a vector of the chosen storyons
;; Map across the storyons, building a vector of their s
;; Return the vector of s


;; - Get current tags (includes tags for top event on queue)
;; - Filter the storyon-deck via the tags
;; - Select a storyon or storyons, create a vector of the chosen one(s)
;; - map #(get % :result) against the vector of selected storyons
;; TODO: - process the map for additional s (like auto-popping the queue unlesss specifically surpressed)
;; - return the resulting vector of s

(defn events-to-s
"  Takes a story and a deck of storyons and processes the story's event queue,
returning the vector of s of the first event in the queue and popping
that event off the queue."
  [story storyon-deck]
  (let [tags (get story :tags)] ;todo: properly implement getting tags
    (reduce
     #(into %1 (get %2 :result))
     []
     (select-storyons (filter-storyons storyon-deck tags) tags))))


;;;
;;; s
;;;

;; --- s ---
;; - Pop Event Queue / Don't Pop Event Queue (determines if the event immidiately repeats)
;; - Replace Event (put the popped event back in the queue, to eventually run again)
;; - Insert Event (Stick an event at the bottom of the queue)
;; - Priority Event (Stick an event at the top of the queue, for an immidiate reaction)
;; - Exit: Inwards (At the generator level, return with :generator set to [this-current-state new-subgenerator]
;; - Exit: Outwards (At the generator level, return with an empty :generator result)
;; - Exit: In-Place (At the generator level, return with :generator set to the current state of this generator)
;; - Exit: Forwards (At the generator level, return with generator set to a completely different generator...which has the side  of dropping this one )
;; - Output (Add information to the output queue)
;; - Feedback (Add information to the feedback vector)
;; - Alter state (Most common , with many different varities.)
;; - Surpress output?
;; s can also call other s.

(defn story-s [story]
  {:output (defn -output [output-text]
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


;; --- Process the s ---
;; Called with the argument of the story-generator
;; Take the vector of s
;; Go through the vector of s, applying their s in order.
;; Return the result of the changes

(defn call-s
  "Processes an s list and applies the changes to the story. Takes
a story (to be returned when altered) and an ordered vector of s,
and returns the new story.
  The s-list is a vector of vectors. Each subvector is an ,
and should start with a function or a keyword that reduces to a function
via the story-s map. The rest of the vector is passed to the fn as
the 's argument."
  [story s-list]
   (loop [s story
          el s-list]
     (if (empty? el) ;empty vector? we're done
       s
       (let [first- (first el)
             head (first first-)
             -fn (if (keyword? head) (get (story-s s) head) head)] ; if it's a keyword, grab it from the map; <- note that story-s is scoped from above
         (if (ifn? -fn) ; if it isn't a function (because of, say, a failed s-map lookup) then bail and return the unmodified story
           (recur (apply -fn (rest first-)) (rest el))
           (recur s (rest el)))))))

;;;
;;; Story Generation
;;;

(defn generate-story [story-generator]
  (call-s story-generator
                (events-to-s story-generator example-storyons)))

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


;((:output (story-s example-story)) ["Test"])

(def example-story  {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    })

;((:output (story-s example-story)) "test")
;(map #(((key %1) (story-s example-story)) (val %1)) {:output "test"})

;(call-s example-story [[:output "test"] [:output "test2"]])

(defn test- [state]
  {:a (fn [x] (println "A") (+ x state))
   :b (fn [x] (println "B" )(- x state))})

(defn call-test
  []
  (reduce
   (fn [story -vec]
     (let [-fn (first -vec)]
       (apply (if (keyword? -fn)
                (get (test- story) -fn)
                -fn)
              (rest -vec))))
   9
   [[:b 7][:a 5]]))

;(call-test)
