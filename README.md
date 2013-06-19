# ring.middleware.statsd
[![Build Status](https://travis-ci.org/loganlinn/ring.middleware.statsd.png?branch=master)](https://travis-ci.org/loganlinn/ring.middleware.statsd)

Simple [ring](https://github.com/ring-clojure/ring) middlewares for collecting
request/response metrics to [statsd](https://github.com/etsy/statsd/).

## Usage

Current [semantic](http://semver.org/) version:

```
[ring.middleware.statsd "1.0.0"]
```

[API Documentation](http://loganlinn.github.io/ring.middleware.statsd/)

### Configure statsd server

```clojure
(ring.middleware.statsd/setup! "statsd.host" 1234)
````

### `wrap-timer`

Record response times under timer named "resp_time"

```clojure
(def app
  (wrap-timer handler :resp_time))
```


### `wrap-request-method-counter`

Record counters for request methods, as "req_method.get", "req_method.post", etc

```clojure
(def app
  (wrap-request-method-counter handler :req_method))
```

### `wrap-response-code-counter`

Record counters for response statuses, as "resp_status.200", "resp_status.404",
etc

```clojure
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
