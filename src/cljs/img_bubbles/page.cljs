(ns img-bubbles.page
  (:require
   [clojure.core.matrix :refer [add sub mul div]]
   [garden.core :refer [css]]
   [img-bubbles.svg :as svg]
   [img-bubbles.net :as net]
   [reagent.core :as r] 
   [promesa.core :as p]))

(defonce state (r/atom {}))

(def newspapers
  ["http://www.nytimes.com/"
   "http://www.cnn.com/"
   "http://www.foxnews.com/"
   "http://www.nbcnews.com/"
   "http://www.washingtonpost.com/"
   "http://www.theguardian.com/"
   "http://www.abcnews.go.com/"])

(defn init-state []
  {:points (let [rand-pos #(vector (rand) (rand))]
             (repeatedly 20 rand-pos))})

(defn start! []
  (reset! state (init-state)))

(defn load [url]
  (-> (net/ajax {:uri "/imgs"
                 :params {:url url}})
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

(defn Link [url]
  (let [on (r/atom false)
        switch #(swap! on not)]
    (fn []
      [:a {:style {:font-family "Monospace"
                   :letter-spacing "2px"
                   :color (if @on "#e0cd23" "grey")
                   :text-decoration :underline 
                   :cursor :pointer}
           :on-click #(load url)
           :on-mouse-enter switch
           :on-mouse-leave switch} 
       url])))

(defn Urls []
  (let [rand-n #(- (rand-int 100) 10)
        poss (repeatedly #(-> [(rand-n) (rand-n)]))]
    [:div {:style {:position :fixed
                   :top 0 :left 0 :right 0 :bottom 0}}
     (map (fn [url [w h]]
            [:span {:style {:position :absolute
                            :left (str w "%")
                            :top (str h "%")}}
             [Link url]])
          newspapers poss)]))

(defn Page []
  (let [style (css)]
    #_(load)
    (fn []
      
      [:div.max {:style {:overflow :hidden}}
       [Style style]
       #_[Log state]
       [:svg.max {:viewBox "0 0 1 1"
                  :preserveAspectRatio "none"}
        [:defs
         [Gradient]]
        [Circles]]
       [Urls]])))

