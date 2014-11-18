(ns nonogen.stories.storyon-library
   (:require [nonogen.stories.storyon :refer :all]
             ))


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
