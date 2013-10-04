(ns slouch.client.http
   (:refer-clojure :exclude [send]))

(defprotocol HttpClient
  (send [this url body])
  (close [this]))
