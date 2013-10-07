(ns example.server
  (:require [example.api :as api]
            [slouch.server]))

(def app
  (slouch.server/handler 'example.api))
