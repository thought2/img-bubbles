(ns img-bubbles.net
  (:require [ajax.core :refer [ajax-request
                               transit-response-format
                               transit-request-format]]
            [promesa.core :as p]))

(def defaults
  {:method :post
   :format (transit-request-format)
   :response-format (transit-response-format)})

(defn ajax [opts]
  (p/promise
   (fn [resolve reject]
     (let [handler (fn [[ok response]]
                     ((if ok resolve reject)
                      response))]
       (ajax.core/ajax-request (merge {:handler handler} defaults opts))))))
