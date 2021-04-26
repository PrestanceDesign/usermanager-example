(ns reagent.inertia
  (:require ["@inertiajs/inertia-react" :refer [App InertiaLink]]
            ["@inertiajs/inertia" :refer [Inertia]]
            [reagent.core :as r]
            [reagent.dom :as d]
            [alandipert.storage-atom :refer [local-storage]]
            [applied-science.js-interop :as j]))

(def el (.getElementById js/document "app"))

(defonce changes (local-storage (r/atom 0) :changes))

(defn site-layout [children]
  [:<>
   [:div#container
    [:h1 "User Manager"]
    [:ul.nav.horizontal.clear
     [:li [:> InertiaLink {:href "/"} "Home"]]
     [:li [:> InertiaLink {:href "/user/list"} "Users"]]
     [:li [:> InertiaLink {:href "/user/form"} "Add User"]]
     [:li [:> InertiaLink {:href "/reset"
                           :on-click #(reset! changes 0)} "Reset"]]]
    [:br]
    [:div.primary
     children]]
   [:div (str "You have made " @changes " change(s) since the last reset!")]])

(defn default [{message :message}]
  [:p message])

(defn user-list [{:keys [users]}]
 [:table {:border "0" :cell-spacing "0"}
   [:thead
    [:tr
      [:th {:width "40"} "Id"]
      [:th "Name"]
      [:th "Email"]
      [:th "Department"]
      [:th "Delete"]]]
   [:tbody
    (if (empty? users)
      [:tr
        [:td {:col-span "5"} "No users exist but new ones can be added"]]
      (for [user users
            :let [{:keys [id first_name last_name email name]} (j/lookup user)]]
        ^{:key id}
        [:tr
          [:td [:> InertiaLink {:href (str"/user/form/" id)} id]]
          [:td.name [:> InertiaLink {:href (str"/user/form/" id)} (str last_name ", " first_name)]]
          [:td.email email]
          [:td.department name]
          [:td.name [:> InertiaLink {:href (str "/user/delete/" id)
                                     :on-click #(swap! changes inc)} "DELETE"]]]))]])

(defn user-form [{:keys [user departments]}]
 (let [{:keys [id first_name last_name email department_id]} (j/lookup user)
       user-info (r/atom {:id id
                           :first_name first_name
                           :last_name last_name
                           :email email
                           :department_id (if (nil? department_id) 1 department_id)})
       handle-change #(swap! user-info assoc (keyword (.. % -target -name)) (.. % -target -value))]
   (fn []
     [:<>
        [:h3 "User Info"]
        [:form.familiar.medium {:method "post"
                                :action "/user/save"
                                :on-submit #(do (.preventDefault %)
                                                (swap! changes inc)
                                                (.post Inertia "/user/save" (clj->js @user-info)))}
          [:input {:type  "hidden"
                   :name  "id"
                   :id    "id"
                   :default-value id}]
          [:div.field
           [:label.label {:for "first_name"} "First name"]
           [:input.first_name {:type  "text"
                               :name  "first_name"
                               :id    "first_name"
                               :default-value first_name
                               :on-change handle-change}]]
          [:div.field
           [:label.label {:for "last_name"} "Last name"]
           [:input.last_name {:type  "text"
                               :name  "last_name"
                               :id    "last_name"
                               :default-value last_name
                               :on-change handle-change}]]
          [:div.field
           [:label.label {:for "email"} "Email"]
           [:input.first_name {:type  "text"
                               :name  "email"
                               :id    "email"
                               :default-value email
                               :on-change handle-change}]]
          [:div.field
           [:label.label {:for "department_id"} "Department:"]
           [:select.department_id {:name "department_id"
                                   :value (:department_id @user-info)
                                   :on-change handle-change}
             (for [department departments
                   :let [{:keys [id name]} (j/lookup department)]]
               [:option {:key id
                         :value id} name])]]
          [:div.control
           [:input {:type "submit"
                     :value "Save User"}]]]])))

(defn reset [{message :message}]
  [:p message])

(def pages {"default" default
            "user-list" user-list
            "user-form" user-form
            "reset" reset})

(defn app []
  [:> App {:initial-page (.parse js/JSON (.. el -dataset -page))
           :resolve-component (fn [name] (let [comp (r/reactify-component (get pages name))]
                                          (set! (.-layout ^js comp) (fn [page] (r/as-element [site-layout page])))
                                          comp))}])

(defn mount-root []
 (d/render [app] el))

(defn init! []
 (mount-root))
