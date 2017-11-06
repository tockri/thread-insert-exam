# Thread insert Examination

Backlogでマルチスレッドでinsertしたときデッドロックみたいになってしまう現象を再現させるための実験プロジェクト

# 実行方法
## Mysqlの準備
* Host = localhost
* Database = threadexam
* User = root
* Password = sa

## 実行
```bash
sbt run
```

## ブラウザからアクセス
http://localhost:9000

## Apache Benchでのアクセス


# 参考：Backlogのスレッドに関する設定
## hikari CPのpool size = 60 (tokyo-5 prod.conf)
[server/ansible/roles/play-app/templates/etc/backlog/play/prod.backlog-tokyo-5.conf.j2](https://nulab.backlog.jp/git/BLG/server/blob/BLG-14546/missing-configs-on-dev/ansible/roles/play-app/templates/etc/backlog/play/prod.backlog-tokyo-5.conf.j2)

```
default.hikaricp {
  maximumPoolSize = 60
  idleTimeout = 12600000
  maxLifetime = 14400000
  registerMbeans = true
}
```

## DBスレッドのExecuterService = 20
[backlog-domain/src/main/scala/backlog/domain/model/config/ThreadPoolConfiguration.scala](https://nulab.backlog.jp/git/BLG/backlog-scala/blob/develop/backlog-domain/src/main/scala/backlog/domain/model/config/ThreadPoolConfiguration.scala)
```scala
ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(20))
```

## ApplicationおよびServiceスレッドのExecuterService = 4?
[backlog-domain/src/main/scala/backlog/support/DefaultContexts.scala](https://nulab.backlog.jp/git/BLG/backlog-scala/blob/develop/backlog-domain/src/main/scala/backlog/support/DefaultContexts.scala)
```scala
ExecutionContext.fromExecutorService(Executors.newCachedThreadPool)
```

