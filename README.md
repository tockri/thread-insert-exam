# Thread insert Examination

Backlogでマルチスレッドでinsertしたときデッドロックみたいになってしまう現象を再現させるための実験プロジェクト

# Backlogのスレッドに関する設定
## tokyo-5 prod.conf
[server/ansible/roles/play-app/templates/etc/backlog/play/prod.backlog-tokyo-5.conf.j2](https://nulab.backlog.jp/git/BLG/server/blob/BLG-14546/missing-configs-on-dev/ansible/roles/play-app/templates/etc/backlog/play/prod.backlog-tokyo-5.conf.j2)

```
default.hikaricp {
  maximumPoolSize = 60
  idleTimeout = 12600000
  maxLifetime = 14400000
  registerMbeans = true
}
```

## DBスレッドのExecuterService
```scala
ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(4))
```

## ApplicationServiceスレッドのExecuterService
```scala
ExecutionContext.fromExecutorService(Executors.newCachedThreadPool())
```

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

## ブラウザから
http://localhost:9000