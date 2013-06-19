(ns ring.middleware.statsd
  (:require [clj-statsd :as s]))

(defn setup!
  "Initialize configuration.
  Not required if clj-statsd setup manually"
  [server port & opts]
  (apply s/setup server port opts))

(defn wrap-request-method-counter
  "Middleware for counting request method types
  Options:
  :key-fn
  A function that's passed the request map to generate remainder of
  counter's key. If falsey value is returned, increment is ignored.
  Returned value is joined with key-base using '.' as delimeter."
  [handler key-base & {:keys [key-fn] :or {key-fn (comp name :request-method)}}]
  (fn [request]
    (let [response (handler request)]
     (if-let [key-rem (key-fn request)]
       (s/increment (str (name key-base) "." key-rem)))
      response)))


(defn wrap-response-code-counter
  "Middleware for counting response status codes.
  Options:
  :key-fn
  A function that's passed the response map to generate remainder of
  counter's key. If falsey value is returned, increment is ignored.
  Returned value is joined with key-base using '.' as delimeter."
  [handler key-base & {:keys [key-fn] :or {key-fn :status}}]
  (fn [request]
    (let [response (handler request)]
      (if-let [key-rem (key-fn response)]
        (s/increment (str (name key-base) "." key-rem)))
      response)))

(defn wrap-timer
  "Middleware for timing requests to statsd.
  Wrap as outermost middleware for most accurate timings."
  [handler k]
  (fn [req]
    (s/with-timing k
      (handler req))))
