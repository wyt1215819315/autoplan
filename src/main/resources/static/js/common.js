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