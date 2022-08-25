(ns shorturl.core
  (:require [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [ring.adapter.jetty :as ring-jetty]
            [ring.util.response :as r]
            [shorturl.db :as db]
            [shorturl.slug :refer [generate-slug]]))

(defn redirect [req]
  (let [slug (get-in req [:path-params :slug])
        url (db/get-url slug)]
    (if url
      (r/redirect url 307)
      (r/not-found "Not Found any url"))))

(defn create-redirect [req]
  (let [url (get-in req[:body-params :url])
        slug (generate-slug)]
    (db/insert-redirect! slug url)
    (r/response (str "create slug " slug))
    ))

(def app
  (ring/ring-handler
   (ring/router
    ["/" 
     [":slug" redirect]
     ["api/"
      ["redirects" {:post create-redirect}]]
     ["" {:handler (fn [req] {:body "Create Redirect Screen" :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty #'app {:port 3000 :join? false}))

(def server (start))

;; to run in clj: clj -X shorturl.core/run
;; (defn run [_]
;;   (start)
;;   )

;; (.stop server)

