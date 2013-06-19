(ns ring.middleware.statsd-test
  (:require [ring.middleware.statsd :refer :all]
            [clj-statsd :as s]
            [ring.util.response :as r]
            [ring.mock.request :as mock]
            [midje.sweet :refer :all]))

(defn key-checker
  "Returns checker function for verifying (default) key"
  [base-key key-rem]
  (fn [actual]
    (= (name actual) (str (name base-key) "." key-rem))))

(fact wrap-request-method-counter "default key-fn"
  (let [response (-> (r/response ...body...) (r/status ...status...))
        request (mock/request :get "/")
        handler (wrap-request-method-counter (fn [_] response) :base.key)]
    (handler request) => response
    (provided
      (s/increment (as-checker (key-checker :base.key "get"))) => nil)))

(fact wrap-request-method-counter "key-fn returns nil"
  (let [response (-> (r/response ...body...) (r/status ...status...))
        request (mock/request :get "/")
        handler (wrap-request-method-counter (fn [_] response)
                                             :base.key
                                             :key-fn (fn [_]))]
    (handler request) => response
    (provided
      (s/increment anything) => nil :times 0)))

(fact wrap-response-code-counter "default key-fn"
  (let [response (-> (r/response ...body...) (r/status ...status...))
        handler (wrap-response-code-counter (fn [_] response) :base.key)]
    (handler (mock/request :get "/")) => response
    (provided
      (s/increment (as-checker (key-checker :base.key ...status...))) => nil)))

(fact wrap-response-code-counter "key-fn returns nil"
  (let [response (-> (r/response ...body...) (r/status ...status...))
        handler (wrap-response-code-counter (fn [_] response)
                                            :base.key
                                            :key-fn (fn [_]))]
    (handler (mock/request :get "/")) => response
    (provided
      (s/increment anything) => nil :times 0)))

(let [handler (wrap-response-code-counter
                (fn [req] (-> (r/response ...body...)
                            (r/status (::status req))))
                :base.key
                :key-fn (fn [{status :status}] (if (= status 200) status)))
      request-404 (-> (mock/request :get "/") (assoc ::status 404))
      request-200 (-> (mock/request :get "/") (assoc ::status 200))]
  (fact "non-matching status"
    (handler request-404) => (contains {:body ...body... :status 404})
    (provided
      (s/increment anything) => nil :times 0))
  (fact "matching status"
    (handler request-200) => (contains {:body ...body... :status 200})
    (provided
      (s/increment (as-checker (key-checker :base.key 200))) => nil :times 1)))
