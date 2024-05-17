(ns transitions.core
  (:require
   [reagent.core :as r]
   [reagent.dom.client :as rdom.client]
   [goog.events :as events]
   [goog.history.EventType :as HistoryEventType]
   [transitions.ajax :as ajax]
   [ajax.core :refer [GET]]
   [reitit.core :as reitit]
   [clojure.string :as string]
   ["react-transition-group" :refer [CSSTransition]]
   ["react" :as react])
  (:import goog.History))

(defonce state (r/atom false))

(defonce session (r/atom {:page :home}))

(defn about-page []
  [:section.section>div.container>div.content
   [:img {:src "/img/warning_clojure.png"}]])

(defn extracted [show?]
  (let [ref (react/useRef)]
    [:> CSSTransition
     {:classNames  "fade"
      :timeout     1000
      :in          show?
      :nodeRef     ref
      :on-enter    #(js/console.log "enter")
      :on-entering #(js/console.log "entering")
      :on-entered  #(js/console.log "entered")
      :on-exit     #(js/console.log "exit")
      :on-exiting  #(js/console.log "exiting")
      :on-exited   #(js/console.log "exited")}
     [:div {:class (when-not show? "hide")
            :ref   (fn [el]
                     (set! (.-current ref) el))}
      "foobar"]]))


(defn css-transition-example-2 []
  (let [state (r/atom true)]
    (fn []
      [:div
       [:button
        {:on-click (fn [_] (swap! state not))}
        "Toggle"]
       [:f> extracted @state]])))

(defn home-page []
  [css-transition-example-2])

(def pages
  {:home  #'home-page
   :about #'about-page})

(defn page []
  [(pages (:page @session))])

;; -------------------------
;; Routes

(def router
  (reitit/router
   [["/" :home]
    ["/about" :about]]))

(defn match-route [uri]
  (->> (or (not-empty (string/replace uri #"^.*#" "")) "/")
       (reitit/match-by-path router)
       :data
       :name))
;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
     HistoryEventType/NAVIGATE
     (fn [^js/Event.token event]
       (swap! session assoc :page (match-route (.-token event)))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(swap! session assoc :docs %)}))

(defonce root
  (rdom.client/create-root (js/document.getElementById "app")))

(defn ^:dev/after-load mount-components []
  ;; (rdom/render [#'navbar] (.getElementById js/document "navbar"))
  ;; (rdom/render [#'page] (.getElementById js/document "app"))
  (.render root (r/as-element [#'page])))

(defn init! []
  (ajax/load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
