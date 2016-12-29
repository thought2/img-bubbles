(ns img-bubbles.core
  (:require
   [img-bubbles.page :refer [Page start!]]
   [clojure.core.matrix :refer [add sub mul div]]
   [garden.core :refer [css]]
   [img-bubbles.svg :as svg]
   [img-bubbles.dom :as dom]
   [img-bubbles.net :as net]
   [reagent.core :as r] 
   [cognitect.transit :as t]
   [promesa.core :as p]))

(defonce app-state (r/atom nil))

(def style
  (css
   [:.max {:width "100%"
           :height "100%"}]))

(defn Style [s]
  [:style s])

(defn main []
  (r/render [Style style] (dom/by-id "style"))
  (r/render [Page] (dom/by-id "app")))

(defn init []
  (start!)
  (main))


