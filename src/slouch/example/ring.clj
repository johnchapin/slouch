(ns slouch.example.ring
  (:require [slouch.example.api]
            [slouch.server]))

(def app
  (slouch.server/handler 'slouch.example.api))
