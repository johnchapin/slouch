(ns slouch.client.http
  (:refer-clojure :exclude [new send])
  (:require [http.async.client :as httpa]))

(defprotocol HttpClient
  (send [this url body])
  (collect [this response])
  (close [this]))

;; DefaultHttpClient implementation

(defn- realize-response [response]
  (-> response
      (update-in ,,, [:status] deref)
      (update-in ,,, [:body] deref)))

(defn- response->result [response]
  (let [response* (realize-response response)]
    [(get-in response* [:status :code])
     (get-in response* [:body])]))

(deftype DefaultHttpClient [client]
  HttpClient
  (send [this url body]
    (httpa/POST client url :body body))
  (collect [this response]
    (httpa/await response)
    (if (httpa/failed? response)
      (throw (RuntimeException. (httpa/error response)))
      (response->result response)))
  (close [this]
    (httpa/close client)))

(defn new []
  (DefaultHttpClient. (httpa/create-client)))
