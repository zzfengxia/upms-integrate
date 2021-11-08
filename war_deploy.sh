#!/bin/bash
#Time
#desc:
#使用前注意替换项目部署路径deploy_home，tomcat路径tomcat_basehome(推荐一键替换%s/sptsm/sptsm/g，sptsm为替换后的名称)，解压的包名war_name
set -m
readonly SCRIPT_NAME=$(basename $0)
log_time=$(date +[%Y-%m-%d]%H:%M:%S)

###manual_properties###
MIN_JDK_VERSION=1.8
tomcat_basehome=/app/tomcat_sptsm
deploy_home=/app/deploy/vfc_sptsm
shell_environment=/bin/bash
war_source_dir=${deploy_home}/sptsm_source
deploy_location=${deploy_home}/sptsm
bak_location=${deploy_home}/sptsm_bak

LOG_BASE_DIR=${tomcat_basehome}/logs

# 解压war包名
war_name=sptsm-server-0.0.1-SNAPSHOT.war
# 保留备份文件数量
bak_file_num=5
usage="Usage: "$SCRIPT_NAME" [start|stop|restart|status|deploy] \
      升级时必须使用deploy指令"

PID="ps -ef|grep -w ${tomcat_basehome}|grep 'java'|grep -v 'grep'|awk '{print \$2}'|xargs"
# 使用tomcat_basehome更有辨识度
server_name=${tomcat_basehome##*/}
###manual_properties###

JAVA_OPTS="${JAVA_OPTS} -server -Xms4g -Xmx4g -XX:MaxMetaspaceSize=512M -Xmn350M"
JAVA_OPTS="${JAVA_OPTS} -Xloggc:${LOG_BASE_DIR}/gc_log/gc.log -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=100M"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOG_BASE_DIR}/gc_log/heapDump_%t.hprof"

#update server environment
echo "********************************** ${log_time} *************************************"
export JAVA_HOME=/usr/local/jdk1.8.0_181/
export JRE_HOME=${JAVA_HOME}/jre
export JAVA_OPTS

error_exit() {
  echo "ERROR: $1"
  exit 1
}

# check jdk version
check_jdk_version() {
  [ -z ${MIN_JDK_VERSION} ] && return

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

stop_server() {
  local pid=$(eval ${PID})
  if [[ -z ${pid} ]]; then
    echo "server ${server_name} is not running, Stop aborted."
    return
  fi

  echo "current server [${server_name}] pid is ${pid}, begin stop..."
  ${shell_environment} ${tomcat_basehome}/bin/shutdown.sh
  sleep 8

  pid=$(eval ${PID})
  if [[ -z ${pid} ]]; then
    echo "server ${server_name} Stopped."
    sleep 3
    return
  fi
  while [[ -n ${pid} ]]; do
    echo "force kill server ${server_name}, pid is ${pid}"
    kill -9 ${pid}
    sleep 3
    pid=$(eval ${PID})
    if [[ -z ${pid} ]]; then
      echo "server ${server_name} stopped"
      sleep 3
      break
    fi
  done
}

# check tomcat running status
check_status() {
  local -r pid=$(eval ${PID})
  if [[ -n ${pid} ]]; then
    echo "${tomcat_basehome##*/} is running, pid is ${pid}"
  else
    echo "tomcat was stopped"
  fi
}

archive() {
  if [[ ! -d $deploy_home/sptsm ]]; then
    return 0
  fi
  local -r file_num=$(find $deploy_home/sptsm -type f | wc -l)
  if [[ $file_num -lt 1 ]]; then
    return 0
  fi

  echo "package to war and backup the old file: $deploy_home/sptsm..."
  if [ ! -d ${bak_location} ]; then
    echo "create dir $bak_location..."
    mkdir -p $bak_location
  fi
  cd $deploy_home/sptsm || exit
  cur_time=$(date '+%Y%m%d%H%M%S')

  bak_file_name="${war_name%.*}_${cur_time}.${war_name##*.}"
  jar -cf0M ${bak_file_name} *

  ret=$?
  if [ "$ret" -ne 0 ]; then
    error_exit "the old file failed to backup"
  fi
  mv ${bak_file_name} $bak_location

  manage_bak_file
}

unpack_and_backup() {
  if [ ! -a "$war_source_dir"/"${war_name}" ]; then
    error_exit "deploy file $war_source_dir/${war_name} not exist,please check."
  fi

  archive

  if [ -d "$deploy_home/sptsm" ]; then
    remove_path="$deploy_home/sptsm/"
    echo "remvoe old deploy file:${remove_path}"
    rm -rf "${remove_path}"
    sleep 1
  fi
  mkdir "$deploy_home/sptsm"

  echo "unzip ${war_source_dir}/${war_name}..."
  cd $deploy_home/sptsm || exit
  jar -xf $war_source_dir/${war_name}

  ret=$?
  if [ "$ret" -ne 0 ]; then
    error_exit "the war file failed to unzip"
  fi
}

manage_bak_file() {
  # delete extra bak file
  cd ${deploy_home}/sptsm_bak || exit
  cur_bak_num=$(find . -type f | wc -l)
  if [ ${cur_bak_num} -gt $((${bak_file_num})) ]; then
    remove_bak_num=$((${cur_bak_num} - ${bak_file_num}))
    echo "remove ${remove_bak_num} backup files."
    rm -r $(ls -rt | head -n${remove_bak_num})
  fi
}

start_server() {
  check_jdk_version

  if [[ ! -d ${LOG_BASE_DIR} ]]; then
    mkdir -p ${LOG_BASE_DIR}
  fi
  if [[ ! -d ${LOG_BASE_DIR}/gc_log ]]; then
    mkdir -p ${LOG_BASE_DIR}/gc_log
  fi

  #reboot tomcat
  echo "startup server..."
  cd ${tomcat_basehome} || exit
  # 删除catalina.out日志
  rm -f logs/catalina.out
  sleep 30 &
  timerPid=$!
  ${shell_environment} bin/startup.sh

  tail -fn 500 --pid=$timerPid logs/catalina.out
}

deploy_app() {
  stop_server

  compare_version
  local -r ver=$?
  if [[ $ver -eq 1 ]]; then
    echo "use unpack file to deploy"
    archive
  elif [ $ver -eq 2 ]; then
    echo "use target war to deploy"
    unpack_and_backup
  else
    error_exit "exec failed"
  fi

  start_server
}

# 遍历unpack目录，查找最新更新日期，并与source目录的war的最后更改时间作比对
# @return 1标识最新的是解包目录;2标识最新的是target中的war
compare_version() {
  if [ ! -d $deploy_home/sptsm ] && [ ! -f "$war_source_dir"/"${war_name}" ]; then
    error_exit "$deploy_home/sptsm have not file, and also not found $war_source_dir/${war_name}"
  fi

  if [[ ! -d $deploy_home/sptsm ]]; then
    return 2
  fi

  local -r file_num=$(find $deploy_home/sptsm -type f | wc -l)
  if [[ $file_num -lt 1 ]]; then
    echo "$deploy_home/sptsm is empty, use target war to deploy"
    return 2
  fi

  if [[ ! -f "$war_source_dir"/"${war_name}" ]]; then
    echo "not found $war_source_dir/${war_name}, use unpack file to deploy"
    return 1
  fi

  # get lasted updated file
  lasted_file=$(find $deploy_home/sptsm -type f -print0 | xargs -0 ls -drt | tail -n 1)
  upt_time=$(stat -c %Y $lasted_file)
  war_upt_time=$(stat -c %Y $war_source_dir/${war_name})

  if [[ $upt_time > $war_upt_time ]]; then
    return 1
  else
    return 2
  fi
}

if [[ -n $1 ]]; then
  case $1 in
  "start")
    start_server
    ;;
  "stop")
    stop_server
    ;;
  "restart")
    stop_server
    start_server
    ;;
  "status")
    check_status
    ;;
  "deploy")
    deploy_app
    ;;
  "unpack")
    unpack_and_backup
    ;;
  *)
    echo "$usage"
    ;;
  esac
else
  echo "$usage"
fi
