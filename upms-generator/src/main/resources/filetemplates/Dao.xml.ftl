<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${packageName}.base.dao.${moduleName}.${classInfo.className}Dao">

	<!-- 可根据自己的需求，是否要使用 -->
    <resultMap id="BaseResultMap" type="${packageName}.base.entity.${moduleName}.${classInfo.className}">
    <#list fields as col>
        <#if col.priFalg>
        <id column="${col.columnName}" property="${col.fieldName}" jdbcType="${col.jdbcType}"/>
        <#else>
        <result column="${col.columnName}" property="${col.fieldName}" jdbcType="${col.jdbcType}"/>
        </#if>
    </#list>
    </resultMap>

</mapper>