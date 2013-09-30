(ns slouch.server
  (:require [clojure.string :as string]
            [clojure.tools.logging :as log]
            [slouch.protocol :refer [valid-request? valid-response?]]
            [slouch.serialization :as serial]))

(def response-status->http-status
  {:success 200
   :invalid 400
   :not-found 404
   :exception 500})

(defn ns-funcs [n]
  (let [nsname (ns-name n)]
    (into {}
          (for [[k v] (ns-publics n) :when (fn? @v)]
            [(str nsname "/" (name k)) v]))))

(defn ring->slouch
  [req]
  (let [{uri :uri body :body} req
        fn-name (subs uri 1)]
    [fn-name (serial/deserialize body)]))

(defn deserialize-request
  [http-request]
  (try
    (serial/deserialize (:body http-request))
    (catch Throwable t
      [:error (Exception. "Slouch deserialization error" t)])))

(defn handle-request
  [request funcs]
  (if (valid-request? request)
    (let [[fn-name args] request]
      (if-let [f (get funcs fn-name)]
        (try
          [:success (let [r (apply f args)]
                      (if (seq? r) (doall r) r))]
          (catch Throwable t
            [:exception (vec (.getStackTrace t))]))
        [:not-found fn-name]))
    [:invalid :client]))

(defn serialize-response
  [[status _ :as response]]
  (try
    [status (serial/serialize response)]
    (catch Throwable t
      [:error (Exception. "Slouch serialization error" t)])))

(defn slouch->ring
  [[status response]]
  {:status (response-status->http-status status)
   :body response})

(defn pipeline
  [http-request funcs]
  (-> http-request
      ;; TODO: incoming interceptor
      ring->slouch
      ;; TODO: pre deserialization interceptor
      deserialize-request
      ;; TODO: pre handler interceptor
      handle-request
      ;; TODO: post handler interceptor
      serialize-response
      ;; TODO: post serialization interceptor
      slouch->ring
      ;; TODO: outgoing interceptor
      ))

(defn handler
  [exposed-ns]
  (let [funcs (ns-funcs exposed-ns)]
    (log/info :funcs funcs)
    (fn [req]
      (-> req
          ring->slouch
          (pipeline ,,, funcs)
          slouch->ring))))
