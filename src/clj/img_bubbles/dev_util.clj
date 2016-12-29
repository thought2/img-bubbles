(ns img-bubbles.dev-util
  (:require [cognitect.transit :as transit]
            [org.httpkit.client :as http]
            #_[ring.middleware.transit :refer [wrap-transit-params wrap-transit-response]])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))


(defn wrap-transit-body-client [handler]
  (let [opts {}
        write
        (fn [x]
          (let [baos (ByteArrayOutputStream.)
                w    (transit/writer baos :json opts)
                _    (transit/write w x)
                ret  (.toString baos)]
            (.reset baos)
            ret))]
    (fn [request]
      (-> (update request :body write)
          handler))))

(defn wrap-transit-response-client [handler]
  (let [opts {}
        read
        (fn [x]
          (transit/read (transit/reader x :json)))]
    (fn [request]
      (-> (handler request)
          (update :body read)))))

(def http-t* (-> (fn [req] @(http/request req)) 
                 wrap-transit-response-client
                 wrap-transit-body-client))

(defn http-t [url & [opts]]
  (let [defaults {:url url
                  :method :post
                  :as :stream
                  :headers {"Content-Type" "application/transit+json"}}]
    (http-t* (merge defaults opts))))
