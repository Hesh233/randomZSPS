layui.use(
		['form', 'layedit'], function(){
  var form = layui.form
  ,layer = layui.layer
  ,layedit = layui.layedit
  ,laydate = layui.laydate;
  
  //创建一个编辑器
  var editIndex = layedit.build('LAY_demo_editor');  
  //监听提交
//   form.on('submit(demo1)', function(data){

//   });
  layui.form.on('submit(demo1)', function(data){
// 	    layer.alert(JSON.stringify(data.field), {
// 	        title: '最终的提交信息'
// 	      });
	    var postData = JSON.stringify(data.field);
	      layui.$.ajax({
	          url: "/cj/card/login_handle",
	          type: "POST",            
	          data:postData,
	          contentType: 'application/json',
	          dataType: "json",
//	          beforeSend:function()
//              { //触发ajax请求开始时执行
//	        	  layui.$("#submit").attr("disabled","true"); //改变提交按钮上的文字并将按钮设置为不可点击
//              },
	          success: function(data){        	
	          	if(data.code==0){
	          		layer.msg("登录成功", {icon: 6}); 
	          		setTimeout(function(){
	          			location.href="/cj/card/CardPage";
	          		},1000);
	          	}
	          	else{
	              layer.msg("登录失败"+data.message, {icon: 5});
	          }
	          }
	      });
	      return false; //阻止表单跳转。如果需要表单跳转，去掉这段即可。
	});    
	});
