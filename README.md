# ring.middleware.statsd

Simple [ring](https://github.com/ring-clojure/ring) middlewares for collecting
request/response metrics to [statsd](https://github.com/etsy/statsd/).

## Usage

Include in `project.clj`

```
[loganlinn/ring.middleware.statsd "0.1.0"]
```

### Configure statsd server

```clojure
(ring.middleware.statsd/setup! "statsd.host" 1234)
````

### `wrap-timer`

```clojure
(def app
  (wrap-timer handler :resp_time))
```

Records response time under timer, "resp_time"

### `wrap-response-code-counter`

```clojure
;; record timers, "resp_status.200", "resp_status.404", and so on
(def app
  (wrap-response-code-counter handler :resp_status))
```

You can customize the counter or filter which statuses are counted by passing
`:key-fn` argument.

```clojure
;; count only HTTP OK as "resp_status.ok". All non-200 response not counted.
(def app
  (wrap-response-code-counter
    handler
    :resp_status
    :key-fn #(if (= % 200) "ok")))
```

```clojure
;; bucket responses into "resp_status.2XX", "resp_status.4XX", and so on
(def app
  (wrap-response-code-counter
    handler
    :resp_status
    :key-fn #(str (quot % 100) "XX")))
```

## License

Copyright © 2013 Logan Linn

Distributed under the Eclipse Public License, the same as Clojure.
