(ns ring.middleware.statsd
  (:require [clj-statsd :as s]))

(defn setup!
  "Initialize configuration.
  Not required if clj-statsd setup manually"
  [server port & opts]
  (apply s/setup server port opts))

(defn wrap-response-code-counter
  "Middleware for counting response status codes.
  Options:
  :key-fn  A function to generate remainder of counter's key.
           Can be used to filter/ignore or combine/bucket statues.
           If falsey value returned, the increment will be ignored.
           Retured value is joined with base-key using '.' as delimiter."
  [handler base-key & {:keys [key-fn] :or {key-fn identity}}]
  (fn [request]
    (let [response (handler request)]
      (if-let [status-key (key-fn (:status response))]
        (s/increment (str (name base-key) "." status-key)))
      response)))

(defn wrap-timer
  "Middleware for timing requests to statsd.
  Wrap as outermost middleware for most accurate timings."
  [handler k]
  (fn [req]
    (s/with-timing k
      (handler req))))
