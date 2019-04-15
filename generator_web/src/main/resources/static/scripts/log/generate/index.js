$(document).ready(function(){
	$('#logGenerateTable').bootstrapTable({
		url: '/log/generate/getList',
		height: $("body .bootstrapTable").height(),
		dataField: "content",
		pagination: true,
		totalField: "totalElements",
		singleSelect: true,
		clickToSelect: true,
		showRefresh: true,
		search: true,
		searchOnEnterKey: true,
		sidePagination: 'server',
		pageNumber: 1,
		sortName: 'generateStartDate',
		sortOrder: 'desc',
		pageList: [10, 25, 50, 100],
	    columns: [
	    	{field: '',title: '序号',formatter : function(value, row, index){return index + 1;}}, 
	    	{field: 'generateStartDate',title: '开始时间', sortable: true}, 
	    	{field: 'generateStopDate',title: '结束时间', sortable: true}, 
	    	{field: 'generateTime',title: '耗时', sortable: true}, 
//	    	{field: 'generateParameterDatabase',title: '数据库参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
//	    	{field: 'generateParameterSsh',title: 'SSH连接参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
//	    	{field: 'generateParameterParameter',title: '配置参数', sortable: true,formatter : function(value, row, index){
//					return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
//				}
//	    	}, 
	    	{field: 'generateResult',title: '生成结果', sortable: true,formatter : function(value, row, index){
	    			return value == 1 ? '成功' : '失败';
	    		}
	    	}, 
	    	{field: 'failReason',title: '失败原因', sortable: true,formatter : function(value, row, index){
		    		if(value){
		    			return "<span style='overflow: hidden;text-overflow:ellipsis;white-space: nowrap;width:100px;display: block;' title='" + value + "'>" + value + "</span>";
		    		}
    				return value;
    			}
	    	}, 
	    	{field: 'id',title: '查看详情', formatter : function(value, row, index){
					return "<input type='button' id='" + value + "' value='查看详情'/>";
				}
	    	}, 
	    	//{field: 'exception',title: '异常信息', sortable: true}, 
	    ],
	    onLoadSuccess: function(rows){
	    	$("table#logGenerateTable input[type='button']").each(function(){
				var button = $(this);
				button.click(function(){
					$.modal({
						title: "生成代码日志详情",
						cancelButtonTitle: "关闭",
						width: 900,
						body: function(){
							var body = null;
							$.ajax({
								type: "get", 
							    url: "/log/generate/detail/" + button.attr("id"), 
							    cache:false, 
							    async:false, 
							    success: function(data){ 
							    	body = data;
							    } 
							});
							return body;
						},
						beforeShow: function(modal){
							modal.find("button.ok").remove();
							//添加重新生成按钮
							var generateButton = $("<button type='button' class='btn btn-primary'>重新生成</button>");
							modal.find("div.modal-footer").prepend(generateButton);
							
							generateButton.click(function(){
								$.fileDownload("/log/generate/generate", {
									data:{id: button.attr("id"), "_csrf": window.top.$("meta[name='_csrf']").attr("content")},
									httpMethod: 'POST',
									prepareCallback: function(url){
										console.debug("开始生成代码");
									},
									successCallback: function(url){
										$.alert("生成代码成功");
									},
									failCallback: function(html, url, error){
										$.alert("生成代码失败：" + (error ? ":" + error.message : ""));
									},
								});
							});
						},
						modalVisibleCallback: function(modal){
							modal.find("div.detailContainer").height(modal.find("div.modal-body").height() - 32);
						}
					});
				});
			});
	    	
	    	//鼠标放上去增加提示信息
	    	$("table#logGenerateTable tbody tr").each(function(index){
	    		if(rows && rows.content && rows.content.length > 0){
	    			var row = rows.content[index];
		    		
		    		var title = "开始时间：" + row.generateStartDate + "\r\n";
		    		title = title + "结束时间：" + row.generateStopDate + "\r\n";
		    		title = title + "耗时：" + row.generateTime + "\r\n";
		    		title = title + "生成结果：" + (row.generateResult == 1 ? '成功' : '失败') + "\r\n";
		    		title = title + "失败原因：" + (row.failReason ? row.failReason : '') + "\r\n";
		    		
		    		$(this).attr("title",title);
	    		}
	    	});
	    }
	});
});
