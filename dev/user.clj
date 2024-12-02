(ns user
  (:require [juxt.clip.repl :refer [start stop reset set-init! system]]
            [l-clip2.system :refer [system-config]]))

(set-init! #(system-config :dev))

(comment
  (start)
  system
  (stop)
  system
  (reset)
  system)
