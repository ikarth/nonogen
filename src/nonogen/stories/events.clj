(ns nonogen.stories.events
   (:require [clojure.pprint]
             [nonogen.stories.predicates]
             [nonogen.stories.storyon]
             ))
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
  (let [tags (nonogen.stories.predicates/get-story-tags story)] ;todo: properly implement getting tags
    (reduce
     #(into %1 (get %2 :result))
     []
     (nonogen.stories.storyon/select-storyons
      (nonogen.stories.storyon/filter-storyons storyon-deck tags)
      tags))))

(defn default-events
  [{:tags []}]

  )
