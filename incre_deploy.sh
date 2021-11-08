#!/bin/bash
#Time
#desc:
#author: Francis.zz
#部署时会解压jar包便于增量升级
set -m
readonly SCRIPT_NAME=$(basename $0)

export JAVA_HOME=/usr/java/jdk1.8.0_172/
export JRE_HOME=${JAVA_HOME}/jre

###manual_properties###
export deploy_home=/f/app/deploy/vfc_task
#export ACTIVE_PROFILE=local
LOG_PATH=/app/biz_logs/sptsm-task
MIN_JDK_VERSION=1.8
target_jar_dir=${deploy_home}/task_release # 发布 jar存放目录，使用该jar启动
src_jar_dir=${deploy_home}/task_target     # 待发布jar存放目录，使用jar替换升级时替换该目录的jar
unpack_location=${deploy_home}/task_unpack # 解包目录，增量更新时替换该目录的文件
archive_location=${deploy_home}/archive    # 备份目录，每次升级后重新部署时会生成当前版本的备份
# target jar name
export jar_name=sptsm-task.jar
# 保留备份文件数量
bak_file_num=5
usage="Usage: "$SCRIPT_NAME" [start|stop|restart|status|deploy] \
      升级时必须使用deploy指令 "

export server_name=${jar_name##*/}
export LOG_BASE_DIR=${deploy_home}/logs
###manual_properties###

JAVA_OPTS="${JAVA_OPTS} -server -Xms2g -Xmx2g -XX:MaxMetaspaceSize=512M -Xmn350M"

# 先观察是否需要该参数
JAVA_OPTS="${JAVA_OPTS} -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
               -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 \
               -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${LOG_BASE_DIR}/gc_log/gc.log -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_BASE_DIR}/gc_log/heapDump_%t.hprof"

PID="ps -ef|grep -w ${server_name}|grep 'java'|grep -v 'grep'|awk '{print \$2}'|xargs"

export JAVA_OPTS

error_exit() {
  echo "ERROR: $1"
  exit 1
}

stop_server() {
  local pid=$(eval "${PID}")
  if [[ -z ${pid} ]]; then
    echo "server ${server_name} is not running, Stop aborted."
    return
  fi

  echo "current server pid is ${pid}, begin kill..."
  kill ${pid} >/dev/null 2>&1
  sleep 8
  if [[ $? -ne 0 ]]; then
    echo "kill failed. Attempting to signal the process to stop through OS signal."
    kill -15 ${pid} >/dev/null 2>&1
    sleep 8
  fi

  pid=$(eval "${PID}")
  if [[ -z ${pid} ]]; then
    echo "server ${server_name} Stopped."
    sleep 3
    return
  fi
  while [[ -n ${pid} ]]; do
    echo "force kill server ${server_name}, pid is ${pid}"
    kill -9 "${pid}"
    sleep 3
    pid=$(eval "${PID}")
    if [[ -z ${pid} ]]; then
      echo "server ${server_name} stopped"
      sleep 3
      break
    fi
  done
}

# check server running status
check_status() {
  local -r pid=$(eval "${PID}")
  # 这里判断要使用[[ ]]或者使用""将字符串抱起来，防止字符串为空时的逻辑错误
  if [[ -n ${pid} ]]; then
    echo "${server_name} is running, pid is ${pid}"
  else
    echo "${server_name} server had stopped"
  fi
}

check_run() {
  local -r pid=$(eval "${PID}")
  # 这里判断要使用[[ ]]获取使用""将字符串抱起来，防止字符串为空时的逻辑错误
  if [[ -n ${pid} ]]; then
    echo "${server_name} is running, pid is ${pid}"
    return 0
  else
    return 1
  fi
}

deploy_server() {
  compare_version
  local -r ver=$?
  if [[ $ver -eq 1 ]]; then
    echo "use unpack file to deploy"
    repack_and_backup
  elif [ $ver -eq 2 ]; then
    echo "use target jar to deploy"
    unpack_and_backup
  else
    error_exit "exec failed"
  fi

  stop_server
  start_server
}

# 遍历unpack目录，查找最新更新日期，并与target目录的jar的最后更改时间作比对
# @return 1标识最新的是解包目录;2标识最新的是target中的jar
compare_version() {
  if [ ! -d ${unpack_location} ] && [ ! -f ${src_jar_dir}/${jar_name} ]; then
    error_exit "${unpack_location} have not file, and also not found ${src_jar_dir}/${jar_name}"
  fi

  if [[ ! -d ${unpack_location} ]]; then
    return 2
  fi

  local -r file_num=$(find ${unpack_location} -type f | wc -l)
  if [[ $file_num -lt 1 ]]; then
    echo "${unpack_location} is empty, use target jar to deploy"
    return 2
  fi

  if [[ ! -f ${src_jar_dir}/${jar_name} ]]; then
    echo "not found ${src_jar_dir}/${jar_name}, use unpack file to deploy"
    return 1
  fi

  # get lasted updated file
  lasted_file=$(find ${unpack_location} -type f -print0 | xargs -0 ls -drt | tail -n 1)
  upt_time=$(stat -c %Y $lasted_file)
  jar_upt_time=$(stat -c %Y ${src_jar_dir}/${jar_name})

  if [[ $upt_time > $jar_upt_time ]]; then
    return 1
  else
    return 2
  fi
}

# target jar unpack and backup
unpack_and_backup() {
  local -r src_app=${src_jar_dir}/${jar_name}

  if [[ ! -f ${src_app} ]]; then
    echo "not found need to deployed app from path:${src_jar_dir}"
    exit 1
  fi

  # unpack target jar
  if [ ! -d ${unpack_location} ]; then
    echo "create unpack directory:${unpack_location}"
    mkdir -p ${unpack_location}
  fi
  cd ${unpack_location} || exit
  jar -xf ${src_app}
  if [[ $? -ne 0 ]]; then
    error_exit "target jar unpack failed"
  fi

  archive

  # 将目标jar文件替换发布文件
  cp -f ${src_app} ${target_jar_dir}/
  if [[ $? -ne 0 ]]; then
    error_exit "transfer ${src_app} to release dir ${target_jar_dir} failed ..."
  fi
}

# 将解包目录文件打包成jar，并更新发布文件
repack_and_backup() {
  if [ ! -d ${unpack_location} ]; then
    error_exit "${unpack_location} not exist"
  fi
  local -r file_num=$(find ${unpack_location} -type f | wc -l)
  if [[ $file_num -lt 1 ]]; then
    error_exit "${unpack_location} is empty"
  fi

  # repack jar
  cd ${unpack_location}/ || exit
  jar -cfM0 ${jar_name} *
  if [[ $? -ne 0 ]]; then
    error_exit "repack jar failed"
  fi

  archive

  # 将目标jar文件替换发布文件
  cp -f ${unpack_location}/${jar_name} ${target_jar_dir}/
  if [[ $? -ne 0 ]]; then
    error_exit "transfer repack jar to release dir ${target_jar_dir} failed ..."
  fi

  # delete repack jar
  rm -f ${unpack_location}/${jar_name}
}

archive() {
  if [[ ! -d ${target_jar_dir} ]]; then
    echo "release dir:${target_jar_dir} not exist, now create it"
    mkdir -p ${target_jar_dir}
    return 0
  fi
  cd ${target_jar_dir}/ || exit

  cur_time=$(date '+%Y%m%d%H%M%S')
  local -r release_app=${target_jar_dir}/${jar_name}
  # 备份旧的发布APP
  if [[ -f ${release_app} ]]; then
    echo "backup old release app file to backup dir: ${archive_location}"
    if [[ ! -d ${archive_location} ]]; then
      echo "backup dir:${archive_location} not exist, now create it"
      mkdir -p ${archive_location}
    fi
    bak_file_name=${archive_location}/${jar_name%.*}_${cur_time}.${jar_name##*.}
    cp -f ${release_app} "${bak_file_name}"

    if [[ $? -ne 0 ]]; then
      error_exit "backup failed"
    fi
    echo "success backup app file:${bak_file_name}"
    manage_bak_file
  fi
}

manage_bak_file() {
  if [[ -z ${archive_location} ]]; then
    archive_location=${deploy_home}/archive
  fi
  # delete extra bak file
  cd ${archive_location} || error_exit
  local cur_bak_num=$(find . -type f | wc -l)
  if [[ ${cur_bak_num} -gt $((${bak_file_num})) ]]; then
    remove_bak_num=$((${cur_bak_num} - ${bak_file_num}))
    echo "remove ${remove_bak_num} backup files."
    rm -r $(ls -rt | head -n${remove_bak_num})
  fi
}

# check jdk version
check_jdk_version() {
  if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    JAVA_EXEC="$JAVA_HOME/bin/java"
  elif type -p java; then
    JAVA_EXEC=java
  else
    error_exit "not found java"
  fi

  if [[ "$JAVA_EXEC" ]]; then
    version=$("$JAVA_EXEC" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo use jdk version "$version"
    if [[ "$version" < ${MIN_JDK_VERSION} ]]; then
      error_exit "jdk version is less than ${MIN_JDK_VERSION}"
    fi
  fi
}

start_server() {
  local -r pid=$(eval "${PID}")
  if [[ -n ${pid} ]]; then
    echo "${server_name} is running, pid is ${pid}, Start aborted."
    exit 1
  fi

  local -r release_app=${target_jar_dir}/${jar_name}
  # 备份旧的发布APP
  if [[ ! -f ${release_app} ]]; then
    error_exit "not found release jar ${release_app}"
  fi

  if [[ ! -d ${LOG_BASE_DIR} ]]; then
    mkdir -p ${LOG_BASE_DIR}
  fi
  if [[ ! -d ${LOG_BASE_DIR}/gc_log ]]; then
    mkdir -p ${LOG_BASE_DIR}/gc_log
  fi

  if [[ -z "${JAVA_EXEC}" ]] || [[ ! -x "${JAVA_EXEC}" ]]; then
    check_jdk_version
  fi

  if [ "${ACTIVE_PROFILE}" ]; then
    RUN_ARGS="${RUN_ARGS} --spring.profiles.active=${ACTIVE_PROFILE}"
  fi
  if [ "${LOG_PATH}" ]; then
    RUN_ARGS="${RUN_ARGS} --LOG_PATH=${LOG_PATH}"
  fi
  echo "RUN_ARGS:${RUN_ARGS}"
  echo "startup server..."
  cd ${target_jar_dir}/ || exit
  local catalina_log=${LOG_BASE_DIR}/start.out
  # 启动app, jvm参数位置不能写错，要紧跟在-jar后面 eg: java -jar [OPTS] app.jar
  # 2标识错误输出  1标识标准输出
  # 2>&1 就是用来将标准错误 2 重定向到标准输出 1 中的。此处 1 前面的 & 就是为了让 bash 将 1 解释成标准输出而不是文件 1
  # 重定向的日志会记录System.ou以及日志框架的ch.qos.logback.core.ConsoleAppender日志
  # 这里如果使用eval，会使脚本运行进程一直存在，因为eval执行指令的返回状态即为eval状态。不使用eval的时候，要去掉${JAVA_OPTS}参数的双引号
  nohup ${JAVA_EXEC} -jar ${JAVA_OPTS} ${release_app} ${RUN_ARGS} >>${catalina_log} 2>&1 &

  # 打开日志，一段时间后自动关闭
  if [[ $? -ne 0 ]]; then
    error_exit "server $server_name start failed."
  fi
  sleep 15 &
  timerPid=$!
  echo "server $server_name Started."
  sleep 1
  tail -fn 500 --pid=$timerPid "${catalina_log}"
}

if [[ -n $1 ]]; then
  case $1 in
  "start")
    start_server
    check_run
    ;;
  "stop")
    stop_server
    ;;
  "restart")
    stop_server
    start_server
    check_run
    ;;
  "status")
    check_status
    ;;
  "deploy")
    deploy_server
    check_run
    ;;
  *)
    echo "$usage"
    ;;
  esac
else
  echo "$usage"
fi
