(ns slouch.client
  (:refer-clojure :exclude [new])
  (:require [slouch.common :as common]
            [slouch.client.http :as http]
            [slouch.serialization :as serial])
  (:import [slouch.client.http HttpClient]))

(defrecord SlouchClient [http-client])

(defn- handle-result
  [code body]
  {:pre [(number? code) body]}
  (let [return (serial/deserialize body)]
    (condp = code
      (:success common/response-codes)
      return

      (:not-found common/response-codes)
      (throw (Exception. (format "Function '%s' not found" return)))

      (:exception common/response-codes)
      (throw return))))

(defn invoke [{:keys [http-client]}
              ns-name fn-name async args]
  (let [uri (str ns-name "/" fn-name)
        handler #(->> (serial/serialize args)
                      (.send http-client uri ,,,)
                      (.collect http-client ,,,)
                      (apply handle-result ,,,))]
    (if async
      (future (handler))
      (handler))))

(defn close [{:keys [http-client]}]
  (.close http-client))

(defmulti new class)
(defmethod new HttpClient [http-client]
  (SlouchClient. http-client))
(defmethod new String [conn-str]
  (SlouchClient. (http/new conn-str)))

(defmacro defn-remote
  [client fn-name & {:keys [remote-ns remote-name async]
                     :or {remote-ns (ns-name *ns*)
                          async false}}]
  (let [facade-sym (symbol fn-name)
        remote-name (or remote-name (str fn-name))]
    `(def ~facade-sym
       (with-meta
         (fn [& args#]
           (invoke ~client ~remote-ns ~remote-name ~async args#))
         {:client ~client
          :remote-ns ~remote-ns
          :remote-name ~remote-name
          :async ~async
          }))))
