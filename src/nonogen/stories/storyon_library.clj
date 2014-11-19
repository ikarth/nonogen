(ns nonogen.stories.storyon-library
   (:require [nonogen.stories.storyon :refer :all]
             ))


(def example-storyons
  [(make-storyon
   {:predicates [:current-character-is-storyteller :storytelling-beginning]
    :result [[:output "So she said, \"It is related, O august king, that...\" "]
             [:pop-event true]
             [:increment true]
             [:add-event {:tags {:storytelling-ready-to-tell true :singular-selection true}}]
             ]})
   (make-storyon
   {:predicates [:current-character-is-storyteller :storytelling-ending]
    :result [[:output "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n"]
             [:pop-event true]
             [:add-event {:tags {:storytelling-beginning true :singular-selection true}}]
             ;[:exit :outward]
             ]})
   (make-storyon
   {:predicates [:current-character-is-storyteller :storytelling-ready-to-tell]
    :result [[:output "And she told them a story. "]
             [:pop-event true]
             [:add-event {:tags {:storytelling-ending true :singular-selection true}}]
             ;[] ;todo: generate new story and exit into it.
             ]})
   (make-storyon
   {:predicates [(fn [_] false)]
    :result [[:output "And then debug text was printed. "]]})

   ])




(def storytelling-actions
  [{:predicates {:event #(= % :event-storytelling-begin)
                 :character-storyteller true?}
    :outcome {:text "So she said, \"It is related, O august king, that...\" "
              :events [:event-storytelling-telling]}
    }
   {:predicates {:event #(= % :event-storytelling-end)
                 :character-storyteller true?}
    :outcome {:text "Then she ended, saying, \"But there is another tale which is more marvelous still.\"\n"
               :events [:event-storytelling-begin]}
    }
   {:predicates {:event #(= % :event-storytelling-telling)
                 :character-storyteller true?}
    :outcome {:text "And she told them a story. "
               :events [:event-storytelling-end]}
    }])
