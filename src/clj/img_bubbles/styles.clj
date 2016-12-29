(ns img-bubbles.styles
  (:require [garden.def :refer [defrule defstyles]]
            [garden.stylesheet :refer [rule]]))

(defstyles base
  [:body :html :#app {:height "100%"}]
  [:* {:box-sizing :border-box}]
  [:body {:background-color :black}])
