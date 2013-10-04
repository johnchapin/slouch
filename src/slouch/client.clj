(ns slouch.client
  (:require [slouch.common :as common]
            [slouch.serialization :as serial]))

(defn- handle-response
  [[code body]]
  (let [result (serial/deserialize body)]
    (condp = code
      (:success common/response-codes)
      result

      (:not-found common/response-codes)
      (throw (Exception. (format "Function '%s' not found" result)))

      (:exception common/response-codes)
      (throw result))))

(defprotocol SlouchClientProtocol
  (invoke [this ns-name fn-name args])
  (close [this]))

(deftype SlouchClient [http-client conn-str]
  SlouchClientProtocol
  (invoke [this ns-name fn-name args]
    (let [uri (str ns-name "/" fn-name)
          url (str conn-str "/" uri)
          body (serial/serialize args)]
      (-> (.send http-client url body)
          handle-response)))
  (close [this]
    (.close http-client)))

(defn new-client [http-client conn-str]
  (SlouchClient. http-client conn-str))

(defmacro defn-remote
  [client fn-name & {:keys [remote-ns remote-name]
                     :or {remote-ns (ns-name *ns*)}}]
  (let [facade-sym (symbol fn-name)
        remote-name (or remote-name (str fn-name))]
    `(def ~facade-sym
       (fn [& args#]
         (invoke ~client ~remote-ns ~remote-name args#)))))
