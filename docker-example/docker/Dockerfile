#基于哪个镜像
FROM fabletang/jre8-alpine

#镜像维护者信息
MAINTAINER biejunyang <biejunyang1210@163.com>

#声明挂载卷
VOLUME /tmp

#设置构建参数，构建传入的参数,${JAR_FILE}方式使用，如下
ARG JAR_FILE

#设置镜像环境变量
ENV JAR_FILE ${JAR_FILE}

#复制jar文件到容器中,源路径是相当于构建上下文路径，目的路径可以是容器的绝对路径，也可以相对于容器的工作目录(WORKDIR指令设置),默认是/根路径
ADD ${JAR_FILE} ${JAR_FILE}

#声明容器需要暴露的端口，主要是告知镜像使用者
EXPOSE 8080

#配置容器启动后执行的命令
ENTRYPOINT [ "sh", "-c", "java -jar $JAR_FILE" ]
#CMD java -jar $JAR_FILE
