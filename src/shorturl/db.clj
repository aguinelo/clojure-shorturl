(ns shorturl.db
  (:require [clojure.java.jdbc :as j]
            [honey.sql :as sql]
            [honey.sql.helpers :refer :all]
            [shorturl.env :as env]))

(def mysql-db {:dbtype "mysql"
               :host (env/env :HOST)
               :dbname (env/env :DBNAME)
               :user (env/env :USER)
               :password (env/env :PASSWORD)})

(defn query [q]
  (j/query mysql-db q))

(defn insert! [q]
  (j/db-do-prepared mysql-db q ))

(defn insert-redirect! [slug url]
  (insert! (-> (insert-into :redirects)
               (columns :slug :url)
               (values
                [[slug url]])
               (sql/format))))

(defn get-url [slug]
  (-> (query (-> (select :*)
                    (from :redirects)
                    (where [:= :slug slug])
                    (sql/format)))
      first
      :url))