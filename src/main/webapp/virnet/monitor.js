var messageJson = { 
		"flag":"monitor",
		"type":"default",
		"user":"default",
		"monitorInfo":"default",
		"content":"default",
		"experimentStatus":"",
		"groupId":"default",
		"problemList":null,
		"pingFromMonitor":"defualt",//trx
};

var websocket = null;

function initWebSocket(){
//判断当前浏览器是否支持WebSocket
	console.log("initWebSocket()");
	if ('WebSocket' in window) {	  
		websocket= new WebSocket("ws://" + host + root_path + "websck");
	}
	else {	  
		websocket = new SockJS("http://" + host + root_path + "sockjs/websck");
	}
}

this.initWebSocket();
this.initialEmoji();

// 谭睿雄2019/07/24：监控实验员连接状态
window.setInterval(function(){
		
	var ping = {"flag":"pingFromMonitor"};
	websocket.send(JSON.stringify(ping));
	
},3000)

//连接发生错误的回调方法
websocket.onerror = function(event){
	
};

//连接成功建立的回调方法
websocket.onopen = function(event){
	console.log("establish success");
	load();
}

//接收到消息的回调方法
websocket.onmessage = function(event){

	var object = JSON.parse(event.data);
	console.log(object);
	//监控小组的网络连接状态-trx
	if(object.type == "studentConnection"){
		console.log(object.userAndGroup != undefined);
		console.log(object.userAndGroup == undefined);
		if(object.userAndGroup != undefined){
			studentConnection(object.userAndGroup);
		}
		else{
			console.log("userAndGroup is undefined");
		}
		
	}
	
	//加载列表
	if (object.type == "load") {

		createFilter(object.monitorInfo);
		showDetail(object.monitorInfo["list"]);
	}
	
	//显示信息
	if (object.type == "communication"){
		setMessageInnerHTML(object);
	}
	
	//消除求助符号
	if(object.type == "disappearHelp"){
		disappearHelp(object.groupId);
	}
	
	//列表中插入实验小组
	if(object.type == "addExpGroup"){
		addExpGroup(object.monitorInfo);
	}
	
	//列表中删除实验小组
	if(object.type == "deleteExpGroup"){
		deleteExpGroup(object.groupId);
	}
	
	
}

//监控小组的网络连接状态-trx
function studentConnection(userAndName){
	console.log("userAndName:"+userAndName);
	var userAndNameList = userAndName.split('%%');
	console.log("userAndNameList:"+userAndNameList);
	console.log(userAndNameList.length);
	for(i = 0; i < userAndNameList.length - 1; i++){
		
		console.log("userAndNameList["+i+"]:"+userAndNameList[i]);
		var temp = userAndNameList[i].split("&");
		console.log("userState=='1':"+(temp[2]=="1"));
		var groupId = temp[1];
		var user = temp[0];
		
		if(temp[2]=="0"){			
			var tr = $("#monitorInfo>tbody").children().first();
			for(var j=0; j<$("#monitorInfo>tbody").children().length ; j++){
				if(tr.find(".groupId").html() == groupId){
					var p = tr.find(".connectionState");
					usertr = tr.find(".connectionState").find(".userName");
					if(usertr.html() == user){
						usertr.remove();
					}
					tr = $("<tr></tr>");
					tr.html(user);
					tr.attr("class", "userName");
					tr.appendTo(p);
				}
				tr = tr.next();
			}
		}
		if(temp[2]=="1"){
			var tr = $("#monitorInfo>tbody").children().first();
			for(var j=0; j<$("#monitorInfo>tbody").children().length ; j++){
				if(tr.find(".groupId").html() == groupId){
					usertr = tr.find(".connectionState").find(".userName");
					if(usertr.html() == user){
						usertr.remove();
					}
				}
				tr = tr.next();
			}
			console.log("remove!");
		}
	}
}
	
	
//	collapse = new collapseUtil();
//	tbody = $("#monitorInfo").find("tbody");
//	tr = $("<tr></tr>");
//	tr.appendTo(tbody);
//	td = $("<td></td>");
//	
//	collapse.createCollapse(member).appendTo(td);
//	td.appendTo(tr);


//生成信息页面
function showDetail(info){

	//初始化排序器的值
	$(".timeHead").val(0);
	$(".problem0Head").val(0);
	$(".problem1Head").val(0);
	$(".problem2Head").val(0);
	$(".helpHead").val(0);
	
	console.log(info);
	tbody = $("#monitorInfo").find("tbody");
	collapse = new collapseUtil();
	for (var i = 0; i< info.length; i++) {

		console.log(info[i]);
		tr = $("<tr></tr>");
		tr.appendTo(tbody);
		if (info[i]["courseName"] != null) {
			td = $("<td></td>");
			p = $("<p></p>");
			p.attr("class", "courseName");
			p.appendTo(td);
			p.html(info[i]["courseName"]);
			td.appendTo(tr);
		}

		if (info[i]["className"] != null) {
			td = $("<td></td>");
			p = $("<p></p>");
			p.attr("class", "className");
			p.appendTo(td);
			p.html(info[i]["className"]);
			td.appendTo(tr);
		}

		if (info[i]["groupMember"] != null) {
			td = $("<td></td>");
			var member = info[i]["groupMember"].split('#');
			console.log(info[i]["groupMember"]);
			console.log("member:"+member);
			collapse.createCollapse(member).appendTo(td);
			console.log(collapse);
			td.appendTo(tr);
		}

		if (info[i]["expName"] != null) {
			td = $("<td></td>");
			p = $("<p></p>");
			p.appendTo(td);
			p.html(info[i]["expName"]);
			td.appendTo(tr);
		}
		
		if (info[i]["ip"] != null) {
			td = $("<td></td>");
			td.attr("contenteditable", true);
			
			ip_p = $("<p></p>");
			ip_p.attr("class", "ipAddress");
			ip_p.appendTo(td);
			ip_p.html(info[i]["ip"]);
//			td.contentEditable = true;
			td.appendTo(tr);
		}

		if (info[i]["endTime"] != null) {
			td = $("<td></td>");
			p = $("<p></p>");
			p.attr("class", "clock");
			p.appendTo(td);
			var retail = timeCal(info[i]["endTime"]);
			p.html(retail);
			td.appendTo(tr);
		}

		//问题栏
		for(j=0;j<3;j++){
			td = $("<td></td>");
			p = $("<p></p>");
			p.html('<img  style="width:30px;height:30px" src="./img/no.png" alt="" />');
			p.attr("class", "problem"+j);
			p.val(0);
			p.appendTo(td);
			td.appendTo(tr);
		}


		//求助栏
		td = $("<td></td>");
		p = $("<p></p>");
		p.attr("class", "help");
		p.val(0);
		p.appendTo(td);
		td.appendTo(tr);

		//以下为跳转按钮设置
		td = $("<td></td>");
		var button = $("<button></button");
		button.attr("class", "btn btn-new");
		button.html("GO");
		button.click(function(){
			jump(messageJson.user,$(this).children(".groupId").html(),$(this).children(".expId").html());
		});


		var workgroup = $("<div></div>");
		workgroup.html(info[i]["groupId"]);
		workgroup.attr("style","display:none");
		workgroup.attr("class","groupId");

		var expId = $("<div></div>");
		expId.html(info[i]["expId"]);
		expId.attr("style","display:none");
		expId.attr("class","expId");
		
		workgroup.appendTo(button);
		expId.appendTo(button);
		button.appendTo(td);
		td.appendTo(tr);
		
		//以下为重连按钮设置
		reallocate_td = $("<td></td>");
		var reallocate_button = $("<button></button");
		reallocate_button.attr("class", "btn btn-new");
		reallocate_button.html("重启重连");
		reallocate_button.click(function(){
			sendReallocateMessage(messageJson.user,$(this).children(".groupId").html(),$(this).children(".expId").html());
		});
		
		var workgroup = $("<div></div>");
		workgroup.html(info[i]["groupId"]);
		workgroup.attr("style","display:none");
		workgroup.attr("class","groupId");

		var expId = $("<div></div>");
		expId.html(info[i]["expId"]);
		expId.attr("style","display:none");
		expId.attr("class","expId");
		
		workgroup.appendTo(reallocate_button);
		expId.appendTo(reallocate_button);
		reallocate_button.appendTo(reallocate_td);
		reallocate_td.appendTo(tr);
		
		//以下为更改机柜按钮
		change_td = $("<td></td>");
		var change_button = $("<button></button");
		change_button.attr("class", "btn btn-new");
		change_button.html("更改机柜");
		change_button.click(function(){
			sendChangeMessage(messageJson.user,$(this).children(".groupId").html(),$(this).children(".expId").html(),$('.ipAddress').html());
		});


		var workgroup = $("<div></div>");
		workgroup.html(info[i]["groupId"]);
		workgroup.attr("style","display:none");
		workgroup.attr("class","groupId");

		var expId = $("<div></div>");
		expId.html(info[i]["expId"]);
		expId.attr("style","display:none");
		expId.attr("class","expId");
		
		
		
//		ip_p.appendTo(change_button);
		workgroup.appendTo(change_button);
		expId.appendTo(change_button);
		change_button.appendTo(change_td);
		change_td.appendTo(tr);
		
		

		if(info[i]["problemList"]!=null){
			displayHelp(info[i]["groupId"],info[i]["problemList"]);
		}
		
		//连接情况-trx
		
		td = $("<td></td>");
		td.	attr("class", "connectionState");
		con = $("<p></p>");
		con.appendTo(td);
		con.html("未连接：");

		td.appendTo(tr);
		
		
		
		
		
	}
	countDown();
}


function createFilter(data){
	//data是  Map<String,List<Map<String,Object>>>格式
	//其中 list[0]是下拉框的左侧描述栏，本函数不需要
	//倒序的方法插入，注意prepend

	if(data["Class"]!=null){
		var classList = $("<td></td>");
		classList.attr("style","padding:5px 10px;")
		$("#filter").prepend(classList);
		createMutiSelect(data["Class"][1]["name"], data["Class"][1]["value"], classList);
		var Class = $("<th></th>");
		Class.html("班级");
		Class.attr("style","padding:5px;font-size:20px");
		$("#filter").prepend(Class);
	}
	if(data["Course"]!=null){
		var courseList = $("<td></td>");
		courseList.attr("style","padding:5px 10px;")
		$("#filter").prepend(courseList);
		createMutiSelect(data["Course"][1]["name"], data["Course"][1]["value"], courseList);
		var course = $("<th></th>");
		course.html("课程");
		course.attr("style","padding:5px;font-size:20px");
		$("#filter").prepend(course);
	}
}
function createMutiSelect(data, value, father){
	var select = $("<select></select>");
	select.attr("class", "form-control multiselect multiselect-primary");
	select.attr("multiple", "multiple");
	select.attr("value", value);
	
	$.each(data, function(index, value){
		var option = $("<option></option>");
		if(value["value"] != null)
			option.attr("value", value["value"]);
		if(value["selected"] != null){
			option.attr("selected", value["selected"]);
		}
		option.html(value["name"]);
		option.appendTo(select);
	});

	select.appendTo(father);
	
	select.select2({dropdownCssClass: 'dropdown-inverse'});
}

//通过过滤框过滤
function filter(){
	l=0;
	var array = new Array();
	//选择课程多选框中的内容
	var content = $("[value=Course]").val();
	if(content!=null){
		$.each(content, function(index, value){
			$.each($("[value=Course]").children("option"), function(i, val){
				if($(val).val() == value){
					array[l++] = $(val).html();
				}
			});
		});
	}
	//选择班级多选框中的内容
	content = $("[value=Class]").val();
	if(content!=null){
		$.each(content, function(index, value){
			$.each($("[value=Class]").children("option"), function(i, val){
				if($(val).val() == value){
					array[l++] = $(val).html();
				}
			});
		});
	}
	
	tbody = $("#monitorInfo").find("tbody");
	var length = tbody.children().length;
	//选中的内容都在array中
	if($.inArray("全部", array) != -1 || array.length == 0){
		//全部小组都应该显示
		var tr = tbody.children().first();
		for(i=0;i<length;i++){
			tr.css("display","");
			tr = tr.next();
		}
	}
	else{
		//主意比对表格中每一行的内容
		var tr = tbody.children().first();
		for(i=0;i<length;i++){
			if ($.inArray(tr.find(".courseName").html(), array) != -1
				|| $.inArray(tr.find(".className").html(),array) != -1) {
				
				tr.css("display","");
			}
			else{
				tr.css("display","none");
			}
			tr = tr.next();
		}
	}
}
function sort(obj){
	//根据排序规则对表格中的列进行排列
	if($(obj).val() == 0){
		console.log($(obj).val());
		$(obj).val(1);
		$(obj).find(".sort").css("box-shadow","0px 1px 6px 2px #01af55");
	}
	else{
		console.log($(obj).val());
		$(obj).val(0);
		$(obj).find(".sort").css("box-shadow","0px 1px 5px 1px #000");
	}
	
	
	//排序规则，为1则为考虑  分别为剩余时间，问题123，是否求助
	var time = 0;
	var problem0 = 0;
	var problem1 = 0;
	var problem2 = 0;
	var help = 0;
	
	//读取排序规则
	var head_tr = $("#monitorInfo>thead").children().first();
	time = head_tr.find(".timeHead").val();
	problem0 = head_tr.find(".problem0Head").val();
	problem1 = head_tr.find(".problem1Head").val();
	problem2 = head_tr.find(".problem2Head").val();
	help = head_tr.find(".helpHead").val();
	
	//行数
	tbody = $("#monitorInfo").find("tbody");
	var length = tbody.children().length;
	tr = tbody.children();
	
	// 冒泡排序
	for(var i=0;i<length-1;i++){
		for(var  j = 0; j < length-1-i; j++){			
			//根据排序规则交换行
			flag = exchange(tr.eq(j),tr.eq(j+1),time,problem0,problem1,problem2,help)
			console.log(flag);
			if(flag){
				//注意！eq顺序是在.children()确定，不由insertBefore改变，所以交换之后重新取children
				tr.eq(j+1).insertBefore(tr.eq(j));    
				tr = tbody.children();
			}
		}
	}	
}
function exchange(row_A,row_B,time,problem0,problem1,problem2,help){
	
	
	//三种条件的优先级            有无按钮 >> 问题 >> 时间
	
	//有求助按钮的优先显示
	if(help == 1 && (row_A.find(".help").val() < row_B.find(".help").val()))
		return true;
	if(help == 1 && (row_A.find(".help").val() > row_B.find(".help").val()))
		return false;
	
	//优先显示符合问题类型的
	if(problem0 == 1 && (row_A.find(".problem0").val() < row_B.find(".problem0").val()))
		return true;
	if(problem0 == 1 && (row_A.find(".problem0").val() > row_B.find(".problem0").val()))
		return false;

	if(problem1 == 1 && (row_A.find(".problem1").val() < row_B.find(".problem1").val()))
		return true;
	if(problem1 == 1 && (row_A.find(".problem1").val() > row_B.find(".problem1").val()))
		return false;
	
	if(problem2 == 1 && (row_A.find(".problem2").val() < row_B.find(".problem2").val()))
		return true;
	if(problem2 == 1 && (row_A.find(".problem2").val() > row_B.find(".problem2").val()))
		return false;
		
	//剩余时间少的优先
	if(time == 1 && timeLess(row_A.find(".clock").html(),row_B.find(".clock").html()))
		return true;
	
	return false;
}

//比较时间是不是B更小
function timeLess(timeA,timeB){
	A = timeA.split(":");
	B = timeB.split(":");
	
	console.log(parseInt(A[0]) + " " + parseInt(A[1]) + " " + parseInt(A[2]));
	console.log(parseInt(B[0]) + " " + parseInt(B[1]) + " " + parseInt(B[2]));
	
	if(A.length > B.length)
		return true;
	else if(A.length < B.length)
		return false;
	else{
		for( i=0; i<A.length; i++){
			if(parseInt(A[i])>parseInt(B[i]))
				return true;
			else if(parseInt(A[i])<parseInt(B[i]))
				return false;
		}
	}
	return false;
}

//跳转到实验平台界面
function jump(username,workgroup,expId){
	
	$.ajax({
		url:"monitor.action",
		data:{
			username : username,
			workgroup : workgroup,
			expId : expId,
			expRole : "monitor",
		},
		type:'post',      
	    dataType:'json',    
	    success:function(data){
	    	if(data["isSuccess"] == true){
	    		window.open( "http://" + host + root_path + 'exp.jsp');
	    	}
	    },
	    error:function(data){
	    	alert("error");
	    }
	});
}
////发送重连机柜函数
//function sendReallocateMessage(username,workgroup,expId){
//	
//	$.ajax({
//		url:"sendReallocateMessage.action",
//		data:{
//			username : username,
//			workgroup : workgroup,
//			expId : expId,
//			expRole : "monitor",
//			reboot : "Reboot",
//		},
//		type:'post',      
//	    dataType:'json',    
//	    success:function(data){
//	    	if(data["isSuccess"] == true){
//	    		window.open( "http://" + host + root_path + 'exp.jsp');
//	    		
//	    	}
//	    },
//	    error:function(data){
//	    	alert("error");
//	    }
//	});
//}


function collapseUtil(){
	number = 0;
	
	this.createCollapse = function(content){
		panel = $("<div></div>");
		panel.attr("class", "panel-group");
		panel.attr("id", "accordion" + number);
		
		div = $("<div></div>");
		div.appendTo(panel);
		div.attr("class", "panel panel-info");
		
		head = $("<div></div>");
		head.attr("class", "panel-heading");
		head.appendTo(div);
		
		h = $("<h4></h4>");
		h.attr("class", "panel-title");
		h.appendTo(head);
		
		a = $("<a></a>");
		a.attr("data-toggle", "collapse");
		a.attr("data-parent", "#accordion" + number);
	    a.attr("href", "#collapse" + number);
	    a.attr("class", "collapsed");
	    a.html(content[0] + " ...");
	    a.appendTo(h);
	    
	    body = $("<div></div>");
	    body.attr("id", "collapse" + number);
	    body.attr("class", "panel-collapse collapse");
	    body.appendTo(div);
	    
	    tableInner = $("<div></div>");
	    tableInner.attr("class", "panel-body");
	    tableInner.appendTo(body);
	    
	    $.each(content, function(index, value){
	    	if(value != "" || value != null){
	    		trInner = $("<div></div>");
		    	trInner.appendTo(tableInner);
		    	
		    	a0 = $("<a></a>");
		    	a0.html(value);
		    	a0.appendTo(trInner);
	    	}
	    });
	    
	    number++;
	    return panel;
	};
}

function countDown(){
	
	$(".clock").each(function(index, value) {
		time = $(value).html().split(":");
		// 超过一个小时
		if (time.length == 3) {

			if (parseInt(time[2],10) > 0){
				if(parseInt(time[2],10) - 1 < 10)
					$(value).html(time[0] + ":" + time[1] + ":0" + (parseInt(time[2],10) - 1));
				else
					$(value).html(time[0] + ":" + time[1] + ":" + (parseInt(time[2],10) - 1));
			}	
			else if (parseInt(time[1],10) > 0){
				if(parseInt(time[1],10) - 1 < 10)
					$(value).html(time[0] + ":0" +( parseInt(time[1],10) - 1 )+ ":59");
				else
					$(value).html(time[0] + ":" +( parseInt(time[1],10) - 1 )+ ":59");
			}
			else if (parseInt(time[0],10) - 1 > 0 ){
				if(parseInt(time[0],10) - 1 < 10)
					$(value).html("0" + (parseInt(time[0],10) - 1) + ":59:59");
				else
					$(value).html((parseInt(time[0],10) - 1) + ":59:59");
			}
			else
				$(value).html("59:59");
		}
		// 一个小时之内
		else if (time.length == 2) {
			
			//倒计时间到
			if(parseInt(time[1],10) == 0 && parseInt(time[0],10) == 0){
				return;
			}
			else if (parseInt(time[1],10) > 0){
				if(parseInt(time[1],10) - 1 < 10)
					$(value).html(time[0] + ":0" + (parseInt(time[1],10) - 1));
				else
					$(value).html(time[0] + ":" + (parseInt(time[1],10) - 1));
			}	
			else if (parseInt(time[0],10) > 0 ){
				if(parseInt(time[0],10) - 1 < 10)
					$(value).html("0" + (parseInt(time[0],10) - 1) + ":59");
				else
					$(value).html((parseInt(time[0],10) - 1) + ":59");
			}
		}
	});
	setTimeout(countDown,1000);
	return;
}

//计算时间差
function timeCal(endTime){
	var stopTime = new Date(endTime.replace(/\-/g, "/"));
	var currentTime = new Date();
	var retailSecond = (stopTime.getTime()-currentTime.getTime())/1000;
	
	console.log(currentTime);
	console.log(stopTime);
	console.log(retailSecond);
	
	var retail = "";
	if(Math.floor(retailSecond/3600)!=0){
		if(Math.floor(retailSecond/3600) < 10)
			retail = retail + "0" + Math.floor(retailSecond/3600) + ':';
		else
			retail = retail + Math.floor(retailSecond/3600) + ':';
		retailSecond = retailSecond%3600;
	}
	if(Math.floor(retailSecond/60)!=0){
		if(Math.floor(retailSecond/60) < 10)
			retail = retail + "0" + Math.floor(retailSecond/60) + ':';
		else
			retail = retail + Math.floor(retailSecond/60) + ':';
		retailSecond = retailSecond%60;
	}
	retail = retail + parseInt(retailSecond);
	return retail;
}

//加载监控界面
function load(){
	
	if(websocket.readyState == 1){
		messageJson.type = "load";
		if($("#username").html()!=null){
			$("#user-name").html($("#username").html());
			messageJson.user = "(管理员)" + $("#username").html();
			var mess = JSON.stringify(messageJson);
			websocket.send(mess);
		}
		else{
			setTimeout(load(),250); 
		}
	 }	 
	 else{
		console.log("wait for websocket establish");
		setTimeout(load(),250); 
	 }
}

function broadcast(){
	//给学生广播消息
	messageJson.type = "broadcast";
	messageJson.content = $("#broadcastMessage").val();
	console.log(messageJson.content);
	$("#broadcastMessage").val("");
	
	if(messageJson.content != "" && messageJson.content != "输入消息"){
		//发送
		messageJson.type = "communication";
		messageJson.flag = "experiment";
		var mess = JSON.stringify(messageJson);
        websocket.send(mess);
        messageJson.flag = "monitor";
	}
}

function sendReallocateMessage(username, workgroup, expId){
//	alert("11111 "+expId);
	$.ajax({
		url:"sendReallocateMessage.action",
		data:{
			username : username,
			workgroup : workgroup,
			expId : expId,
			expRole : "monitor",
		},
		type:'post',      
		dataType:'json',    
		success:function(data){
			if(data["isSuccess"] == true){
				//发送重连信息
				messageJson.experimentStatus = "reboot";
				messageJson.flag = "experiment";
				var mess = JSON.stringify(messageJson);
			    websocket.send(mess);
			    messageJson.flag = "monitor";	
			}
		},
		error:function(data){
			alert("error");
		}
	});
}

function sendChangeMessage(username, workgroup, expId,cabinetIp){
	
	alert("expid == "+expId);
	alert("workgroup == "+workgroup);
	alert("机柜ip == "+cabinetIp);
	$.ajax({
		url:"changeMachine.action",
		data:{
			username : username,
			workgroup : workgroup,
			expId : expId,
			expRole : "monitor",
			cabinetIP : cabinetIp,
		},
		type:'post',      
		dataType:'json',    
		success:function(data){
			if(data["isSuccess"] == true){
				//发送重连信息
				alert("change message send successful!");
				messageJson.experimentStatus = "changeCabinet";
				messageJson.flag = "experiment";
				var mess = JSON.stringify(messageJson);
			    websocket.send(mess);
			    messageJson.flag = "monitor";	
			}
		},
		error:function(data){
			alert("error");
		}
	});
}




//聊天消息显示窗口
function setMessageInnerHTML(msg){
	var msgToDisplay = $("<p></p>")
	var historyMessage = $('#historyMsg');
	date = new Date().toTimeString().substr(0, 5);
	msgToDisplay.attr("style","color:#000;font-size:12px;font-weight:bold;");
	msg.content = this.showEmoji(msg.content);
	msgToDisplay.html(msg.user+ '<span class="timespan" style="color: red;">(' + date + ') </span>'+":"+msg.content);
	historyMessage.append(msgToDisplay);
	historyMessage.scrollTop(historyMessage[0].scrollHeight);
	
	if(msg.user == "系统消息" && msg.content.indexOf(")请求帮助") && msg.problemList != null){
		//是求助信息
		displayHelp(msg.groupId,msg.problemList);
	}
}

//标志求助的小组
function displayHelp(groupId,problemList){

	var tr = $("#monitorInfo>tbody").children().first();
	for(var i=0; i<$("#monitorInfo>tbody").children().length ; i++){
		if(tr.find(".groupId").html() == groupId){
			
			var p = tr.find(".help");
			p.html('<img style="width:25px;height:41px" src="./img/handup.png" alt="" />');
			p.val(1);
			
			for(var j=0;j<3;j++){
				p = tr.find(".problem"+j);
				p.html('<img  style="width:30px;height:30px" src="./img/no.png" alt="" />');
				p.val(0);
			}
			
			problem = problemList.split("#");
			for(var k=0;k<problem.length;k++){
				problemNo = parseInt(problem[k]);
				p = tr.find(".problem"+problemNo);
				p.html('<img  style="width:30px;height:30px" src="./img/yes.png" alt="" />');
				p.val(1);
			}
			return;
		}
		tr = tr.next();
	}
}

//取消求助标志
function disappearHelp(groupId){
	var tr = $("#monitorInfo>tbody").children().first();
	for( var i=0; i<$("#monitorInfo>tbody").children().length ; i++){
		if(tr.find(".groupId").html() == groupId){
			
			var p = tr.find(".help");
			p.html("");
			p.val(0);
			
			for(var j=0;j<3;j++){
				p = tr.find(".problem"+j);
				p.html('<img  style="width:30px;height:30px" src="./img/no.png" alt="" />');
				p.val(0);
			}
			return;
		}
		tr = tr.next();
	}
	
}

//显示表情的函数		
function  initialEmoji() {
	var emojiContainer = document.getElementById('emojiWrapper'),
		docFragment = document.createDocumentFragment();
	for (var i = 1; i <= 60; i++) {
		var emojiItem = document.createElement('img');
		emojiItem.src = 'emotions/emoji/' + i + '.png';
		emojiItem.title = i;
		docFragment.appendChild(emojiItem);
	};
	emojiContainer.appendChild(docFragment);
	
	//表情控制项
	document.getElementById('emoji').addEventListener('click', function(e) {
		var emojiwrapper = document.getElementById('emojiWrapper');
		emojiwrapper.style.display = 'block';
		e.stopPropagation();
	}, false);
	
	document.body.addEventListener('click', function(e) {
		var emojiwrapper = document.getElementById('emojiWrapper');
		if (e.target != emojiwrapper) {
			emojiwrapper.style.display = 'none';
		};
	});
	 document.getElementById('emojiWrapper').addEventListener('click', function(e) {
		var target = e.target;
		if (target.nodeName.toLowerCase() == 'img') {
			var messageInput = document.getElementById('broadcastMessage');
			messageInput.focus();
			messageInput.value = messageInput.value + '[emoji:' + target.title + ']';
		};
	}, false);
}	
function showEmoji(msg) {
	var match, result = msg,
		reg = /\[emoji:\d+\]/g,
		emojiIndex,
		totalEmojiNum = document.getElementById('emojiWrapper').children.length;
	while (match = reg.exec(msg)) {
		emojiIndex = match[0].slice(7, -1);
		if (emojiIndex > totalEmojiNum) {
			result = result.replace(match[0], '[X]');
		} else {
			result = result.replace(match[0], '<img class="emoji" src="emotions/emoji/' + emojiIndex + '.png" />');//todo:fix this in chrome it will cause a new request for the image
		};
	};
	return result;
}
//清屏
function clear(){
	
	var historyMessage = $('#historyMsg');
	historyMessage.empty();
}

function addExpGroup(info){
	tbody = $("#monitorInfo").find("tbody");
	tr = $("<tr></tr>");
	tr.appendTo(tbody);
	if (info["courseName"] != null) {
		td = $("<td></td>");
		p = $("<p></p>");
		p.attr("class", "courseName");
		p.appendTo(td);
		p.html(info["courseName"]);
		td.appendTo(tr);
	}
	if (info["className"] != null) {
		td = $("<td></td>");
		p = $("<p></p>");
		p.attr("class", "className");
		p.appendTo(td);
		p.html(info["className"]);
		td.appendTo(tr);
	}
	if (info["groupMember"] != null) {
		td = $("<td></td>");
		var member = info["groupMember"].split('#');
		collapse.createCollapse(member).appendTo(td);
		td.appendTo(tr);
	}

	if (info["expName"] != null) {
		td = $("<td></td>");
		p = $("<p></p>");
		p.appendTo(td);
		p.html(info["expName"]);
		td.appendTo(tr);
	}
	if (info["endTime"] != null) {
		td = $("<td></td>");
		p = $("<p></p>");
		p.attr("class", "clock");
		p.appendTo(td);
		var retail = timeCal(info["endTime"]);
		p.html(retail);
		td.appendTo(tr);
	}
	//问题栏
	for (j = 0; j < 3; j++) {
		td = $("<td></td>");
		p = $("<p></p>");
		p.html('<img  style="width:30px;height:30px" src="./img/no.png" alt="" />');
		p.attr("class", "problem" + j);
		p.val(0);
		p.appendTo(td);
		td.appendTo(tr);
	}
	//求助栏
	td = $("<td></td>");
	p = $("<p></p>");
	p.attr("class", "help");
	p.val(0);
	p.appendTo(td);
	td.appendTo(tr);
	//以下为跳转按钮设置
	td = $("<td></td>");
	var button = $("<button></button");
	button.attr("class", "btn btn-new");
	button.html("GO");
	button.click(function() {
		jump(messageJson.user, $(this).children(".groupId").html(), $(this)
				.children(".expId").html());
	});
	var workgroup = $("<div></div>");
	workgroup.html(info["groupId"]);
	workgroup.attr("style", "display:none");
	workgroup.attr("class", "groupId");

	var expId = $("<div></div>");
	expId.html(info["expId"]);
	expId.attr("style", "display:none");
	expId.attr("class", "expId");

	workgroup.appendTo(button);
	expId.appendTo(button);
	button.appendTo(td);
	td.appendTo(tr);
	//重启按钮
	reallocate_td = $("<td></td>");
	var reallocate_button = $("<button></button");
	reallocate_button.attr("class", "btn btn-new");
	reallocate_button.html("重启重连");
	reallocate_button.click(function() {
		sendReallocateMessage(messageJson.user, $(this).children(".groupId").html(), $(this)
				.children(".expId").html());
	});
	var workgroup = $("<div></div>");
	workgroup.html(info["groupId"]);
	workgroup.attr("style", "display:none");
	workgroup.attr("class", "groupId");

	var expId = $("<div></div>");
	expId.html(info["expId"]);
	expId.attr("style", "display:none");
	expId.attr("class", "expId");
	
	
	workgroup.appendTo(reallocate_button);
	expId.appendTo(reallocate_button);
	reallocate_button.appendTo(reallocate_td);
	reallocate_td.appendTo(tr);
	
	
	//更改机柜按钮
	change_td = $("<td></td>");
	var change_button = $("<button></button");
	change_button.attr("class", "btn btn-new");
	change_button.html("更改机柜");
	alert($(this).parent().children(".ipAddress").html());
	change_button.click(function() {
		sendChangeMessage(messageJson.user, $(this).children(".groupId").html(), $(this).children(".expId").html(),$('.ipAddress').html());
	});
	
	var workgroup = $("<div></div>");
	workgroup.html(info["groupId"]);
	workgroup.attr("style", "display:none");
	workgroup.attr("class", "groupId");

	var expId = $("<div></div>");
	expId.html(info["expId"]);
	expId.attr("style", "display:none");
	expId.attr("class", "expId");
	
	
	workgroup.appendTo(change_button);
	expId.appendTo(change_button);
	change_button.appendTo(change_td);
	change_td.appendTo(tr);
	
}
function deleteExpGroup(groupId){
	tbody = $("#monitorInfo").find("tbody");
	var tr = tbody.children().first();
	for( var i=0; i<tbody.children().length ; i++){
		if(tr.find(".groupId").html() == groupId){
		    tr.remove();
			break;
		}
		tr = tr.next();
	}
	
}