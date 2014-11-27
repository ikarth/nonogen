(ns nonogen.stories.effects
  (:require [clojure.pprint]
            [nonogen.stories.output]
            ))

(defn positions [pred coll]
  (keep-indexed #(when (pred %2) %1) coll))

(defn next-in-coll
  "Takes a collection and an item to match, finds the first instance of
  that item, and return the thing that follows it in the collection."
  [coll match]
  (if (nil? match)
    (next-in-coll coll 1)
    (let [cur (positions #(= % match) coll)]
      (if (< (count cur) 1)
        match
        (first (subvec (into [] coll) (mod (inc (first cur)) (count coll))))))))

(defn if-not-included [pred check-for otherwise]
  (if (some check-for)
    pred
    (conj pred [otherwise])))

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

(defn add-to-story [story thing-type thing]
  (assoc-in story thing-type (conj (get-in story thing-type) thing)))

(defn format-output [story text]
  (nonogen.stories.output/parse story text))

(defn story-effects [story]
  {:output (defn effect-output [& output-text]
             (let [output-buffer (if (empty? (:output (:state story)))
                                   []
                                   (:output (:state story)))]
               (assoc-in story [:state :output] (conj output-buffer (format-output story output-text)))))

   :pop-event (defn pop-event-queue [yes]
                (if yes
                  (if (not (empty? (get-in story [:state :events])))
                    (assoc-in story [:state :events] (pop (get-in story [:state :events])))
                    story)
                  story))
   :surpress-pop nil
   :insert-event (defn insert-event [event-to-insert]
                   (let [event-queue (:events (:state story))]
                     (assoc-in story [:state :events] (into event-queue event-to-insert))))
   :exit (defn exit-command [command]
           (assoc-in story [:state :exit] command))
   :feedback nil
   :add-state-tag (defn add-state-tag [thing]
                    (assoc-in story [:state :tags] (merge (get-in story [:state :tags]) thing)))
   :remove-state-tag (defn remove-state-tag [thing]
                       (assoc-in story [:state :tags] (dissoc (get-in story [:state :tags]) thing)))
   :add-event (defn add-event [event]
                (add-to-story story [:state :events] event))
   :increment (defn increment [_]
                (assoc story :counter (inc (let [c (:counter story)]
                                             (if (number? c) c 0)
                                             ))))
   :quality (defn alter-quality [action place qual]
              (let [a (case action
                        :increment inc
                        :decrement #(- % 1)
                        :erase (fn [_] 0)
                        (fn [i] i))
                    p (case place  ; todo: add qualities to characters and events
                        :scene [:state :scene :qualities qual]
                        :state [:state :qualities qual]
                        nil)]
                (if p
                  (assoc-in story p (a (get-in story p)))
                  )))
   :exit-inward (defn embed-story [substory]
                  (let [sub (if (ifn? substory) (substory (:seed (:state story))) substory)]
                    (assoc-in (assoc-in story [:state :subgenerator] sub) [:state :exit] :inward)))
   :exit-outward (defn exit-outward [_]
                   (assoc-in story [:state :exit] :outward))
   :advance-character (defn advance-character
                        "Look at the :current-character, and swap in the next valid character instead."
                        [p]
                        (if (false? p)
                          story
                          (assoc-in story [:state :current-character]
                                    (let [prev (:current-character (:state story))
                                          charlist (:characters (:state story))]
                                      (if (nil? prev)
                                        (peek charlist)
                                        (next-in-coll charlist prev)))

                                    )))

   })

(defn increment-seed [story]
  (let [seed (get-in story [:state :seed])]
    (assoc-in story [:state :seed] (+ 1 seed))))

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
  (loop [s (increment-seed story)
         el effects-list]
    (if (empty? el) ;empty vector? we're done
      s
      (let [first-effect (first el)
            head (first first-effect)
            effect-fn (if (keyword? head) (get (story-effects s) head) head)] ; if it's a keyword, grab it from the map; <- note that story-effects is scoped from above
        (if (ifn? effect-fn) ; if it isn't a function (because of, say, a failed effects-map lookup) then bail and return the unmodified story
          (recur (apply effect-fn (rest first-effect)) (rest el))
          (recur s (rest el)))))))
