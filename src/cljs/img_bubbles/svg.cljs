(ns img-bubbles.svg
  (:require
   [img-bubbles.color :refer [rgba-str]]
   [clojure.core.matrix :refer [add sub mul div]]))

(defn wrap-opt [f]
  (fn [nxt & args]
    (fn [& [opt more]]
      (nxt (f opt args) more))))

(defn tag [t]
  (fn [& [opt more]]
    [t opt more]))

(def explode-vec
  (wrap-opt
   (fn [opt [k ks]]
     (let [v (k opt)]
       (-> (merge opt (zipmap ks v))
           (dissoc k))))))

(def update-attr
  (wrap-opt
   (fn [opt [k f]]
     (update opt k f))))

(def inherit
  (wrap-opt
   (fn [opt [std]]
     (merge opt std))))

#_(def style
    (wrap-opt
     (fn [opt]
       )))

;; api

(def Circle
  (-> (tag :circle)
      (explode-vec :pos [:cx :cy])))

(def Image
  (-> (tag :image)
      (explode-vec :pos [:x :y])
      (explode-vec :size [:height :width])))

(def LinearGradient
  (-> (tag :linearGradient)
      (explode-vec :pos1 [:x1 :y1])
      (explode-vec :pos2 [:x2 :y2])))

(def Stop
  (-> (tag :stop)
      (update-attr :stop-color rgba-str)))

(def Pattern
  (-> (tag :pattern)
      (explode-vec :size [:height :width])))

(def CenterCircle
  (let [move
        (wrap-opt
         (fn [opt]
           (let [r (-> opt :r (/ 2))]
             (update opt :pos #(sub % r)))))]
    (-> Circle
        move)))
