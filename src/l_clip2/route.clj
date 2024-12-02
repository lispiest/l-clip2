(ns l-clip2.route
  (:require [reitit.ring :as ring]
            [hiccup2.core :as h]
            [l-clip2.db :refer [eid-title all-data-by-eid]]))

(defn index [conn]
  (fn [_]
    (let [movies (eid-title conn)]
      {:status 200
       :body (str (h/html [:table {:style {:border "1px solid gray"}}
                           [:thead
                            [:tr
                             [:th {:style {:border "1px solid gray" :padding ".5em"}} ":db/id"]
                             [:th {:style {:border "1px solid gray"}} ":movie/title"]]]
                           [:tbody (for [movie movies]
                                     [:tr
                                      (for [m movie]
                                        [:td {:style {:border "1px solid gray"
                                                      :padding ".5em"}}
                                         (if (= (type m) java.lang.Long)
                                           [:a {:href (str "/movie/" m)} m]
                                           m)])])]]))})))

(defn detail [conn]
  (fn [req]
    (let [eid (Long/parseLong (:id (:path-params req)))
          movie (all-data-by-eid conn eid)]
      {:status 200
       :body (str (h/html [:table {:style {:border "1px solid gray"}}
                           [:thead
                            [:tr
                             [:th {:style {:border "1px solid gray" :padding ".5em"}} ":db/id"]
                             [:th {:style {:border "1px solid gray"}} ":movie/title"]
                             [:th {:style {:border "1px solid gray"}} ":movie/genre"]
                             [:th {:style {:border "1px solid gray"}} ":movie/release-year"]]]
                           [:tbody 
                            [:tr
                             [:td {:style {:border "1px solid gray" :padding ".5em"}} (:db/id movie)]
                             [:td {:style {:border "1px solid gray" :padding ".5em"}} (:movie/title movie)]
                             [:td {:style {:border "1px solid gray" :padding ".5em"}} (:movie/genre movie)]
                             [:td {:style {:border "1px solid gray" :padding ".5em" :text-align "right"}} (:movie/release-year movie)]]]
                           [:tfoot
                            [:tr
                             [:td]
                             [:td]
                             [:td]
                             [:td {:style {:text-align "right"}} [:a {:href "/"} "Kembali"]]]]]))})))

(defn handler [conn]
  (ring/ring-handler (ring/router [["/" {:get (index conn)}]
                                   ["/movies" {:get (index conn)}]
                                   ["/movie/:id" {:get (detail conn)}]])))
