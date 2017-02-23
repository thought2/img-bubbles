(ns img-bubbles.page
  (:require
   [clojure.core.matrix :refer [add sub mul div]]
   [garden.core :refer [css]]
   [img-bubbles.svg :as svg]
   [img-bubbles.net :as net]
   [reagent.core :as r] 
   [promesa.core :as p]))

(defonce state (r/atom {}))

(defn init-state []
  {:points (let [rand-pos #(vector (rand) (rand))]
             (repeatedly 20 rand-pos))})

(defn start! []
  (reset! state (init-state)))

(defn load []
  (-> (net/ajax {:uri "/imgs"
                 :params {:url "http://taz.de"}})
      (p/then #(swap! state assoc :result %))
      (p/catch #(.log js/console "error" %))))

(defn Log [state]
  [:div {:style {:color :white
                 :position :fixed}}
   (prn-str @state)])

(defn Style [s]
  [:style s])

(defn Gradient [] 
  (let [colors [[0.7 0.89 0.67 1] [1 1 1 0]]
        f (fn [i c] [svg/Stop {:offset i :stop-color c}])]
    [svg/LinearGradient {:id "gradient" :pos1 [0 0] :pos2 [1 1]}
     (map-indexed f colors)]))

(def Circle
  (svg/inherit
   svg/CenterCircle
   {:r 0.05}))

(def Pattern
  (svg/inherit
   svg/Pattern
   {:size [1 1]
    :patternContentUnits "objectBoundingBox"}))

(def Image
  (svg/inherit
   svg/Image
   {:pos [0 0] :size [1 1] 
    :preserveAspectRatio "none"}))

(defn CircleWithPattern [{:keys [pos url opacity]}]
  (let [id (str (random-uuid))
        mk-url #(str "url(#" % ")")]
    [:g {:style {:cursor :pointer
                 :opacity opacity}
         :on-click #(js/alert "h")}
     [Pattern {:id id}
      [Image {:xlinkHref url}]] 
     [Circle {:pos pos
              :style {:fill (mk-url id)}}]
     [Circle {:pos pos
              :style {:fill (mk-url "gradient")}}]]))

(defn Circles [] 
  [:g
   (map
    (fn [i pos url]
      ^{:key i} [CircleWithPattern {:pos pos :url url :opacity (->> @state :points count (/ i) )}])
    (range)
    (:points @state)
    (-> @state :result :result))]) 

(defn Page []
  (let [style (css)]
    (load)
    (fn []
      
      [:div.max {:style {:overflow :hidden}}
       [Style style]
       #_[Log state]
       [:svg.max {:viewBox "0 0 1 1"
                  :preserveAspectRatio "none"}
        [:defs
         [Gradient]]
        [Circles]]])))

