(ns example.client
  (:require [slouch.client]))

(def slouch-client (slouch.client/new "http://127.0.0.1:3000"))

(slouch.client/defn-remote slouch-client product :remote-ns "example.api")
(slouch.client/defn-remote slouch-client stats-meta :remote-ns "example.api")
