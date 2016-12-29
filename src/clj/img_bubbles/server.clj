(ns img-bubbles.server
  (:require
   [img-bubbles.impl :as i]
   [slingshot.slingshot :refer [throw+ try+]]
   [compojure.core :refer :all]
   [compojure.route :as route]
   [clojure.java.io :as io] 
   [ring.middleware.transit :refer [wrap-transit-params wrap-transit-response]]
   [ring.util.response :refer [response not-found]]))

(defroutes main-routes
  (GET "/" _ (io/resource "index.html"))
  (POST "/imgs" [url] (do
                        (prn url)
                        (response (i/imgs url))))
  (route/resources "/" {:root ""}))

(defn wrap-exception-handling
  [handler]
  (fn [request]
    (try+
     (handler request)
     (catch :error-type x
       (response (merge x {:msg-type :error})))
     (catch  Object _
       (response {:msg-type :error
                  :error-type :unknown})))))

(def handler
  (-> main-routes
      wrap-exception-handling
      wrap-transit-params
      wrap-transit-response)) 

