$(document).ready(function() {

    if (top != window){
        top.location.href = window.location.href;
    }

    const labels = document.querySelectorAll('.form-control label')

    labels.forEach(label => {
        label.innerHTML = label.innerText
            .split('')
            .map((letter, idx) => `<span style="transition-delay:${idx * 50}ms">${letter}</span>`)
            .join('')
    })

    $("#reg_btn").click(function () {
        submitform();
    });

    //点击刷新验证码
    $("#showImageCode").click(function (){
        updateImageCode();
    })
});

//回车注册
$(document).keydown(function(e){
    if(e.keyCode == 13) {
        submitform();
    }
});

//验证码刷新函数
function updateImageCode(){
    $("#imageCode").val('');
    var captcha = document.getElementById("showImageCode");
    captcha.src ='/code/image?'+Math.random()
}

function showErrorMessage(text){
    $("#text").text(text);
}

function submitform(){
    $("#text").text("");
    if($("#username").val() == ""){
        showErrorMessage("请输入用户名！");
        return false;
    }
    if($("#password").val() == ""){
        showErrorMessage("请输入密码！");
        return false;
    }
    if($("#repass").val() == ""){
        showErrorMessage("请输入确认密码！");
        return false;
    }
    if($("#imageCode").val() == ""){
        showErrorMessage("请输入验证码！");
        return false;
    }

    setTimeout(function () {
        $.ajax({
            async : false,
            cache : false,
            type : 'POST',
            url : '/reg',
            data : {
                'username': $("#username").val(),
                'password': $("#password").val(),
                'imageCode': $("#imageCode").val()
            },
            error : function(d) {
                $("#maskDiv").hide();
                showErrorMessage(d.responseJSON.msg);
                updateImageCode();
            },
            success : function(d) {
                if (d.code == 200) {
                    alert("注册成功，请登录");
                    window.location.href = "/login";
                }else{
                    showErrorMessage(d.msg);
                    updateImageCode();
                }
            }
        });
    }, 500);

}