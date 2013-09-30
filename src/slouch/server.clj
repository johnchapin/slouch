(ns slouch.server
  (:require [slouch.common :as common]
            [slouch.serialization :as serial]))

(defn- ns-funcs [n]
  (let [nsname (ns-name n)]
    (into {}
          (for [[k v] (ns-publics n) :when (fn? @v)]
            [(str nsname "/" (name k)) v]))))

(defn wrap-slouch-fn
  [handler funcs]
  (fn [{uri :uri :as request}]
    (let [fn-name (subs uri 1)]
      (if-let [f (get funcs fn-name)]
        (handler (assoc-in request [:slouch :fn] f))
        {:status (:not-found common/response-codes)
         :slouch {:result uri}}))))

;; TODO: Handle serialization exceptions
(defn wrap-slouch-serial
  [handler]
  (fn [{body :body :as request}]
      (let [args (serial/deserialize body)
            response (handler (assoc-in request [:slouch :args] args))
            result (get-in response [:slouch :result])]
        (assoc response :body (serial/serialize result)))))

(defn handle-request
  [{{f :fn args :args} :slouch :as request}]
  (try
    (let [result (let [r (apply f args)]
                   (if (seq? r) (doall r) r))]
      {:status (:success common/response-codes)
       :slouch {:result result}})
    (catch Throwable t
      {:status (:exception common/response-codes)
       :slouch {:result t}})))

(defn handler [exposed-ns & {:keys [interceptors]}]
  (let [funcs (ns-funcs exposed-ns)]
    (-> handle-request
        ((get interceptors :slouch-request identity))
        (wrap-slouch-fn ,,, funcs)
        ((get interceptors :slouch-fn identity))
        wrap-slouch-serial
        ((get interceptors :serial identity)))))
