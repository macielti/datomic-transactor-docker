(require '[babashka.fs :as fs])

(def config {:config-template "/usr/datomic-pro/config/samples/sql-transactor-template.properties"
             :apply-configs-from "/usr/config/postgresql/transactor.properties"
             :host-ip '?host-ip })

(defn read-properties [file-path]
      (with-open [rdr (io/reader file-path)]
                 (into {} (map #(let [[k v] (clojure.string/split % #"=")]
                                     [k v])
                               (filter (comp not clojure.string/blank?)
                                       (line-seq rdr))))))

(defn write-properties [props file-path]
      (with-open [wrtr (io/writer file-path)]
                 (doseq [[k v] props]
                        (.write wrtr (str k "=" v "\n")))))

(defn merge-properties [base-props new-props]
      (merge base-props new-props))

(defn main [& args]
      (let [config-path (System/getenv "BOOT_CONFIG_EDN")
            base-props (read-properties (:config-template config))
            new-props (read-properties (:apply-configs-from config))
            host-ip (slurp *in*)
            merged-props (merge-properties base-props new-props)
            final-props (assoc merged-props "alt-host" host-ip)]
           (write-properties final-props "/usr/datomic-pro/config/transactor.properties")))

(main)