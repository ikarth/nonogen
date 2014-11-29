(ns nonogen.stories.nights
   (:require [clojure.pprint]
             [clojure.inspector]
             [nonogen.generators :as gens]
             [nonogen.stories.story]
             [nonogen.stories.storyon-library]))





(defn a-thousand-and-one-nights []
  (nonogen.stories.story/make-thousand-nights-story
   nonogen.stories.storyon-library/example-storyons))


;(:make-basic-story (nonogen.stories.story/make-make-story nonogen.stories.storyon-library/example-storyons))
;(nonogen.stories.story/make-make-story nonogen.stories.storyon-library/example-storyons)



;;;
;;; Sketching
;;;

(a-thousand-and-one-nights)
(get-in (a-thousand-and-one-nights) [:state :seed])

  (gens/insert (gens/make-generator-stack)
               (a-thousand-and-one-nights))


(gens/process
  (gens/insert (gens/make-generator-stack)
               (a-thousand-and-one-nights)))

  (gens/process
 (gens/process
  (gens/insert (gens/make-generator-stack)
               (a-thousand-and-one-nights))))

(nth (iterate gens/process
              (gens/insert (gens/make-generator-stack)
                           (a-thousand-and-one-nights)))
     30)


 (nth
      (iterate gens/process
               (gens/insert
                (gens/make-generator-stack)
                (a-thousand-and-one-nights)

                ))
    600)

 ;(clojure.inspector/inspect-tree



 ; (add-event
 ;               (add-scene (make-story (make-characters)) {:tags {:storyteller "Scheherazade"}})
 ;               {:tags {:event :story-introduction}}
 ;              )

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

