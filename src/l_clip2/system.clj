(ns l-clip2.system
  (:require [l-clip2.util :refer [read-edn]]))

(defn system-config [profile]
  (-> (str "system/" (name profile) ".edn")
      read-edn))
