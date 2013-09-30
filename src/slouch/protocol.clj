(ns slouch.protocol)

(defn valid-request?
  [req]
  (and (coll? req)
       (= 2 (count req))
       (string? (first req))
       (coll? (second req))))

(def response-handlers
  {:success second
   :invalid (fn [[_ scapegoat]]
              (throw
                (Exception.
                  (format "Invalid %s message" (name scapegoat)))))
   :not-found (fn [[_ fn-name]]
                (throw
                  (Exception.
                    (format "Function '%s' not exported by server" fn-name))))
   :exception (fn [[_ throwable]]
                (throw throwable))
   :error (fn [[_ throwable]]
            ;; TODO: Indicate this is a slouch error, not from wrapped code
            (throw throwable))})

(defn valid-response?
  [response]
  (and (coll? response)
       (= 2 (count response))
       (contains? (set (keys response-handlers)) (first response))))

(defn handle-response
  [response]
  (if (valid-response? response)
    (let [handler (get response-handlers (first response))]
      (handler response))
    ;; TODO: This seems ugly.
    ((:invalid response-handlers) [nil :server])))
