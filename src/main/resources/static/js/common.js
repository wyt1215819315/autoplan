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
