(ns spede.args.map
  (:require [spede.args.core :as args]))

(defn is-named-binding? [[map-key map-val]]
  (keyword? map-val))

(defn get-keyword [[map-key map-val :as binding]]
  (when (is-named-binding? binding)
    map-val))

(defn is-namespaced-keyword? [k]
  (and (not (nil? (namespace k)))
       (keyword? k)))

(defn make-namespaced-keyword [symb]
  (keyword (str *ns*) (name symb)))

(defn get-spec-keyword [k]
  (if (is-namespaced-keyword? k)
    k
    (make-namespaced-keyword k)))

(defn get-namespaced-symbol-from-symbol [k]
  (->> k
       name
       (symbol (str *ns*))))

(defn get-keyword-symbol-and-namespace-for-keys [k]
  {::spec-keyword (get-spec-keyword k)
   ::symbol       (get-namespaced-symbol-from-symbol k)
   ::namespaced   (is-namespaced-keyword? k)})

(defn get-keyword-symbol-and-namespace-for-named [[binding-name kw]]
  {::spec-keyword (get-spec-keyword kw)
   ::symbol       (get-namespaced-symbol-from-symbol binding-name)
   ::namespaced   (is-namespaced-keyword? kw)})

(defn get-keys-and-symbols [{:keys [keys] :as arg}]
  (concat (map get-keyword-symbol-and-namespace-for-keys keys)
          (->> arg
               (filter is-named-binding?)
               (map get-keyword-symbol-and-namespace-for-named))))

(defn assign-spec-keys-type [optionals {sym ::symbol has-ns ::namespaced}]
  (if (contains? optionals sym)
    (if has-ns :opt :opt-un)
    (if has-ns :req :req-un)))

(defn get-keys-def [[keys-kw keywords]]
  [keys-kw (mapv ::spec-keyword keywords)])

(defn get-namespaced-symbol [sym]
  (if (namespace sym)
    sym
    (symbol (str *ns*) (str sym))))

(defn parse-map [{optionals :or :keys [:as] :as arg}]
  (let [argname (if as
                  (keyword (name as))
                  (keyword (gensym)))
        opts    (apply hash-set (map get-namespaced-symbol (keys optionals)))
        keydefs (->> (get-keys-and-symbols arg)
                     (group-by (partial assign-spec-keys-type opts)))]
    [argname (->> (apply concat
                         [`clojure.spec/keys]
                         (map get-keys-def keydefs))
                  (apply list))]))

(defmethod args/parse-args clojure.lang.PersistentHashMap [arg] (parse-map arg))
(defmethod args/parse-args clojure.lang.PersistentArrayMap [arg] (parse-map arg))

