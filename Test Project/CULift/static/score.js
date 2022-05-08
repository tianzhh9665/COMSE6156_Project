function populatePage() {
    var title = document.getElementById("score_title");
    for (var i = 0; i < 3; i++) {
        title.appendChild(document.createElement('br'));
    }
    var title_text = document.createElement('p');
    title_text.style = "font-size: 35px";
    title_text.innerHTML = "Congratulations!";
    title.appendChild(title_text);

    var score_div = document.getElementById("score_grade");
    score_div.appendChild(document.createElement('br'));

    var score = document.createElement('p');
    score.style = "font-size: 35px; color:red;";
    score.innerHTML = "Your score is: " + user_score + " / " + total;
    score_div.appendChild(score);

    var des_div = document.getElementById("score_des");
    des_div.appendChild(document.createElement('br'));

    var des = document.createElement('p');
    des.style = "font-size: 25px;";
    des.innerHTML = "Remember that your body best indicates if you are lifting correctly. If you know what muscle groups should be activated and can refine from watching the abundance of model examples online, you can easily set up a routine and feel confident that you are progressing!";
    des_div.appendChild(des);

    var restart_div = document.getElementById("restart_button_div");
    var restart_button = document.createElement('button');
    restart_button.type = "button";
    restart_button.id = "quiz_restart_button";
    restart_button.className = "btn btn-primary";
    restart_button.innerText = "Retake the Quiz";
    restart_div.appendChild(restart_button);

};
function toRestart() {
    param = {};
    $.ajax({
        type: "GET",
        url: "/restart",
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(param),
        success: function(result){
            if (result["code"] == 200) {
                window.location.href = "/quizhome";
            }
        },
        error: function(request, status, error){
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });
};
$(document).ready(function(){
    populatePage();
    document.getElementById("quiz_restart_button").addEventListener("click", toRestart);
});