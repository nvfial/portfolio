# 多阶段构建 - 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 复制pom文件
COPY pom.xml .

# 下载依赖（利用Docker缓存层）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 生产阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 创建非root用户
RUN addgroup -S spring && adduser -S spring -G spring

# 复制jar文件
COPY --from=builder /app/target/*.jar app.jar

# 更改所有权
RUN chown spring:spring app.jar

# 切换到非root用户
USER spring:spring

# 暴露端口
EXPOSE 8080

# JVM参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof"

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
