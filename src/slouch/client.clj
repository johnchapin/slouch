(ns slouch.client
  (:require [http.async.client :as http]
            [slouch.serialization :as serial]
            [slouch.protocol :refer [handle-response valid-request? valid-response?]]
            [clojure.tools.logging :as log]))

(defn fn-wrapper
  [host port]
  (let [http-client (http/create-client)]
    (fn [ns-name fn-name & args]
      ;; TODO: Use standard lib to build URL
      (let [conn-str (str host ":" port)
            uri (str ns-name "/" fn-name)
            url (str conn-str "/" uri)
            body (serial/serialize args)
            http-response (http/POST http-client url :body body)
            http-body @(:body (http/await http-response))
            response (serial/deserialize http-body)]
          (handle-response response)))))

(comment
  (require '[slouch.example.api :as api])
  (require '[slouch.client :as client])

  (def wrapper (client/fn-wrapper "http://localhost" 3000))
  (def result (wrapper 'slouch.example.api 'sum [1 2 3]))
  )
