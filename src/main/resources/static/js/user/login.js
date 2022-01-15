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

    $("#login_btn").click(function () {
        submitform();
    });
});

//回车登录
$(document).keydown(function(e){
    if(e.keyCode == 13) {
        submitform();
    }
});

//点击刷新验证码
$("#showImageCode").click(function (){
    updateImageCode();
})

//验证码刷新函数
function updateImageCode(){
    $("#showImageCode")[0].src ='/code/image?'+Math.random()
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
    if($("#imageCode").val() == ""){
        showErrorMessage("请输入验证码！");
        return false;
    }

    $("#maskDiv").show();

    setTimeout(function () {
        $.ajax({
            async : false,
            cache : false,
            type : 'POST',
            url : '/loginProcessing',
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
                $("#maskDiv").hide();
                if (d.code == 200) {
                    window.location.href = "/index";
                }else{
                    showErrorMessage(d.msg);
                    updateImageCode();
                }
            }
        });
    }, 500);

}