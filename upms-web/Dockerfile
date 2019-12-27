FROM registry.cn-hangzhou.aliyuncs.com/vfuchong/java-alpine:8u202
MAINTAINER Francis.zz 674390846@qq.com

ARG JAR_FILE

COPY ${JAR_FILE} /app/app.jar
ENTRYPOINT ["/bin/sh", "-c",  "java -jar ${JAVA_OPTS} /app/app.jar"]
