function populateQuizHomePage() {
    var title = document.getElementById("quiz_home_title");
    for (var i = 0; i < 3; i++) {
        title.appendChild(document.createElement('br'));
    }
    var title_text = document.createElement('p');
    title_text.style = "font-size: 35px";
    title_text.innerHTML = "Quiz: Watching a video and answer the question";
    title.appendChild(title_text);

    var des = document.getElementById("quiz_home_des");
    des.appendChild(document.createElement('br'));
    var des_text = document.createElement('p');
    des_text.style = "color:red; font-size: 25px;text-align: center;";
    des_text.innerHTML = "You are about to start the Quiz Section. ";

    des.appendChild(des_text);

    var des_text_2 = document.createElement('p');
    des_text_2.style = "font-size: 20px;text-align: center;";
    var text_2 = "There are 6 questions in total. For each question, you will watch a short video of people doing";
    des_text_2.innerHTML = text_2 + " pushup. Answering the question by selecting which part of their body is off in that video."

    des.appendChild(des_text_2);

    var quiz_start = document.getElementById("quiz_start");
    for (var i = 0; i < 2; i++) {
        quiz_start.appendChild(document.createElement('br'));
    }
    var start_button = document.createElement('button');
    start_button.type = "button";
    start_button.id = "quiz_start_button";
    start_button.className = "btn btn-primary";
    start_button.innerText = "Start Quiz";
    quiz_start.appendChild(start_button);
};
function toQuiz() {
    param = {};
    $.ajax({
        type: "GET",
        url: "/getPendingQuizId",
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(param),
        success: function(result){
            pending_id = result["id"];
            if (pending_id == -1) {
                window.location.href = "/score";
            } else {
                window.location.href = "/quiz/" + pending_id;
            }
        },
        error: function(request, status, error){
            console.log(request);
            console.log(status);
            console.log(error);
        }
    });
};
function getPendingQuizId(pending_id) {
    param = {};
    $.ajax({
        type: "GET",
        url: "/getPendingQuizId",
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(param),
        success: function(result){
            pending_id = result["id"];
            console.log(pending_id);
            if (pending_id == -1) {
                document.getElementById("quiz_start_button").innerHTML = "View Your Score";
            } else if (pending_id == 1) {
                document.getElementById("quiz_start_button").innerHTML = "Start Quiz";
            } else {
                document.getElementById("quiz_start_button").innerHTML = "Resume Quiz";
            }
        },
        error: function(request, status, error){
            console.log(request);
            console.log(status);
            console.log(error);
        }
    });
}
$(document).ready(function(){
    var pending_id = 0;
    populateQuizHomePage();
    document.getElementById("quiz_start_button").addEventListener("click", toQuiz);
    getPendingQuizId(pending_id);
});