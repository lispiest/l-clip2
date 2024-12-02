(ns l-clip2.util
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]))

(defn read-edn [path]
  (-> path
      io/resource
      slurp
      edn/read-string))
