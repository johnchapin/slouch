(ns slouch.client.http.default
  (:require [slouch.client.http]
            [http.async.client]))

(deftype DefaultHttpClient [client]
  slouch.client.http/HttpClient
  (send [this url body]
    (let [response (-> (http.async.client/POST
                         client
                         url
                         :body body
                         http.async.client/await
                         (update-in ,,, [:status] deref)
                         (update-in ,,, [:body] deref)))]
      [(get-in response [:status :code])
       (get-in response [:body])]))
  (close [this]
    (http.async.client/close client)))

(defn new-client []
  (HttpAsyncClient. (http.async.client/create-client)))
