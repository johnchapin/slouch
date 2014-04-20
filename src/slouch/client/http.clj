(ns slouch.client.http
  (:refer-clojure :exclude [new send])
  (:require [org.httpkit.client :as http]))

(defprotocol HttpClient
  (send [this url body])
  (collect [this response])
  (close [this]))

;; DefaultHttpClient implementation

(deftype DefaultHttpClient [conn-str]
  HttpClient
  (send [this uri body]
    (let [url (str conn-str "/" uri)]
      (http/post url {:body body :as :stream})))
  (collect [this response]
    ;; TODO: Deref w/ timeout instead?
    (let [{:keys [status headers body error]} @response]
      (if error
        (throw (RuntimeException. error))
        [status body])))
  (close [this]))

(defn new [conn-str]
  (DefaultHttpClient. conn-str))
