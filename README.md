# Logger

## How to add to your project

```kotlin
repositories {
    maven("https://repository.voinearadu.com/repository/maven-releases/")
    maven("https://repo.voinearadu.com/") // The short version of the above (might be slower on high latency connections)
}

dependencies {
    implementation("com.voinearadu:utils:VERSION")
    
    // To use the redis_manager
    implementation("redis.clients:jedis:<JEDIS_VERSION>")
}
```