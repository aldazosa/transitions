(ns transitions.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [transitions.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[transitions started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[transitions has shut down successfully]=-"))
   :middleware wrap-dev})
