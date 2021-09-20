(defproject lein-finagle-clojure "0.8.0-SNAPSHOT"
  :description "A lein plugin for working with finagle-clojure"
  :url "https://github.com/twitter/finagle-clojure"
  :license {:name "Apache License, Version 2.0"
            :url "https://www.apache.org/licenses/LICENSE-2.0"}
  :scm {:name "git" :url "https://github.com/finagle/finagle-clojure"}
  :min-lein-version "2.0.0"
  :repositories [["sonatype" "https://oss.sonatype.org/content/groups/public/"]
                 ["twitter" {:url "https://maven.twttr.com/" :checksum :warn}]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.scala-lang/scala-library "2.12.15"]
                 [org.apache.thrift/libthrift "0.10.0"]
                 [com.twitter/finagle-thrift_2.12 "21.3.0"]
                 [com.twitter/finagle-core_2.12 "21.3.0"]
                 [com.twitter/scrooge-generator_2.12 "21.3.0"]
                 [com.twitter/scrooge-core_2.12 "21.3.0"]]
  :eval-in-leiningen true)
