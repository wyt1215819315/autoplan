// 判断页面大小,
function screen() {
    //获取当前窗口的宽度
    var width = $(window).width();
    if (width > 1200) {
        return 3;   //大屏幕
    } else if (width > 992) {
        return 2;   //中屏幕
    } else if (width > 768) {
        return 1;   //小屏幕
    } else {
        return 0;   //超小屏幕
    }
}

function showBtnSize(){
    if (screen() === 1) {
        $(".layui-btn").removeClass("layui-btn-xs").addClass("layui-btn-sm");
    }
    if (screen() === 0) {
        $(".layui-btn").removeClass("layui-btn-sm").addClass("layui-btn-xs");
    }
}

/**
 * 获取Get请求的参数
 * @param name
 * @returns
 */
function GetQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);//search,查询？后面的参数，并匹配正则
    if(r!=null){
        return decodeURI(r[2]);
    }
    return null;
}

function getStatus(status) {
    if (status === "200") {
        return '<button class="layui-btn layui-btn-xs">运行完毕</button>';
    }

    if (status === "-1") {
        return '<button class="layui-btn layui-btn-xs layui-btn-danger">运行失败</button>';
    }

    if (status === "500") {
        return '<button class="layui-btn layui-btn-xs layui-btn-danger">账号信息已过期</button>';
    }

    if (status === "501") {
        return '<button class="layui-btn layui-btn-xs layui-btn-normal">执行成功，账号信息更新失败</button>';
    }

    if (status === "0") {
        return '<button class="layui-btn layui-btn-xs layui-btn-warm">未开启</button>';
    }

    if (status === "1") {
        return '<button class="layui-btn layui-btn-xs layui-btn-disabled">任务运行中</button>';
    }

    if (status === "100" || status == null) {
        return '<button class="layui-btn layui-btn-xs layui-btn-primary">等待运行</button>';
    }
}