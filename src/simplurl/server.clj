;; Here we define the routing for the server, as well as provide the main function.
(ns simplurl.server
  (:require [ring.adapter.jetty :as rjetty]
            [ring.util.response :as resp]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes GET PUT]]
            [compojure.route :as route]
            [simplurl.core :as core]))

;; ## Initialization
;; To set up the database when the server is started, we can provide an init function.

(defn init
  "Initialize the database for the app."
  []
  (core/create))

;; ## Routing
;; Lets use Compojure to set up the URL routes to the functions in simplurl.core.

(defroutes routes
  "This wraps up all the routes needed for the server."
  (GET "/:k" [k]
    (if-let [url (core/expand k)]
      (resp/redirect (.toString url))
      (resp/not-found "No.")))
  (PUT "/:k" [k url]
    (if (core/add k url)
      (resp/created (str "/" k))))
  (route/not-found "No."))

;; Handler
;; We need some Ring middleware to handle params.

(def simplurl
  "The app."
  (-> routes
      (wrap-params)))

;; ## Main

(defn -main
  "Starts a simplurl server."
  [& [port]]
  (let [p (Integer. (or port 3000))]
    (rjetty/run-jetty simplurl {:port p :join? false})))

