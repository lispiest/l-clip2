(ns l-clip2.core
  (:require [juxt.clip.core :as clip]
            [l-clip2.system :refer [system-config]]))

(defn -main []
  (clip/start (system-config :dev))
  (println "Starting up on port 8090")
  @(promise))
