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

(defn make-a-storytelling-story [seed]
  (nonogen.stories.story/make-storytelling-story example-storyons seed))

(defn make-a-labyrinth-story [seed]
  (nonogen.stories.story/make-labyrinth-story labyrinth-storyons seed))

;(make-story nil)

(def example-storyons
  [(nonogen.stories.storyon/make-storyon
   {:predicates [:at-least-one-character [:event :story-introduction] [:is-tag :reality-prime]]
    :result [[:output "# Ten Stories by Scheherazade \n\n"]
             [:output "Once upon a time, there was " ]
             [:output describe-all-characters ". "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :pick-next-scene :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:at-least-one-character [:event :story-introduction] [:not-tag :reality-prime]]
    :result [[:output "\n## " count-nested-hashes ": " narrator-name "'s " adjective " story\n\n"]
             [:output "Once upon a time, there was " ]
             [:output describe-all-characters ". "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :pick-next-scene :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :pick-next-scene] :current-character-is-storyteller]
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
             [:exit-inward make-a-storytelling-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "\n\nThis is the story that " nonogen.stories.output/storyteller-name " told:\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-a-labyrinth-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell] [:not-tag :reality-prime]]
    :result [[:output "And " storyteller-name " told a very " adjective " story. "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
  ; (nonogen.stories.storyon/make-storyon
  ;  {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
  ;  :result [[:output "Then " storyteller-name " opened " her " book and read them the following page:\n\n"]
  ;           [:exit-inward (fn [_] (nonogen.borges.babel/make-babel))]
  ;           [:pop-event true]
  ;           [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
  ;           ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending] [:not-tag :reality-prime]]
    :result [[:output "\"And that was how it happened,\" " storyteller-name " said, ending " her " story. "]
             [:output "\n\n---\n\n"]
             [:pop-event true]
             [:exit :outward]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending]]
    :result [[:quality :increment :state :nights]
             [:output "Thus " storyteller-name " ended " her " " nights-count " story, saying, \"But there is another tale which is more marvelous still.\"\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             [:exit :inplace]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ending] :nights-quality-at-least-1001]
    :result [[:quality :increment :state :nights]
             [:output "And so " storyteller-name " ended " her " last story, saying, \"And that is my final tale.\"\n\n\n\n#THE END\n\n\n\n"]

             [:pop-event true]
             [:exit :outward]
             ]})
   ])




(def labyrinth-storyons
  [(nonogen.stories.storyon/make-storyon
    {:predicates [:at-least-one-character [:event :labyrinth-introduction] [:is-tag :labyrinth] :current-character-is-explorer]
     :result [[:output "\n## " count-nested-hashes ": " narrator-name "'s Story About " current-character-name "\n\n"
                      "There was once " labyrinth/introduce-labyrinth]
              ;[:output "And in that place there was also " describe-all-characters ". " ]
              [:pop-event true]
              [:add-event (story/make-event {:tags {:event :introduce-labyrinth-character :singular-selection true}})]
              ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :introduce-labyrinth-character] [:is-tag :labyrinth] :current-character-is-explorer]
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
   {:predicates [[:event :wander-labyrinth] [:is-tag :labyrinth] :current-character-is-explorer]
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
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})

         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
       (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
      (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:output "There was a book here, and " she " opened it and read the following page:\n\n"]
             [:exit-inward (fn [_] (nonogen.borges.babel/make-babel))]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :close-book :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :close-book] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\"Well,\" " she " said, \""
              (fn []
                (fn [story]
                       (apply str
                              (nonogen.random/variation
                               ["That explains a lot"
                                "That was quite useless"
                                "It is as confusing as this maze"
                                "Maybe it's a clue to where the exit is"
                                "I have an unsettling feeling of déjà vu"
                                "Perhaps it's a list of names? Or a crossword puzzle that was filled in wrong"
                                "Somehow, it reminds me of tigers"
                                ]
                               (get-in story [:state :seed]))
                              (nonogen.random/variation [". I hope that it's not important, because I can't read it"
                                                              "" "" "" "" "" "" ""
                                                              ", or maybe it was written upside down"
                                                              ". Perhaps there's a code"
                                                              ". Maybe it's in a language I don't know"
                                                              ". Perhaps the book is as infinite and inscrutable as the rest of this place"]
                               (get-in story [:state :seed])))))
              ".\"\n\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :wander-labyrinth :singular-selection true}})]
             ]})
         (nonogen.stories.storyon/make-storyon
   {:predicates [[:event :new-place] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " entered " (fn [] labyrinth/describe-inside-labyrinth)]
             [:add-random-character nil]
             [:output "And that was where the encounter between " describe-all-characters " took place. "]
             [:add-scene nonogen.stories.story/make-storytelling-scene]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :intro-storytelling :singular-selection true}})]
             ]})
  (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :intro-storytelling]]
    :result [[:output storyteller-name " offered advice to " non-storyteller-name " in the form of a story. " ]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-beginning :singular-selection true}})]
             ]})

   (nonogen.stories.storyon/make-storyon
    {:predicates [[:event :encounter-over] [:is-tag :labyrinth]  [:not-tag :labyrinth-exit] :current-character-is-explorer]
    :result [[:output "\n\n" She " decided to travel onwards. "]
             [:remove-scene-characters nil]
             [:remove-scene nil]
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
              ". "]
             [:output "\n\n---\n\n"]
             [:pop-event true]
             [:exit :outward]
             ]})


   (nonogen.stories.storyon/make-storyon
   {:predicates [:not-current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output She "begged " storyteller-name " to tell the story. "]
             [:quality :increment :scene :anticipation]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-beginning]]
    :result [[:output "So " storyteller-name " began, \"It seems to me that this place we find ourselves reminds me of when...\" "]
             [:pop-event true]
             [:quality :erase :scene :anticipation]
             [:add-event (story/make-event {:tags {:event :storytelling-ready-to-tell :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "\n\nAnd " she " told the following story:\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-a-storytelling-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "\n\nAnd " she " told the following story:\n"]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             [:exit-inward make-a-labyrinth-story]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-storyteller [:event :storytelling-ready-to-tell]]
    :result [[:output "And " storyteller-name " told a very exciting story. "]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :storytelling-ending :singular-selection true}})]
             ]})
   (nonogen.stories.storyon/make-storyon
   {:predicates [:current-character-is-explorer [:event :storytelling-ending] [:not-tag :reality-prime]]
    :result [[:output "\"So you see how that story was very like this place,\" " storyteller-name " said, ending the story. "]
             [:remove-scene-characters nil]
             [:pop-event true]
             [:add-event (story/make-event {:tags {:event :encounter-over :singular-selection true}})]
             ]})
   ])
