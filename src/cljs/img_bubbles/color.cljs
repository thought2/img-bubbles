(ns img-bubbles.color)

(defn prime [color]
  (mapv (comp Math/round
              #(* 255 %)) color))

(defn rgb-str [rgb]
  (let [[r g b] (prime rgb)]
    (str "rgb(" r "," g "," b ")")))


(defn rgba-str [rgba]
  (let [[r g b a] rgba
        [r g b] (prime [r g b])]
    (str "rgba(" r "," g "," b "," a ")")))
