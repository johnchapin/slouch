(ns slouch.unit.serialization
  (:require [slouch.serialization :as serial]
            [midje.sweet :refer :all]))

(fact
  "Round-trip throwable"
  (let [e (Throwable. "test")
        e* (serial/deserialize (serial/serialize e))]
    (.getCause e) => (.getCause e*)
    (.getMessage e) => (.getMessage e*)))
