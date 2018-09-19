<!DOCTYPE html>
<#assign endIf = '</#if>'>
<#assign permPrefix = "${moduleName}:${classInfo.className?uncap_first}">
<html>
<head>
	<title>${classInfo.desc}</title>
	${r'<#include "/header.html">'}
	<!-- bootstrap -->
	<link rel="stylesheet" href="${r'${base}'}/statics/css/bootstrap.min.css">
</head>
<body>
<div id="app" v-cloak>
	<div class="card">
		<div id="my-toolbar" class="grid-btn">
			${r'<#if shiro.hasPermission("' + permPrefix + ':create")>'}
			<button class="btn btn-primary" @click="create"><i class="fa fa-plus"></i>&nbsp;新增</button>
            ${endIf}
            ${r'<#if shiro.hasPermission("' + permPrefix + ':del")>'}
			<button class="btn btn-primary" @click="del"><i class="fa fa-trash-o"></i>&nbsp;删除</button>
            ${endIf}
            ${r'<#if shiro.hasPermission("' + permPrefix + ':update")>'}
			<input value="true" ref="uptPermInput" type="hidden"/>
            ${endIf}
		</div>

		<div class="card-body">
			<table id="bt-table"></table>
		</div>
	</div>

	<div id="winContent" v-show="show" class="layer-form">
		<form id="${classInfo.className?uncap_first}Form">
		<#list fields as col>
			<#if !col.priFalg>
			<div class="form-group row">
				<div class="col-sm-3 col-form-label text-right"><#if (col.fieldDesc)??><#if col.fieldDesc?length gt 0>${col.fieldDesc}<#else>${col.fieldName}</#if><#else>${col.fieldName}</#if></div>
				<div class="col-sm-6">
					<input type="text" v-model="${classInfo.className?uncap_first}.${col.fieldName}" name="${col.fieldName}" id="${col.fieldName}" class="form-control" <#if col.notNull>required</#if>/>
			    </div>
				<#if col.notNull>
                <div class="text-danger col-form-label">*</div>
				</#if>
			</div>
			</#if>
		</#list>
		</form>
	</div>
</div>

<script src="${r'${base}/statics/js/' + moduleName + '/' + clientFileName + r'.js?_${.now?long}'}"></script>
</body>
</html>