(ns genmo2014.storyon
   (:require [clojure.pprint]
             ))


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
;;; Events
;;;




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

(def example-story  {:state {:characters []
            :scenes []
            :events []
            :output []
            }
    })

;((:output (story-effects example-story)) "test")
;(map #(((key %1) (story-effects example-story)) (val %1)) {:output "test"})

(defn call-effects [story effects-list]
  (reduce
   #(((first %2)
      (story-effects %1))
     (rest %2))
   story
   effects-list))

(call-effects example-story [[:output "test"] [:output "test2"]])


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

;;;
;;; Sketching
;;;

;(defn )

(defn format-output [text]
  [:output text]
  )

(def example-storyons
  [(make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "So she said, \"It is related, O august king, that...\" ")]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n")
             :exit :outward
             ]})
   (make-storyon
   {:predicates [:current-character-is-storyteller]
    :result [(format-output "And she told them a story. ")]})])


(def example-predicate-list [:current-character-is-storyteller :test [:vector "test"]])
(expand-predicates example-predicate-list predicate-conversions)


((:output (story-effects example-story)) "Test")
