function addScriptTag(src) {
    var script = document.createElement("script");
    script.setAttribute("type", "text/javascript");
    script.src = src;
    document.body.appendChild(script);
}

//方法调用值为需要引入js的路径
addScriptTag("https://cdn.jsdelivr.net/npm/marked/marked.min.js");

layui.use(['element','layer'], function() {
    var element = layui.element;
    var layer = layui.layer;

    showNoticeContent();

})

function showNoticeContent() {
    $.ajax({
        url: "/api/index/welcome-notice",
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
