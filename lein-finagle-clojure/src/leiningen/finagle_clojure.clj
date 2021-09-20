(ns leiningen.finagle-clojure
  (:require [leiningen.javac]
            [robert.hooke]
            [clojure.java.io :as io]))

(defn- thrift-files
  [project-root source-path]
  (->> source-path
    (io/file project-root)
    (file-seq)
    (filter #(and (.isFile %) (.endsWith (.getName %) ".thrift")))
    (map #(.getPath %))))


(defprotocol CljToScala
  (clj->scala [obj]))

(extend-protocol CljToScala
  clojure.lang.PersistentVector
  (clj->scala [seqn]
    (.toSeq (scala.collection.JavaConverters/asScalaBuffer seqn)))
  clojure.lang.PersistentHashSet
  (clj->scala [hset]
    (.toSet (scala.collection.JavaConverters/asScalaSet hset)))
  clojure.lang.PersistentArrayMap
  (clj->scala [hmap]
    (.$plus$plus (scala.collection.Map/empty$
                  (.asScala (scala.collection.JavaConverters/mapAsScalaMapConverter (java.util.HashMap. {}))))
                 (.asScala (scala.collection.JavaConverters/mapAsScalaMapConverter (java.util.HashMap. hmap)))))
  clojure.lang.PersistentList$EmptyList
  (clj->scala [lst]
    (.toList (scala.collection.JavaConverters/asScalaBuffer lst)))
  clojure.lang.PersistentList
  (clj->scala [lst]
    (.toList (scala.collection.JavaConverters/asScalaBuffer lst)))
  clojure.lang.LazySeq
  (clj->scala [lst]
    (.toList (scala.collection.JavaConverters/asScalaBuffer lst))))


(defrecord Configs [dest-folder include-paths thrift-files flags namespace-mappings verbose strict gen-adapt
                    skip-unchanged language-flags file-map-path dry-run language default-namespace
                    scala-warn-on-java-ns-fallback java-ser-enum-type add-root-dir-importer])

(defn configure-settings
  [project]
  (let [absolute-dest-path (->> (or (get-in project [:finagle-clojure :thrift-output-path])  "src/java")
                                (io/file (:root project))
                                (.getAbsolutePath))]
    (Configs. absolute-dest-path
              (clj->scala '((or (get-in project [:finagle-clojure :thrift-source-path]) "src/thrift")))
              (clj->scala (thrift-files (:root project) (or (get-in project
                                                                    [:finagle-clojure :thrift-source-path])
                                                            "src/thrift")))
              (clj->scala #{"--finagle" "--java-passthrough --skip-unchanged"})
              (clj->scala {})
              false
              true
              false
              false
              (clj->scala ["java"])
              (scala.Option/empty)
              false
              "java"
              (com.twitter.scrooge.CompilerDefaults/defaultNamespace)
              false
              false
              true)))

(defn initiate-scrooge-config
  [configured-settings]
  (com.twitter.scrooge.ScroogeConfig. (:dest-folder configured-settings) (:include-paths configured-settings)
                                      (:thrift-files configured-settings) (:flags configured-settings)
                                      (:namespace-mappings configured-settings) (:verbose configured-settings)
                                      (:strict configured-settings) (:gen-adapt configured-settings)
                                      (:skip-unchanged configured-settings) (:language-flags configured-settings)
                                      (:file-map-path configured-settings) (:dry-run configured-settings)
                                      (:language configured-settings) (:default-namespace configured-settings)
                                      (:scala-warn-on-java-ns-fallback configured-settings)
                                      (:java-ser-enum-type configured-settings) (:add-root-dir-importer configured-settings)))


(defn scrooge
  "Compile Thrift definitions into Java classes using Scrooge.

  Scrooge is a Thrift compiler that generates classes with 
  Finagle appropriate interfaces (wraps Service method return values in Future).

  Scrooge also provides a Thrift linter that can be run before compilation. Lint errors will
  prevent compilation. Pass :lint as an argument to this task to enable linting.
  Additional args passed after :lint will be passed to the linter as well.
  See https://twitter.github.io/scrooge/Linter.html or run :lint with --help for more info.
  
  This task expects the following config to present on the project:

    :finagle-clojure {:thrift-source-path \"\" :thrift-output-path \"\"}

  Example usage:

    lein finagle-clojure scrooge # compiles thrift files
    lein finagle-clojure scrooge :lint # lints thrift files before compilation
    lein finagle-clojure scrooge :lint --help # shows available options for the linter
    lein finagle-clojure scrooge :lint -w # show linter warnings as well (warnings won't prevent compilation)"
  [project & options]
  (let [configured-settings (configure-settings project)]
    (leiningen.core.main/info "Compiling Thrift files:" (:thrift-files configured-settings))
    (.run (com.twitter.scrooge.Compiler. (initiate-scrooge-config configured-settings)))))

(defn javac-hook
  [f project & args]
  (scrooge project)
  (apply f project args))

(defn finagle-clojure
  "Adds a hook to lein javac to compile Thrift files first."
  {:help-arglists '([scrooge])
   :subtasks [#'scrooge]}
  [project subtask & args]
  (case subtask
    "scrooge" (apply scrooge project args)))

(defn activate []
  (robert.hooke/add-hook #'leiningen.javac/javac #'javac-hook))
