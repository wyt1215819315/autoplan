$.ajax({
    url: "/api/user/me",
    async: false,//关键是这个参数 是否异步请求=>false:使用同步请求
    type: "POST",
    success: function(result) {

        $(".noLoginShowMenu").show();
        $(".loginShowMenu").hide();
        $(".adminShowMenu").hide();

        if(result.code == 200){
            var data = result.data;
            $("#loginUserName").html(data.username);
            $(".noLoginShowMenu").hide();
            $(".loginShowMenu").show();

            if (data.authorities[0].authority === "ROLE_ADMIN"){
                $(".adminShowMenu").show();
            }

        } else {
            $(".loginShowMenu").hide();
            $(".adminShowMenu").hide();
        }
    },
    error: function(){}
})