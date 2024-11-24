# 如何添加依赖
- [Maven](#maven)
- [Gradle](#gradle)
- [GradleKts](#gradleKt)
- [Version](https://jitpack.io/com/github/zimoyin/qqbot/) 列表: 请查看该列表并选择一个新的版本
- [Version](https://github.com/zimoyin/qqbot/releases) 列表: 请查看该列表并选择一个新的版本 （Github）
---

#### <a name="maven"></a>Maven

```xml
<!--添加该库的仓库地址-->
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
<!--引入依赖-->
<dependencies>
  <dependency>
    <groupId>com.github.zimoyin</groupId>
    <artifactId>qqbot</artifactId>
    <version>$VERSION</version>
  </dependency>
</dependencies>
```

---

#### <a name="tab2"></a>Gradle
```groovy
// 添加jitpack仓库
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
  }
}
// 或者 添加jitpack仓库
repositories {
  mavenCentral()
  maven { url 'https://jitpack.io' }
}
// 引入依赖
implementation 'com.github.zimoyin:qqbot:$VERSION'
```

---

#### <a name="tab2"></a>Kotlin Gradle
```kotlin
// 添加jitpack仓库
repositories {
  maven("https://jitpack.io")
}
// 引入依赖
implementation("com.github.zimoyin:qqbot:$VERSION")
```
---
