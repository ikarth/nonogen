(ns nonogen.stories.storyon-library
   (:require [nonogen.stories.storyon :refer :all]
             [nonogen.stories.story :as story]
             [nonogen.borges.babel]
             [nonogen.stories.output  :refer :all]
             [nonogen.stories.labyrinth :as labyrinth]
             [nonogen.random]
             ))


;;;
;;; Storyon Library
;;;


(declare make-story)
(declare example-storyons)
(declare labyrinth-storyons)

(defn make-story [seed]
  (nonogen.stories.story/make-storytelling-story example-storyons seed))

(defn make-labyrinth-story [seed]
  (nonogen.stories.story/make-labyrinth-story labyrinth-storyons seed))

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
    :result [[:output "\n\nThis is the story that " nonogen.stories.output/storyteller-name " told:\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "\n\nThis is the story that " nonogen.stories.output/storyteller-name " told:\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-labyrinth-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "And " storyteller-name " told a very exciting story. "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "Then " storyteller-name " opened " her " book and read them the following page:\n\n"]
             [:exit-inward (fn [_] (nonogen.borges.babel/make-babel))]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
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




(def labyrinth-storyons
  [(nonogen.stories.storyon/make-storyon
    {:predicates [:at-least-one-character [:event :labyrinth-introduction] [:is-tag :labyrinth]]
     :result [[:output "There was once " labyrinth/introduce-labyrinth]
              ;[:output "And in that place there was also " describe-all-characters ". " ]
              [:pop-event true]
              [:add-event (story/make-event {:tags {:event :introduce-labyrinth-character :singular-selection true}})]
              ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :introduce-labyrinth-character] [:is-tag :labyrinth]]
    :result [[:output current-character-name " "]
             [:output
              (fn []  (fn [story] (apply str (nonogen.random/variation ["didn't know why "
                                                                "was almost certain about why "
                                                                "couldn't quite say how "
                                                                "must have gotten lost, because "
                                                                "had followed a secret path, and so "
                                                                "must have spoken the unutterable word, because "
                                                                "was lost, like so many before and after, and "
                                                                "wasn't quite sure where this was, only that "]
                                                               (get-in story [:state :seed])))))
              she " "
              (fn [] (fn [story] (apply str (nonogen.random/variation ["happened to be there. " "was wandering there. " "had arrived in that place. " "had come to that place, as we all eventually must. "]
                                                               (get-in story [:state :seed])))))
              ]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
      (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :wander-labyrinth] [:is-tag :labyrinth]]
    :result [[:output current-character-name " "
              (fn []
                (fn [story]
                       (apply str
                              (nonogen.random/variation
                               ["walked away from that place"
                                "wandered, lost in thought"
                                "discovered that one of the doors lead somewhere else"
                                "chose an exit at random and walked that way"
                                "felt sure that this must be the way out"
                                "thought that this direction looked promising, and went that way"
                                "muttered, \"North, this way is probably north!\" as the door opened"
                                "felt a bit dizzy at the confusion of doors"
                                "opened a door, not feeling quite sure where it lead"]
                               (get-in story [:state :seed]))
                              (nonogen.random/variation [", not knowing that it was indeed the wrong way"
                                                              "" "" "" "" "" "" ""
                                                              ", passing a reflection in a mirror"
                                                              ", listening to the echo of footsteps"
                                                              ", sparing a passing glance at a mirror"
                                                              ", humming a little to relieve the silence"]
                               (get-in story [:state :seed])))))
              ". "]
             [:run-labyrinth true]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :new-place :singular-selection true}})]
             ]})
      (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit]]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
      (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth] [:is-tag :labyrinth-exit]]
    :result [[:output
              (fn []
                (fn [story]
                       (apply str
                              (nonogen.random/variation
                               ["And there "
                                "Which was where "
                                "Quite unexpectedly "
                                "At the darkest hour "
                                "Almost unable to believe it, "
                                ]
                               (get-in story [:state :seed])))))
                              current-character-name
              (fn []
                (fn [story]
                       (apply str
                              (nonogen.random/variation [" found the exit" " discovered the way out" " reached the end of the labyrinth"]
                               (get-in story [:state :seed])))))
              ".\n\n"]
             [:pop-event true]
             [:exit :outward]
             ]})
   ])
