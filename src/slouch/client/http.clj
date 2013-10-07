(ns slouch.client.http
  (:refer-clojure :exclude [send])
  (:require [http.async.client]))

(defprotocol HttpClient
  (send [this url body])
  (close [this]))

(deftype DefaultHttpClient [client]
  slouch.client.http/HttpClient
  (send [this url body]
    (let [response (-> (http.async.client/POST client url :body body)
                       http.async.client/await
                       (update-in ,,, [:status] deref)
                       (update-in ,,, [:body] deref))]
      [(get-in response [:status :code])
       (get-in response [:body])]))
  (close [this]
    (http.async.client/close client)))

(defn new-client []
  (DefaultHttpClient. (http.async.client/create-client)))
