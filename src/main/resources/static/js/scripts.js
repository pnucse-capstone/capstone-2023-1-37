/*!
* Start Bootstrap - Simple Sidebar v6.0.6 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
// 
// Scripts
//

function email_chk() {

    var email = $('#email').val();
    alert("check");
    console.log("check_log"+email);

    $.ajax({
        type : "post",
        url : "/user/check",
        data : {"email" : email},
        dataType : "JSON"
    })
}

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }
});

$('#dropdownBtn').on('show.bs.dropdown', function () {
    //클릭하는 순간 하위 버튼들이 보여질 때
})

$('#dropdownBtn').on('shown.bs.dropdown', function () {
    //하위 버튼들이 다 보여지고 난 뒤
})

$('#dropdownBtn').on('hide.bs.dropdown', function () {
    //하위 버튼이 닫혔을 때
})

$('#dropdownBtn').on('hidden.bs.dropdown', function () {
    //하위 버튼이 닫힌 후
})