
$(document).ready(function(){
	
	//数据库类型切换
	$("select[name='database.type']").change(function(){
		var value = $(this).val();
		var option = $(this).find("option[value='" + value + "']");
		$("input[name='database.driver']").val(option.attr("_driver"));
		$("input[name='database.port']").val(option.attr("_port"));
		$("input[name='database.username']").val(option.attr("_username"));
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	
	$("input[name='database.url']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	$("input[name='database.port']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		var port = $(this).val();
		if(!port){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		//端口号必须为正整数且必须在1-65535之间
		//检查发件服务器端口是否是数字
		var reg = new RegExp(regexp.constants.integer);
		if(!reg.test(port)){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		//检查发件服务器端口是否超过限制(1至65535)
		var port = parseInt(port);
		if(port < 1 || port > 65535){
			$(this).parent().parent().addClass("has-warning");
			return;
		}
		
		reloadDatabases();
		reloadConnectionUrl();
	});
	$("input[name='database.username']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		reloadDatabases();
	});
	$("input[name='database.password']").change(function(){
		$(this).parent().parent().removeClass("has-warning");
		if(!$(this).val()){
			$(this).parent().parent().addClass("has-warning");
		}
		reloadDatabases();
	});
	$("select[name='database.databaseName']").change(function(){
		reloadConnectionUrl();

		$('#selectTableNameTable').bootstrapTable('removeAll');
		//重新加载所有的表
		if($(this).val()){
			$.get("/generator/connection/findTables", $("#generatorForm").serialize(), function(data){
				//重新加载表之前设置一个标识位
				$("input[name='lodingTables']").val(1);

				$('#selectTableNameTable').bootstrapTable('load',data);
			});
		}
		
	}).trigger("change");
	
});
