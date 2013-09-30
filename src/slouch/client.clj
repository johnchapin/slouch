(ns slouch.client
  (:require [http.async.client :as http]
            [slouch.common :as common]
            [slouch.serialization :as serial]))

(defn- handle-cookies
  [{headers :headers :as response} cookies]
  (when (contains? headers :set-cookie)
    (reset! cookies (http/cookies response)))
  response)

(defn- handle-response
  [{{code :code} :status body :body :as response}]
  (let [result (serial/deserialize body)]
    (condp = code
      (:success common/response-codes)
      result

      (:not-found common/response-codes)
      (throw (Exception. "Function '%s' not found" result))

      (:exception common/response-codes)
      (throw result))))

(defprotocol SlouchClientProtocol
  (invoke [this ns-name fn-name args])
  (close [this]))

(deftype SlouchClient [http-client cookies conn-str]
  SlouchClientProtocol
  (invoke [this ns-name fn-name args]
    (let [url (str conn-str "/" ns-name "/" fn-name)
          body (serial/serialize args)]
      (-> (http/POST http-client url :body body :cookies @cookies)
          http/await
          (update-in ,,, [:status] deref)
          (update-in ,,, [:headers] deref)
          (update-in ,,, [:body] deref)
          (handle-cookies ,,, cookies)
          handle-response)))
  (close [this]
    (http/close http-client)))

(defn new-client [conn-str]
  (SlouchClient. (http/create-client) (atom '()) conn-str))

(defmacro defn-remote
  [client fn-name & {:keys [remote-ns remote-name]
                     :or {remote-ns (ns-name *ns*)}}]
  (let [facade-sym (symbol fn-name)
        remote-name (or remote-name (str fn-name))]
    `(def ~facade-sym
       (fn [& args#]
         (invoke ~client ~remote-ns ~remote-name args#)))))
