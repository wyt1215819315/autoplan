layui.use(['element','layer'], function() {
    var element = layui.element;
    var layer = layui.layer;

    showUserBilibiliInfo();
    showUserNetmusicInfo();
    showUserMiyousheInfo();

    $("#addBilibiliTask").click(function (){
        addTask("bili");
    })
    $("#addMiyousheTask").click(function (){
        addTask("mihuyou");
    })
    $("#addNetmusicTask").click(function (){
        addTask("netmusic");
    })

})

function showUserBilibiliInfo() {
    $.ajax({
        url: "/api/user/bili/list",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function (result) {
            if (result.code === 200) {
                var _html = bilibiliHtml(result.data);
                $("#bilibiliShow").html(_html);
            } else {
                layer.msg("获取哔哩哔哩任务失败");
            }
        },
        error: function () {
            layer.msg("获取哔哩哔哩任务失败");
        }
    })
}

function showUserNetmusicInfo() {
    $.ajax({
        url: "/api/user/netmusic/list",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function (result) {
            if (result.code === 200) {
                var _html = netmusicHtml(result.data);
                $("#netmusicShow").html(_html);
            } else {
                layer.msg("获取网易云任务失败");
            }
        },
        error: function () {
            layer.msg("获取网易云任务失败");
        }
    })
}

function showUserMiyousheInfo() {
    $.ajax({
        url: "/api/user/mihuyou/list",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function (result) {
            if (result.code === 200) {
                var _html = miyousheHtml(result.data);
                $("#miyousheShow").html(_html);
            } else {
                layer.msg("获取米游社任务失败");
            }
        },
        error: function () {
            layer.msg("获取米游社任务失败");
        }
    })
}

function openLog(type, id) {
    var boxSize = '600px';
    if (name === "netmusic") {
        boxSize = '450px';
    }

    var index = layer.open({
        title: type + "日志查看",
        type: 2,
        content: "/getlog?type=" + type + "&id=" + id,
        maxmin: true,
        area: screen() < 2 ? ['90%', '80%'] : ['600px', boxSize],
        end: function (index, layero) {
            return true;
        }
    });
}

function runTask(name, id) {
    layer.confirm('确定要手动运行一次' + name + '任务？', {icon: 3, title: '提示'}, function (index) {
        layer.close(index);
        let loading = layer.load();
        $.ajax({
            url: "/api/user/" + name + "/run?id=" + id,
            type: 'post',
            success: function (result) {
                layer.close(loading);
                if (result.code == 200) {
                    parent.layer.msg(result.msg);
                    updateHtml(name);
                } else {
                    parent.layer.msg(result.msg);
                }
            }
        })
    });
}

function editTask(name, id) {
    layer.open({
        type: 2,
        title: '修改' + name + '任务',
        shade: 0.1,
        area: screen() < 2 ? ['90%', '80%'] : ['1200px', '600px'],
        content: name + "/edit?id=" + id,
        end: function (index, layero) {
            updateHtml(name);
            return true;
        }
    });
}

function addTask(name) {
    layer.open({
        type: 2,
        title: '添加' + name + '任务',
        shade: 0.1,
        area: screen() < 2 ? ['90%', '80%'] : ['1200px', '600px'],
        content: name + "/add",
        end: function (index, layero) {
            updateHtml(name);
            return true;
        }
    });
}

//刷新主页自己对于的内容
function updateHtml(name){
    if (name === "bili") {
        showUserBilibiliInfo();
    }

    if (name === "netmusic") {
        showUserNetmusicInfo();
    }

    if (name === "mihuyou") {
        showUserMiyousheInfo();
    }
}

//拼接米游社显示html
function miyousheHtml(data) {
    var name = "'mihuyou'";

    var _miyousheHtml = '';

    var dataLength = data.length;

    if (dataLength === 0) {
        return _miyousheHtml;
    }

    //头
    _miyousheHtml += '<fieldset class="layui-elem-field miyoushe">';
    _miyousheHtml += '<legend><i class="layui-icon search miyousheLogo"></i>米游社</legend>';
    _miyousheHtml += '<div class="layui-field-box">';
    _miyousheHtml += '<div class="layui-row layui-col-space15">';

    data.forEach(function(miyousheUser) {

        _miyousheHtml += '<div class="layui-col-xs6 layui-col-sm6 layui-col-md3">';
        _miyousheHtml += '<div class="layui-card">';
        _miyousheHtml += '<div class="layui-card-header" style="font-size: 16px;">' + miyousheUser.name;

        if (miyousheUser.endDateString != null && miyousheUser.endDateString !== "" && miyousheUser.endDateString !== undefined) {
            _miyousheHtml += '<font style="color: #b4b4b4; font-size: 14px; margin-left: 10px;">运行于：' + miyousheUser.endDateString + '</font>'
        }

        if (miyousheUser.enable == "true") {
            _miyousheHtml += '<span class="layui-badge layui-bg-blue layuiadmin-badge">开启</span>';
        } else {
            _miyousheHtml += '<span class="layui-badge layui-bg-red layuiadmin-badge">关闭</span>';
        }

        _miyousheHtml += '</div>';
        _miyousheHtml += '<div class="layui-card-body layuiadmin-card-list">';
        _miyousheHtml += '<p class="layuiadmin-big-font">用户名：';
        _miyousheHtml += '<font class="miyoushe-p-font">' + miyousheUser.miName + '</font>';

        if (miyousheUser.avatar !== undefined && miyousheUser.avatar != null && miyousheUser.avatar !== "") {
            _miyousheHtml += '<span class="layuiadmin-span-color">';
            _miyousheHtml += '<img src="' + miyousheUser.avatar + '" alt="头像" style="width: 60px; height: 60px">';
            _miyousheHtml += '</span>';
        }

        _miyousheHtml += '</p>';
        _miyousheHtml += '<p class="layuiadmin-big-font">原神UID：';
        _miyousheHtml += '<font class="miyoushe-p-font">' + miyousheUser.genshinUid + '</font>';
        _miyousheHtml += '</p>';
        _miyousheHtml += '<p style="margin-top: 10px;">';

        _miyousheHtml = addStatus(_miyousheHtml, miyousheUser.status);

        _miyousheHtml += '<span class="layuiadmin-span-color">';
        _miyousheHtml += '<button class="layui-btn layui-btn-sm" onclick="openLog(' + name + ',' + miyousheUser.id + ')">';
        _miyousheHtml += '<i class="layui-icon layui-icon-tips"></i>';
        _miyousheHtml += '</button>';
        _miyousheHtml += '<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(' + name + ',' + miyousheUser.id + ')">';
        _miyousheHtml += '<i class="layui-icon layui-icon-edit"></i>';
        _miyousheHtml += '</button>';
        _miyousheHtml += '<button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(\'mihuyou\', ' + miyousheUser.id + ')">';
        _miyousheHtml += '<i class="layui-icon layui-icon-delete"></i>';
        _miyousheHtml += '</button>';
        _miyousheHtml += '<button class="layui-btn layui-btn-warm layui-btn-sm " onclick="runTask(' + name + ',' + miyousheUser.id + ')">';
        _miyousheHtml += '<i class="layui-icon layui-icon-triangle-r"></i>';
        _miyousheHtml += '</button>';
        _miyousheHtml += '</span>';
        _miyousheHtml += '</p>';
        _miyousheHtml += '</div>';
        _miyousheHtml += '</div>';
        _miyousheHtml += '</div>';
    })

    //尾
    _miyousheHtml += '</div>';
    _miyousheHtml += '</div>';
    _miyousheHtml += '</fieldset>';

    return _miyousheHtml;
}

//拼接网易云显示html
function netmusicHtml(data) {
    var name = "'netmusic'";

    var _netmusicHtml = '';

    var dataLength = data.length;

    if (dataLength === 0) {
        return _netmusicHtml;
    }

    //头
    _netmusicHtml += '<fieldset class="layui-elem-field netmusic">';
    _netmusicHtml += '<legend><i class="layui-icon search netmusicLogo"></i>网易云</legend>';
    _netmusicHtml += '<div class="layui-field-box">';
    _netmusicHtml += '<div class="layui-row layui-col-space15">';

    data.forEach(function(netmusicUser) {

        _netmusicHtml += '<div class="layui-col-xs6 layui-col-sm6 layui-col-md3">';
        _netmusicHtml += '<div class="layui-card">';
        _netmusicHtml += '<div class="layui-card-header" style="font-size: 16px;">' + netmusicUser.name;

        if (netmusicUser.endDateString != null && netmusicUser.endDateString !== "" && netmusicUser.endDateString !== undefined) {
            _netmusicHtml += '<font style="color: #b4b4b4; font-size: 14px; margin-left: 10px;">运行于：' + netmusicUser.endDateString + '</font>'
        }

        if (netmusicUser.enable == "true") {
            _netmusicHtml += '<span class="layui-badge layui-bg-blue layuiadmin-badge">开启</span>';
        } else {
            _netmusicHtml += '<span class="layui-badge layui-bg-red layuiadmin-badge">关闭</span>';
        }

        _netmusicHtml += '</div>';
        _netmusicHtml += '<div class="layui-card-body layuiadmin-card-list">';
        _netmusicHtml += '<p class="layuiadmin-big-font">用户名：';
        _netmusicHtml += '<font class="netmusic-p-font">' + netmusicUser.netmusicName + '</font>';

        if (netmusicUser.avatar !== undefined && netmusicUser.avatar != null && netmusicUser.avatar !== ""){
            _netmusicHtml += '<span class="layuiadmin-span-color">';
            _netmusicHtml += '<img src="' + netmusicUser.avatar + '" alt="头像" style="width: 60px; height: 60px">';
            _netmusicHtml += '</span>';
        }

        _netmusicHtml += '</p>';
        _netmusicHtml += '<p class="layuiadmin-big-font">等级：';
        _netmusicHtml += '<font class="netmusic-p-font">' + netmusicUser.netmusicLevel + '</font>';
        _netmusicHtml += '</p>';
        _netmusicHtml += '<p class="layuiadmin-big-font">升级还需：';
        _netmusicHtml += '<font class="netmusic-p-font">' + Number(netmusicUser.netmusicNeedDay) + ' 天</font>';
        _netmusicHtml += '</p>';
        _netmusicHtml += '<p class="layuiadmin-big-font">剩余听歌数：';
        _netmusicHtml += '<font class="netmusic-p-font">' + Number(netmusicUser.netmusicNeedListen) + '</font>';
        _netmusicHtml += '</p>';
        _netmusicHtml += '<p style="margin-top: 10px;">';

        _netmusicHtml = addStatus(_netmusicHtml, netmusicUser.status);

        _netmusicHtml += '<span class="layuiadmin-span-color">';
        _netmusicHtml += '<button class="layui-btn layui-btn-sm" onclick="openLog(' + name + ',' + netmusicUser.id + ')">';
        _netmusicHtml += '<i class="layui-icon layui-icon-tips"></i>';
        _netmusicHtml += '</button>';
        _netmusicHtml += '<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(' + name + ',' + netmusicUser.id + ')">';
        _netmusicHtml += '<i class="layui-icon layui-icon-edit"></i>';
        _netmusicHtml += '</button>';
        _netmusicHtml += '<button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(\'netmusic\' ,' + netmusicUser.id + ')">';
        _netmusicHtml += '<i class="layui-icon layui-icon-delete"></i>';
        _netmusicHtml += '</button>';
        _netmusicHtml += '<button class="layui-btn layui-btn-warm layui-btn-sm" onclick="runTask(' + name + ',' + netmusicUser.id + ')">';
        _netmusicHtml += '<i class="layui-icon layui-icon-triangle-r"></i>';
        _netmusicHtml += '</button>';
        _netmusicHtml += '</span>';
        _netmusicHtml += '</p>';
        _netmusicHtml += '</div>';
        _netmusicHtml += '</div>';
        _netmusicHtml += '</div>';
    })

    //尾
    _netmusicHtml += '</div>';
    _netmusicHtml += '</div>';
    _netmusicHtml += '</fieldset>';

    return _netmusicHtml;
}

//拼接b站显示html
function bilibiliHtml(data) {
    var name = "'bili'";

    var _bilibiliHtml = '';

    var dataLength = data.length;

    if (dataLength === 0) {
        return _bilibiliHtml;
    }

    //头
    _bilibiliHtml += '<fieldset class="layui-elem-field bilibili">';
    _bilibiliHtml += '<legend><i class="layui-icon search bilibiliLogo"></i>哔哩哔哩</legend>';
    _bilibiliHtml += '<div class="layui-field-box">';
    _bilibiliHtml += '<div class="layui-row layui-col-space15">';

    data.forEach(function(bilibiliUser) {

        _bilibiliHtml += '<div class="layui-col-xs6 layui-col-sm6 layui-col-md3">';
        _bilibiliHtml += '<div class="layui-card">';
        _bilibiliHtml += '<div class="layui-card-header" style="font-size: 16px;">' + bilibiliUser.planName;

        if (bilibiliUser.endDateString != null && bilibiliUser.endDateString !== "" && bilibiliUser.endDateString !== undefined) {
            _bilibiliHtml += '<font style="color: #b4b4b4; font-size: 14px; margin-left: 10px;">运行于：' + bilibiliUser.endDateString + '</font>'
        }

        if (bilibiliUser.skipdailytask == "false") {
            _bilibiliHtml += '<span class="layui-badge layui-bg-blue layuiadmin-badge">开启</span>';
        } else {
            _bilibiliHtml += '<span class="layui-badge layui-bg-red layuiadmin-badge">关闭</span>';
        }

        _bilibiliHtml += '</div>';
        _bilibiliHtml += '<div class="layui-card-body layuiadmin-card-list">';
        _bilibiliHtml += '<p class="layuiadmin-big-font">用户名：';
        _bilibiliHtml += '<font class="bilibili-p-font">' + bilibiliUser.biliName + '</font>';

        if (bilibiliUser.faceImg !== undefined && bilibiliUser.faceImg != null && bilibiliUser.faceImg !== ""){
            _bilibiliHtml += '<span class="layuiadmin-span-color">';
            _bilibiliHtml += '<img src="' + bilibiliUser.faceImg + '" alt="头像" style="width: 60px; height: 60px">';
            _bilibiliHtml += '</span>';
        }

        _bilibiliHtml += '</p>';
        _bilibiliHtml += '<p class="layuiadmin-big-font">硬币：';
        _bilibiliHtml += '<font class="bilibili-p-font">' + bilibiliUser.biliCoin + '</font>';
        _bilibiliHtml += '</p>';
        _bilibiliHtml += '<p class="layuiadmin-big-font">等级：';
        _bilibiliHtml += '<font class="bilibili-p-font">' + bilibiliUser.biliLevel + '</font>';
        _bilibiliHtml += '</p>';
        _bilibiliHtml += '<p class="layuiadmin-big-font">升级还需：';
        _bilibiliHtml += '<font class="bilibili-p-font">' + Number(bilibiliUser.biliUpexp - bilibiliUser.biliExp) + '</font>';
        _bilibiliHtml += '</p>';
        _bilibiliHtml += '<p class="layuiadmin-big-font">大会员情况：';

        if (bilibiliUser.isVip == "true") {
            _bilibiliHtml += '<font class="bilibili-p-font">大会员</font>';
        } else {
            _bilibiliHtml += '<font class="bilibili-p-font">不是大会员</font>';
        }

        _bilibiliHtml += '</p>';
        _bilibiliHtml += '<p style="margin-top: 10px;">';

        _bilibiliHtml = addStatus(_bilibiliHtml, bilibiliUser.status);

        _bilibiliHtml += '<span class="layuiadmin-span-color">';
        _bilibiliHtml += '<button class="layui-btn layui-btn-sm" onclick="openLog(' + name + ',' + bilibiliUser.autoId + ')">';
        _bilibiliHtml += '<i class="layui-icon layui-icon-tips"></i>';
        _bilibiliHtml += '</button>';
        _bilibiliHtml += '<button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(' + name + ',' + bilibiliUser.autoId + ')">';
        _bilibiliHtml += '<i class="layui-icon layui-icon-edit"></i>';
        _bilibiliHtml += '</button>';
        _bilibiliHtml += '<button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(\'bili\',' + bilibiliUser.autoId + ')">';
        _bilibiliHtml += '<i class="layui-icon layui-icon-delete"></i>';
        _bilibiliHtml += '</button>';
        _bilibiliHtml += '</span>';
        _bilibiliHtml += '</p>';
        _bilibiliHtml += '</div>';
        _bilibiliHtml += '</div>';
        _bilibiliHtml += '</div>';
    })

    //尾
    _bilibiliHtml += '</div>';
    _bilibiliHtml += '</div>';
    _bilibiliHtml += '</fieldset>';

    return _bilibiliHtml;
}

//添加状态
function addStatus(_html, status) {
    if (status === "200") {
        _html += '<button class="layui-btn layui-btn-sm">运行完毕</button>';
    }

    if (status === "-1") {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-danger">运行失败</button>';
    }

    if (status === "500") {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-danger">账号信息已过期</button>';
    }

    if (status === "501") {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-normal">执行成功，账号信息更新失败</button>';
    }

    if (status === "0") {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-warm">未开启</button>';
    }

    if (status === "1") {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-disabled">任务运行中</button>';
    }

    if (status === "100" || status == null) {
        _html += '<button class="layui-btn layui-btn-sm layui-btn-primary">等待运行</button>';
    }

    return _html;
}

function removePlan(name, autoId) {
    layer.confirm('确定要删除该任务?删除后无法恢复！', {icon: 3, title: '提示'}, function (index) {
        layer.close(index);
        let loading = layer.load();
        $.post("/api/user/" + name + "/delete", {id: autoId}, function (result) {
            layer.close(loading);
            if (result.code == 200) {
                layer.msg(result.msg, {icon: 1, time: 1000}, function () {
                    updateHtml(name);
                });
            } else {
                layer.msg(result.msg, {icon: 2, time: 1000});
            }
        });
    });
}


