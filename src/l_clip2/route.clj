(ns l-clip2.route
  (:require [reitit.ring :as ring]
            [hiccup2.core :as h]
            [l-clip2.db :refer [eid-title all-data-by-eid new-movie]]
            [muuntaja.core :as m]
            [muuntaja.format.form :as form-format]
            [ring.util.response :as resp]))

(def m (m/create
        (-> m/default-options
            (m/install form-format/format))))

(defn index [conn]
  (fn [_]
    (let [movies (eid-title conn)]
      (resp/response (str (h/html [:h1 "Movies"]
                            [:table {:style {:border "1px solid gray"
                                             :margin-bottom ".5em"}}
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
                                             [:a {:href (str "/movie?eid=" m)} m]
                                             m)])])]]
                            [:a {:href "/movie/new"} "Tambah"]))))))

(defn detail [conn]
  (fn [req]
    (let [eid (-> (:query-string req)
                  (clojure.string/split #"=")
                  last
                  Long/parseLong)
          movie (all-data-by-eid conn eid)]
      (resp/response (if (contains? movie :movie/title)
                       (str (h/html [:table {:style {:border "1px solid gray"}}
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
                                       [:td {:style {:text-align "right"}} [:a {:href "/"} "Kembali"]]]]]))
                       (str (h/html [:h1 (str "movie dengan eid = " eid " ngga ada!!!")])))))))

(defn new [_]
  (resp/response (str (h/html [:h1 "Tambah"]
                        [:div
                         [:form {:action "/movie/add" :method "post"}
                          [:label {:for "title"} ":movie/title"]
                          [:br]
                          [:input {:type "text" :id "title" :name "title"}]
                          [:br]
                          [:label {:for "genre" } ":movie/genre"]
                          [:br]
                          [:input {:type "text" :id "genre" :name "genre"}]
                          [:br]
                          [:label {:for "year" } ":movie/release-year"]
                          [:br]
                          [:input {:type "text" :id "year" :name "year"}]
                          [:br]
                          [:input {:style {:margin-top ".5em"} :type "submit" :value "Simpan"}]
                          [:input {:type "button" :onclick "location.href='/'" :value "Balik"}]]]))))

(defn add [conn]
  (fn [req]
    (let [data (slurp (:body req))
          format (:content-type req)
          decode (m/decode m format data)]
      (new-movie conn [decode])
      (resp/redirect "/"))))

(defn handler [conn]
  (ring/ring-handler (ring/router [["/" {:get (index conn)}]
                                   ["/movies" {:get (index conn)}]
                                   ["/movie" {:get (detail conn)}]
                                   ["/movie/new" {:get new}]
                                   ["/movie/add" {:post (add conn)}]])))
