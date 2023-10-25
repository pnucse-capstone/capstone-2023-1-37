// const signup = document.getElementById("sign-up");
// signin = document.getElementById("sign-in");
// loginin = document.getElementById("login-in");
// loginup = document.getElementById("login-up");
//
// signup.addEventListener("click", () => {
//     loginin.classList.remove("block");
//     loginup.classList.remove("none");
//
//     loginin.classList.add("none");
//     loginup.classList.add("block");
// })
//
// signin.addEventListener("click", () => {
//     loginin.classList.remove("none");
//     loginup.classList.remove("block");
//
//     loginin.classList.add("block");
//     loginup.classList.add("none");
// })

var compare_result = false;

function fn_compare_pwd(){
    var pwd1 = $("#password1").val();
    var pwd2 = $("#password2").val();
    var $s_result = $("#s_result");

    if(pwd1 == pwd2){
        compare_result = true;
        $s_result.text("비밀번호가 일치합니다.");
        return;
    }
    compare_result = false;
    $s_result.text("비밀번호가 일치하지 않습니다.");
}