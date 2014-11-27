(ns nonogen.stories.storyon-library
   (:require [nonogen.stories.storyon :refer :all]
             [nonogen.stories.story :as story]
             [nonogen.borges.babel]
             [nonogen.stories.output  :refer :all]
             ))


;;;
;;; Storyon Library
;;;


(declare make-story)
(declare example-storyons)

(defn make-story [seed]
  (nonogen.stories.story/make-storytelling-story example-storyons seed))

;(make-story nil)

(def example-storyons
  [(nonogen.stories.storyon/make-storyon
   {:predicates [:at-least-one-character [:event :story-introduction]]
    :result [[:output "Once upon a time, " ]
             [:output describe-all-characters ". "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :pick-next-scene :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :pick-next-scene]]
    :result [[:output storyteller-name " suggested that " she " should tell a story, because it was Alex's birthday. " ]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:not-current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output She "begged " storyteller-name " to tell the story. "]
             [:quality :increment :scene :anticipation]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output "So " she " began, \"It is related, O august king, that...\" "]
             [:pop-event true]
             [:quality :erase :scene :anticipation]
             [:add-event (story/make-event {:tags {:event :storytelling-ready-to-tell :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "This is the story that " nonogen.stories.output/storyteller-name " told:\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "And " storyteller-name " told a very exciting story. "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
   ;(nonogen.stories.storyon/make-storyon
   ;{:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
   ; :result [[:output "Then " storyteller-name " opened her book and read them the following page:\n\n"]
   ;          [:exit-inward (nonogen.borges.babel/make-babel)]
   ;          [:pop-event true]
   ;          [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
   ;          ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending] [:not-tag :reality-prime]]
    :result [[:output "\"And that was how it happened,\" " storyteller-name " said, ending " her " story.\n\n"]
             [:pop-event true]
             [:exit :outward]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending]]
    :result [[:output "Then " storyteller-name " ended " her " story, saying, \"But there is another tale which is more marvelous still.\"\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             [:exit :inplace]
             ]})
   ])


