(ns slouch.client
  (:require [slouch.common :as common]
            [slouch.client.http :as http]
            [slouch.serialization :as serial]))

(defn- handle-result
  [[code body]]
  (let [return (serial/deserialize body)]
    (condp = code
      (:success common/response-codes)
      return

      (:not-found common/response-codes)
      (throw (Exception. (format "Function '%s' not found" return)))

      (:exception common/response-codes)
      (throw return))))

(defprotocol SlouchClientProtocol
  (invoke [this ns-name fn-name async args])
  (close [this]))

(deftype SlouchClient [http-client conn-str]
  SlouchClientProtocol
  (invoke [this ns-name fn-name async args]
    (let [uri (str ns-name "/" fn-name)
          url (str conn-str "/" uri)
          body (serial/serialize args)]
      (if async
        (.send-async http-client url body handle-result)
        (-> (.send http-client url body)
            handle-result))))
  (close [this]
    (.close http-client)))

(defn new-client [conn-str & {:keys [http-client]
                              :or {http-client (http/new-client)}}]
  (SlouchClient. http-client conn-str))

(defmacro defn-remote
  [client fn-name & {:keys [remote-ns remote-name async]
                     :or {remote-ns (ns-name *ns*)
                          async false}}]
  (let [facade-sym (symbol fn-name)
        remote-name (or remote-name (str fn-name))]
    `(def ~facade-sym
       (fn [& args#]
         (invoke ~client ~remote-ns ~remote-name ~async args#)))))
