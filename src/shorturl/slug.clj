(ns shorturl.slug)

(def charset "ABCDEFGHIJKLMNOPQRSTUVXYWZ")

(defn generate-slug []
  (->> (repeatedly #(rand-nth charset))
       (take 4)
       (apply str))
  )