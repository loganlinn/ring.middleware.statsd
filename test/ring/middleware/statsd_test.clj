(ns ring.middleware.statsd-test
  (:require [ring.middleware.statsd :refer :all]
            [clj-statsd :as s]
            [ring.util.response :as r]
            [midje.sweet :refer :all]))

(defn key-checker
  "Returns checker function for verifying (default) key"
  [base-key status]
  (fn [actual]
    (= (name actual) (str (name base-key) "." status))))

(fact wrap-response-code-counter
  (let [response (-> (r/response ...body...) (r/status ...status...))
        handler (wrap-response-code-counter
                  (fn [req] response)
                  :base.key)]
    (handler {}) => response
    (provided
      (s/increment (as-checker (key-checker :base.key ...status...))) => nil)))

(fact wrap-response-code-counter "key-fn returns nil"
  (let [response (-> (r/response ...body...) (r/status ...status...))
        handler (wrap-response-code-counter
                  (fn [req] response)
                  :base.key
                  :key-fn (fn [status] nil))]
    (handler {}) => response
    (provided
      (s/increment anything) => nil :times 0)))

(let [handler (wrap-response-code-counter
                (fn [req] (-> (r/response ...body...)
                            (r/status (::status req))))
                :base.key
                :key-fn (fn [status] (if (= status 200) status)))]
  (fact "non-matching status"
    (handler {::status 404}) => (contains {:body ...body... :status 404})
    (provided
      (s/increment anything) => nil :times 0))
  (fact "matching status"
    (handler {::status 200}) => (contains {:body ...body... :status 200})
    (provided
      (s/increment (as-checker (key-checker :base.key 200))) => nil :times 1)))
