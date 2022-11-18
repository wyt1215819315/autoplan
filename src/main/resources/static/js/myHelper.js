layui.use(['element','layer'], function() {
    var element = layui.element;
    var layer = layui.layer;

    showUserBilibiliInfo();
    showUserNetmusicInfo();
    showUserMiyousheInfo();
    showUserXiaoMiInfo();

    $("#addBilibiliTask").click(function () {
        addTask("bili", "哔哩哔哩");
    })
    $("#addMiyousheTask").click(function () {
        addTask("mihuyou", "米游社");
    })
    $("#addNetmusicTask").click(function () {
        addTask("netmusic", "网易云");
    })
    $("#addXiaoMi").click(function () {
        addTask("xiaomi", "小米运动");
    })

})

//显示b站部分的定时任务
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

//显示网易云部分的定时任务
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

//显示米游社部分的定时任务
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

//显示小米运动部分的定时任务
function showUserXiaoMiInfo() {
    $.ajax({
        url: "/api/user/xiaomi/list",
        async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
        type: "POST",
        success: function (result) {
            if (result.code === 200) {
                var _html = xiaoMiHtml(result.data);
                $("#xiaoMiShow").html(_html);
            } else {
                layer.msg("获取小米运动任务失败");
            }
        },
        error: function () {
            layer.msg("获取小米运动任务失败");
        }
    })
}

//查看日志方法
function openLog(name, url, autoId) {
    var boxSize = '600px';
    if (url === "netmusic") {
        boxSize = '450px';
    }

    var index = layer.open({
        title: `${name}日志查看`,
        type: 2,
        content: `/getlog?type=${url}&autoId=${autoId}`,
        maxmin: true,
        area: screen() < 2 ? ['90%', '80%'] : ['600px', boxSize],
        end: function (index, layero) {
            return true;
        }
    });
}

//手动运行任务方法
function runTask(name, url, id) {
    layer.confirm(`确定要手动运行一次${name}任务？`, {icon: 3, title: '提示'}, function (index) {
        layer.close(index);
        let loading = layer.load();
        $.ajax({
            url: `/api/user/${url}/run?id=${id}`,
            type: 'post',
            success: function (result) {
                layer.close(loading);
                if (result.code == 200) {
                    parent.layer.msg(result.msg);
                    updateHtml(url);
                } else {
                    parent.layer.msg(result.msg);
                }
            }
        })
    });
}

//修改定时任务方法
function editTask(name, url, id) {
    layer.open({
        type: 2,
        title: `修改${name}任务`,
        shade: 0.1,
        area: screen() < 2 ? ['90%', '80%'] : ['1200px', '600px'],
        content: `${url}/edit?id=${id}`,
        end: function (index, layero) {
            updateHtml(url);
            return true;
        }
    });
}

//添加定时任务方法
function addTask(url, name) {
    layer.open({
        type: 2,
        title: `添加${name}任务`,
        shade: 0.1,
        area: screen() < 2 ? ['90%', '80%'] : ['1200px', '600px'],
        content: `${url}/add`,
        end: function (index, layero) {
            updateHtml(url);
            return true;
        }
    });
}

//删除定时任务方法
function removePlan(url, autoId) {
    layer.confirm('确定要删除该任务?删除后无法恢复！', {icon: 3, title: '提示'}, function (index) {
        layer.close(index);
        let loading = layer.load();
        $.post(`/api/user/${url}/delete`, {id: autoId}, function (result) {
            layer.close(loading);
            if (result.code == 200) {
                layer.msg(result.msg, {icon: 1, time: 1000}, function () {
                    updateHtml(url);
                });
            } else {
                layer.msg(result.msg, {icon: 2, time: 1000});
            }
        });
    });
}

//最后运行日期是否显示
function endDateShow(endDateString) {
    if (endDateString != null && endDateString !== "" && endDateString !== undefined) {
        return true;
    } else {
        return false;
    }
}

//用户头像是否显示
function userAvatarShow (avatar) {
    if (avatar !== undefined && avatar != null && avatar !== "") {
        return true;
    } else {
        return false;
    }
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

    if (name === "xiaomi") {
        showUserXiaoMiInfo();
    }
}

//拼接小米运动显示html
function xiaoMiHtml(data) {
    var _xiaoMiHtml = '';
    var dataLength = data.length;

    if (dataLength === 0) {
        return _xiaoMiHtml;
    }

    _xiaoMiHtml += `<fieldset class="layui-elem-field xiaomi">
                        <legend><i class="layui-icon search xiaomiLogo"></i>小米运动</legend>
                        <div class="layui-field-box">
                            <div class="layui-row layui-col-space15">
                                ${xiaomiHtmlUserInfo(data)}
                            </div> 
                        </div>
                    </fieldset>`;

    return _xiaoMiHtml;
}

//拼接小米运动用户信息显示html
function xiaomiHtmlUserInfo(data) {
    var name = "'小米运动'";
    var url = "'xiaomi'";
    let miyousheHtmlUserInfo = '';

    data.forEach(function (xiaomiUser) {

        miyousheHtmlUserInfo += `<div class="layui-col-xs12 layui-col-sm6 layui-col-md3">
                                    <div class="layui-card">
                                        <div class="layui-card-header" style="font-size: 16px;">${xiaomiUser.name}
                                            <font style="color: #b4b4b4; font-size: 14px; margin-left: 10px; ${endDateShow(xiaomiUser.endDateString) ? '' : 'display: none;'}">运行于：${xiaomiUser.endDateString}</font>
                                            <span class="layui-badge layuiadmin-badge ${xiaomiUser.enable == "true" ? 'layui-bg-blue' : 'layui-bg-red'}">${xiaomiUser.enable == "true" ? "开启" : "关闭"}</span>
                                        </div>
                                        <div class="layui-card-body layuiadmin-card-list">
                                            <p class="layuiadmin-big-font">小米运动账号：
                                                <font class="xiaomi-p-font">${xiaomiUser.phone}</font>
                                            </p>
                                            <p class="layuiadmin-big-font">当前设置的步数：
                                                <font class="xiaomi-p-font">${xiaomiUser.steps}</font>
                                            </p>
                                            <p class="layuiadmin-big-font">上一次提交的步数：
                                                <font class="xiaomi-p-font">${xiaomiUser.previousOccasion}</font>
                                            </p>
                                            <p style="margin-top: 10px;">
                                                ${addStatus(xiaomiUser.status)}
                                                <span class="layuiadmin-span-color">
                                                    <button class="layui-btn layui-btn-sm" onclick="openLog(${name}, ${url}, ${xiaomiUser.id})">
                                                        <i class="layui-icon layui-icon-tips"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(${name}, ${url}, ${xiaomiUser.id})">
                                                        <i class="layui-icon layui-icon-edit"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(${url}, ${xiaomiUser.id})">
                                                        <i class="layui-icon layui-icon-delete"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-warm layui-btn-sm " onclick="runTask(${name}, ${url}, ${xiaomiUser.id})">
                                                        <i class="layui-icon layui-icon-triangle-r"></i>
                                                    </button>
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                </div>`;
    })

    return miyousheHtmlUserInfo;
}

//拼接米游社显示html
function miyousheHtml(data) {
    var _miyousheHtml = '';
    var dataLength = data.length;

    if (dataLength === 0) {
        return _miyousheHtml;
    }

    _miyousheHtml += `<fieldset class="layui-elem-field miyoushe">
                        <legend><i class="layui-icon search miyousheLogo"></i>米游社</legend>
                        <div class="layui-field-box">
                            <div class="layui-row layui-col-space15">
                                ${miyousheHtmlUserInfo(data)}
                            </div> 
                        </div>
                    </fieldset>`;

    return _miyousheHtml;
}

//拼接米游社用户信息显示html
function miyousheHtmlUserInfo(data) {
    var name = "'米游社'";
    var url = "'mihuyou'";
    let miyousheHtmlUserInfo = '';

    data.forEach(function (miyousheUser) {

        miyousheHtmlUserInfo += `<div class="layui-col-xs12 layui-col-sm6 layui-col-md3">
                                    <div class="layui-card">
                                        <div class="layui-card-header" style="font-size: 16px;">${miyousheUser.name}
                                            <font style="color: #b4b4b4; font-size: 14px; margin-left: 10px; ${endDateShow(miyousheUser.endDateString) ? '' : 'display: none;'}">运行于：${miyousheUser.endDateString}</font>
                                            <span class="layui-badge layuiadmin-badge ${miyousheUser.enable == "true" ? 'layui-bg-blue' : 'layui-bg-red'}">${miyousheUser.enable == "true" ? "开启" : "关闭"}</span>
                                        </div>
                                        <div class="layui-card-body layuiadmin-card-list">
                                            <p class="layuiadmin-big-font">用户名：
                                                <font class="miyoushe-p-font">${miyousheUser.miName}</font>
                                                <span class="layuiadmin-span-color" style="${userAvatarShow(miyousheUser.avatar) ? '' : 'display: none;'}">
                                                    <img src="${miyousheUser.avatar}" alt="头像" style="width: 60px; height: 60px">
                                                </span>
                                            </p>
                                            <p class="layuiadmin-big-font">原神UID：
                                                <font class="miyoushe-p-font">${miyousheUser.genshinUid}</font>
                                            </p>
                                            <p style="margin-top: 10px;">
                                                ${addStatus(miyousheUser.status)}
                                                <span class="layuiadmin-span-color">
                                                    <button class="layui-btn layui-btn-sm" onclick="openLog(${name}, ${url}, ${miyousheUser.id})">
                                                        <i class="layui-icon layui-icon-tips"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(${name}, ${url}, ${miyousheUser.id})">
                                                        <i class="layui-icon layui-icon-edit"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(${url}, ${miyousheUser.id})">
                                                        <i class="layui-icon layui-icon-delete"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-warm layui-btn-sm " onclick="runTask(${name}, ${url}, ${miyousheUser.id})">
                                                        <i class="layui-icon layui-icon-triangle-r"></i>
                                                    </button>
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                </div>`;
    })

    return miyousheHtmlUserInfo;
}

//拼接网易云显示html
function netmusicHtml(data) {
    var _netmusicHtml = '';
    var dataLength = data.length;

    if (dataLength === 0) {
        return _netmusicHtml;
    }

    _netmusicHtml += `<fieldset class="layui-elem-field netmusic">
                        <legend><i class="layui-icon search netmusicLogo"></i>网易云</legend>
                        <div class="layui-field-box">
                            <div class="layui-row layui-col-space15">
                                ${netmusicHtmlUserInfo(data)}
                            </div>
                        </div>
                    </fieldset>`;

    return _netmusicHtml;
}

//拼接网易云用户信息显示html
function netmusicHtmlUserInfo(data) {
    var name = "'网易云'";
    var url = "'netmusic'";
    let netmusicHtmlUserInfo = '';

    data.forEach(function (netmusicUser) {

        netmusicHtmlUserInfo += `<div class="layui-col-xs12 layui-col-sm6 layui-col-md3">
                                <div class="layui-card">
                                    <div class="layui-card-header" style="font-size: 16px;">${netmusicUser.name}
                                        <font style="color: #b4b4b4; font-size: 14px; margin-left: 10px; ${endDateShow(netmusicUser.endDateString) ? '' : 'display: none;'}">运行于：${netmusicUser.endDateString}</font>
                                        <span class="layui-badge layuiadmin-badge ${netmusicUser.enable == "true" ? 'layui-bg-blue' : 'layui-bg-red'}">${netmusicUser.enable == "true" ? "开启" : "关闭"}</span>
                                    </div>
                                    <div class="layui-card-body layuiadmin-card-list">
                                        <p class="layuiadmin-big-font">用户名：
                                            <font class="netmusic-p-font">${netmusicUser.netmusicName}</font>
                                            <span class="layuiadmin-span-color" style="${userAvatarShow(netmusicUser.avatar) ? '' : 'display: none;'}">
                                                <img src="${netmusicUser.avatar}" alt="头像" style="width: 60px; height: 60px">
                                            </span>
                                        </p>
                                        <p class="layuiadmin-big-font">等级：
                                            <font class="netmusic-p-font">${netmusicUser.netmusicLevel}</font>
                                        </p>
                                        <p class="layuiadmin-big-font">升级还需：
                                            <font class="netmusic-p-font">${Number(netmusicUser.netmusicNeedDay)} 天</font>
                                        </p>
                                        <p class="layuiadmin-big-font">剩余听歌数：
                                            <font class="netmusic-p-font">${Number(netmusicUser.netmusicNeedListen)}</font>
                                        </p>
                                        <p style="margin-top: 10px;">
                                            ${addStatus(netmusicUser.status)}
                                            <span class="layuiadmin-span-color">
                                                <button class="layui-btn layui-btn-sm" onclick="openLog(${name}, ${url}, ${netmusicUser.id})">
                                                    <i class="layui-icon layui-icon-tips"></i>
                                                </button>
                                                <button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(${name}, ${url}, ${netmusicUser.id})">
                                                    <i class="layui-icon layui-icon-edit"></i>
                                                </button>
                                                <button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(${url}, ${netmusicUser.id})">
                                                    <i class="layui-icon layui-icon-delete"></i>
                                                </button>
                                                <button class="layui-btn layui-btn-warm layui-btn-sm" onclick="runTask(${name}, ${url}, ${netmusicUser.id})">
                                                    <i class="layui-icon layui-icon-triangle-r"></i>
                                                </button>
                                            </span>
                                        </p>
                                    </div>
                                </div>
                            </div>`;
    })

    return netmusicHtmlUserInfo;
}

//拼接b站显示html
function bilibiliHtml(data) {
    var _bilibiliHtml = '';
    var dataLength = data.length;

    if (dataLength === 0) {
        return _bilibiliHtml;
    }

    _bilibiliHtml += `<fieldset class="layui-elem-field bilibili">
                        <legend><i class="layui-icon search bilibiliLogo"></i>哔哩哔哩</legend>
                        <div class="layui-field-box">
                            <div class="layui-row layui-col-space15">
                            ${bilibiliHtmlUserInfo(data)}
                            </div>
                        </div>
                    </fieldset>`;

    return _bilibiliHtml;
}

//拼接b站用户信息显示html
function bilibiliHtmlUserInfo(data) {
    var name = "'哔哩哔哩'";
    var url = "'bili'";
    let bilibiliHtmlUserInfo = '';

    data.forEach(function (bilibiliUser) {

        bilibiliHtmlUserInfo += `<div class="layui-col-xs12 layui-col-sm6 layui-col-md3">
                                    <div class="layui-card">
                                        <div class="layui-card-header" style="font-size: 16px;">${bilibiliUser.planName}
                                            <font style="color: #b4b4b4; font-size: 14px; margin-left: 10px; ${endDateShow(bilibiliUser.endDateString) ? '' : 'display: none;'}">运行于：${bilibiliUser.endDateString}</font>
                                            <span class="layui-badge layuiadmin-badge ${bilibiliUser.enable == "true" ? 'layui-bg-blue' : 'layui-bg-red'}">${bilibiliUser.enable == "true" ? "开启" : "关闭"}</span>
                                        </div>
                                        <div class="layui-card-body layuiadmin-card-list">
                                            <p class="layuiadmin-big-font">用户名：
                                                <font class="bilibili-p-font">${bilibiliUser.biliName}</font>
                                                <span class="layuiadmin-span-color" style="${userAvatarShow(bilibiliUser.faceImg) ? '' : 'display: none;'}">
                                                    <img src="${bilibiliUser.faceImg}" alt="头像" style="width: 60px; height: 60px">
                                                </span>
                                            </p>
                                            <p class="layuiadmin-big-font">硬币：
                                                <font class="bilibili-p-font">${bilibiliUser.biliCoin}</font>
                                            </p>
                                            <p class="layuiadmin-big-font">等级：
                                                <font class="bilibili-p-font">${bilibiliUser.biliLevel}</font>
                                            </p>
                                            <p class="layuiadmin-big-font">升级还需：
                                                <font class="bilibili-p-font">${Number(bilibiliUser.biliUpexp - bilibiliUser.biliExp)}</font>
                                            </p>
                                            <p class="layuiadmin-big-font">大会员情况：
                                                <font class="bilibili-p-font">${bilibiliUser.isVip == "true" ? '大会员' : '不是大会员'}</font>
                                            </p>
                                            <p style="margin-top: 10px;">
                                                ${addStatus(bilibiliUser.status)}
                                                <span class="layuiadmin-span-color">
                                                    <button class="layui-btn layui-btn-sm" onclick="openLog(${name}, ${url}, ${bilibiliUser.autoId})">
                                                        <i class="layui-icon layui-icon-tips"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-normal layui-btn-sm" onclick="editTask(${name}, ${url}, ${bilibiliUser.autoId})">
                                                        <i class="layui-icon layui-icon-edit"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-danger layui-btn-sm" onclick="removePlan(${url}, ${bilibiliUser.autoId})">
                                                        <i class="layui-icon layui-icon-delete"></i>
                                                    </button>
                                                    <button class="layui-btn layui-btn-warm layui-btn-sm" onclick="runTask(${name}, ${url}, ${bilibiliUser.autoId})">
                                                        <i class="layui-icon layui-icon-triangle-r"></i>
                                                    </button>
                                                </span>
                                            </p>
                                        </div>
                                    </div>
                                </div>`;
    })

    return bilibiliHtmlUserInfo;
}

//添加状态
function addStatus(status) {
    let _html = '';

    if (status === "200") {
        _html = '<button class="layui-btn layui-btn-sm">运行完毕</button>';
    }

    if (status === "-1") {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-danger">运行失败</button>';
    }

    if (status === "500") {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-danger">账号信息已过期</button>';
    }

    if (status === "501") {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-normal">执行成功，账号信息更新失败</button>';
    }

    if (status === "0") {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-warm">未开启</button>';
    }

    if (status === "1") {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-disabled">任务运行中</button>';
    }

    if (status === "100" || status == null) {
        _html = '<button class="layui-btn layui-btn-sm layui-btn-primary">等待运行</button>';
    }

    return _html;
}


