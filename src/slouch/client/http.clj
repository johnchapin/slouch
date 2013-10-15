(ns slouch.client.http
  (:refer-clojure :exclude [send])
  (:require [http.async.client]))

(defn- realize-response [response]
  (-> response
      (update-in ,,, [:status] deref)
      (update-in ,,, [:body] deref)))

(defn- response->result [response]
  (let [response* (realize-response response)]
    [(get-in response* [:status :code])
     (get-in response* [:body])]))

(defprotocol HttpClient
  (send [this url body])
  (close [this]))

(defprotocol AsyncHttpClient
  (send-async [this url body response-fn])
  (close [this]))

(deftype DefaultHttpClient [client]
  slouch.client.http/HttpClient
  (send [this url body]
    (-> (http.async.client/POST client url :body body)
        http.async.client/await
        response->result))
  (close [this]
    (http.async.client/close client))
  slouch.client.http/AsyncHttpClient
  (send-async [this url body result-fn]
    (let [request (http.async.client.request/prepare-request :post url :body body)
          result (promise)]
      (http.async.client.request/execute-request
        client request
        :completed #(deliver result (delay (result-fn (response->result %)))))
      result)))

(defn new-client []
  (DefaultHttpClient. (http.async.client/create-client)))
