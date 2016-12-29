(ns img-bubbles.impl
  (:require
   [slingshot.slingshot :refer [throw+ try+]]
   [clojure.string :as s]
   [net.cgrand.enlive-html :as html]
   [org.httpkit.client :as http]
   [cemerick.url :as u]))

(defmacro save [& forms]
  `(try ~@forms
        (catch Throwable e#)))

(defn imgs* [url]
  (let [ok
        (fn [res]
          (let [body (:body res)
                url (-> res :opts :url) 
                nodes (-> (html/html-snippet body)
                          (html/select [:img]))] 
            (->> (map (comp :src :attrs) nodes)
                 (remove #(or (nil? %)
                              (s/starts-with? % "data:")))
                 (keep #(if (re-find #"^https?://" %)
                          %
                          (-> (u/url url %) u/url-decode save))))))
        get
        (fn [url]
          (let [r @(http/get url)]
            (if (:error r)
              (throw+ {:error-type :bad-connection})
              r)))]
    (ok (get url))))

(defn imgs [url]
  (try+ {:msg-type :ok
         :result (imgs* url)}
        (catch #(not (:error-type %)) _
          (throw+ {:error-type :bad-process}))))
