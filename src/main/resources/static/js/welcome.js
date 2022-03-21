layui.use(['element','layer'], function() {
    var element = layui.element;
    var layer = layui.layer;

    //展示公告
    showNoticeContent();

    //追加编辑按钮
    setEditButtonCondition();

    $("#notice_content_edit").click(function (){
        editNoticeContent();
    })

})

function setEditButtonCondition() {
    $.ajax({
        url: "/api/user/me",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function(result) {
            if(result.code == 200){
                if (result.data.authorities[0].authority === "ROLE_ADMIN"){
                    $("#content_header").append('<button id="notice_content_edit" class="layui-btn layui-btn-sm layuiadmin-badge" style="color: #fff; margin-top: -14px">编辑</button>');
                }
            }
        },
        error: function(){}
    });
}

function showNoticeContent() {
    $.ajax({
        url: "/api/index/welcome-notice/list",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function (result) {
            if (result.code === 200) {
                var _markdown_text = result.data;
                var _html = marked.parse(_markdown_text);
                $("#notice_content").html(_html);
            } else {
                layer.msg("获取公告失败！" + result.msg);
            }
        },
        error: function () {
            layer.msg("获取公告失败！");
        }
    })
}

function editNoticeContent() {
    layer.open({
        type: 2,
        title: '编辑系统公告',
        shade: 0.1,
        area: screen() < 2 ? ['90%', '80%'] : ['1200px', '640px'],
        content: "/system-notice-edit",
        end: function () {
            showNoticeContent();
            return true;
        }
    });
}
