(def +version+ "0.1.0-SNAPSHOT")
(def +name+ 'img-bubbles)

(defn mk-ns-symbol [xs & [x]]
  (symbol (str (clojure.string/join "." xs)
               (when x (str "/" x)))))

(def both       '[[com.cemerick/url                     "0.1.1"]
                  [ring-transit                         "0.1.6"] 
                  [org.clojure/test.check               "0.9.0"]
                  [org.clojure/math.combinatorics       "0.1.3"]
                  [org.clojure/clojure                  "1.8.0"] 
                  [org.clojure/core.match               "0.3.0-alpha4"]
                  [net.mikera/core.matrix               "0.54.0"]
                  [org.clojure/core.async               "0.2.385"]
                  [adzerk/boot-reload                   "0.4.12"                :scope "test"]
                  [com.cemerick/pomegranate             "0.3.1"                 :scope "test"]
                  [edw/pomjars                          "0.1.0"                 :scope "test"] 
                  ;;[thought2/boot-misc                   "0.3.0-SNAPSHOT"        :scope "test"]
                  [pandeiro/boot-http                   "0.7.3"                 :scope "test"]])

(def back       '[[thought2/boot2nix             "0.1.0-SNAPSHOT"]
                  [slingshot                     "0.12.2"]
                  [com.cognitect/transit-clj     "0.8.297"]
                  [ring-transit                  "0.1.6"] 
                  #_[thought2/misc                 "0.1.0-SNAPSHOT"]
                  [fivetonine/collage            "0.2.1"]
                  [org.clojure/java.jdbc         "0.4.2"]
                  [mysql/mysql-connector-java    "6.0.4"]
                  [clj-http                      "2.2.0"]
                  [ring/ring-defaults            "0.2.1"]
                  [ring                          "1.5.0"]
                  [http-kit                      "2.1.18"]
                  [compojure                     "1.5.1"]
                  [enlive                        "1.1.6"]])

(def front      '[[markdown-clj                  "0.9.89"]
                  [enfocus                       "2.1.1"]
                  #_[thought2/web-misc             "0.1.0"]
                  [cljs-ajax                     "0.5.8"]
                  [garden                        "1.3.2"]
                  [funcool/promesa                      "1.5.0"]
                  [reagent                       "0.6.0-rc"] 
                  ;;[org.clojure/clojurescript     "1.8.51"]
                  [org.clojure/clojurescript     "1.9.229"]
                  [org.martinklepsch/boot-garden "1.3.2-0"]
                  [deraen/boot-less              "0.5.0"                 :scope "test"] 
                  [adzerk/boot-cljs              "1.7.170-3"             :scope "test"]
                  [pandeiro/boot-http            "0.7.3"                 :scope "test"] 
                  [adzerk/boot-cljs-repl         "0.3.0"                 :scope "test"]
                  [com.cemerick/piggieback       "0.2.1"                 :scope "test"]
                  [weasel                        "0.7.0"                 :scope "test"]
                  [org.clojure/tools.nrepl       "0.2.12"                :scope "test"]])

(set-env!
 :source-paths #{"src/clj" "src/cljs"} 
 :resource-paths #{"resources"}
 :dependencies (concat both back front))

(require         '[thought2.boot2nix             :refer [boot2nix]]
                 '[pomjars.core                  :as pj]
                 ;;                 '[thought2.boot-misc            :refer [boot-misc! push-webdir push-uber mk-ns-symbol]] 
                 '[pandeiro.boot-http            :refer [serve]]
                 '[clojure.repl                  :refer [doc]]
                 '[clojure.pprint                :refer [pprint]])

(require         '[net.cgrand.enlive-html        :as html]
                 ;;'[clj-http.client               :as client]
                 '[org.httpkit.client :as client]
                 )

(require         '[deraen.boot-less              :refer [less]] 
                 '[adzerk.boot-cljs              :refer [cljs]] 
                 '[adzerk.boot-reload            :refer [reload]]
                 '[adzerk.boot-cljs-repl         :refer [cljs-repl start-repl]] 
                 '[org.martinklepsch.boot-garden :refer [garden]] )

;;(boot-misc! +name+ +version+)

(let [main-ns (mk-ns-symbol [+name+ 'start])]
  (task-options!
   pom    {:project     +name+
           :version     +version+
           :description ""
           :license     {"The MIT License (MIT)"
                         "http://opensource.org/licenses/mit-license.php"}}
   garden {:styles-var  (mk-ns-symbol [+name+ 'styles] 'base)
           :output-to   "css/styles.css"}
   repl   {:eval '(set! *print-length* 20)}
   jar    {:main main-ns}
   aot    {:namespace #{main-ns}}))

(deftask dev []
  (comp
   (serve
    :handler    (mk-ns-symbol [+name+ 'server] 'handler)
    :reload     true
    :port       3000)
   (watch)
   (cljs-repl)
   (reload :on-jsload 'img-bubbles.core/main)
   (cljs :optimizations :none
         :source-map true)
   (garden :styles-var (mk-ns-symbol [+name+ 'styles] 'base)
           :output-to "css/styles.css")
   (target)))

(deftask build []
  (comp
   (cljs :optimizations :advanced)
   (garden)
   (aot)
   (pom)
   (uber)
   (jar)))


