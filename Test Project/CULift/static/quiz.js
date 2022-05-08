function populateQuiz() {
    var quiz_item = quiz;
    var total_item = total;

    for (var i = 0; i < 3; i++) {
        document.getElementById("quiz_title").appendChild(document.createElement('br'));
    }
    var title_text = document.createElement('p');
    title_text.style = "font-size: 35px";
    title_text.innerHTML = "Question " + quiz_item["id"] + " / " + total_item;
    document.getElementById("quiz_title").appendChild(title_text);

    var quiz_content = document.getElementById("quiz_content");
    quiz_content.appendChild(document.createElement('br'));
    content_text = document.createElement('p');
    content_text.style = "font-size: 20px";
    content_text.innerHTML = quiz_item["content"];
    quiz_content.appendChild(content_text);

    var quiz_video = document.getElementById("quiz_video");
    //quiz_video.appendChild(document.createElement('br'));
    var video = document.createElement('video');
    var source = document.createElement('source');
    video.appendChild(source);
    source.src = "/display/" + quiz_item["video"];
    video.autoplay = false;
    video.controls = true;
    video.height = 240;
    video.width = 360;
    quiz_video.appendChild(video);

    var quiz_options = quiz_item["options"];
    var quiz_option_div = document.getElementById("quiz_options");
    quiz_option_div.appendChild(document.createElement('br'));
    for (var i = 1; i <= 4; i++) {
        var option = quiz_options[i];
        var option_div = document.createElement('div');
        quiz_option_div.appendChild(option_div);

        var label = document.createElement('label');
        label.for = "option" + i;
        label.style = "font-size: 20px;";
        label.innerHTML = "<input type='radio' name='option' value='" + i + ":" + option + "' id='option" + i + "' /> " + option;
        option_div.appendChild(label);
    }

    submit_div = document.getElementById("quiz_submit");
    var submit_button = document.createElement('button');
    submit_button.type = "button";
    submit_button.id = "quiz_submit_button";
    submit_button.className = "btn btn-warning";
    submit_button.innerText = "Submit";
    submit_div.appendChild(submit_button);

    prev_div = document.getElementById("prev_button");
    var prev_button = document.createElement('button');
    prev_button.type = "button";
    prev_button.id = "quiz_prev_button";
    prev_button.className = "btn btn-primary";
    prev_button.innerText = "<-- Prev";
    prev_div.appendChild(prev_button);

    next_div = document.getElementById("next_button");
    var next_button = document.createElement('button');
    next_button.type = "button";
    next_button.id = "quiz_next_button";
    next_button.className = "btn btn-danger";
    next_button.innerText = "Next -->";
    next_div.appendChild(next_button);

    preProcess(quiz_item, total_item, submit_button, prev_button, next_button);

};
function preProcess(quiz_item, total_item, submit_button, prev_button, next_button) {
    if (quiz_item["id"] == 1) {
        prev_button.disabled = true;
    }
    if (quiz_item["is_answered"] == 0) {
        next_button.disabled = true;
    }
    if (quiz_item["is_answered"] != 0) {
        submit_button.disabled = true;
        document.getElementById("option" + quiz_item["user_answer"]).checked = true;
        for (var i = 1; i <= 4; i++) {
            document.getElementById("option" + i).disabled = true;
        }

        var result_div =  document.getElementById("quiz_result");
        //result_div.appendChild(document.createElement('br'));

        var result_text = document.createElement('p');
        result_text.style = "font-size: 20px;text-align: center;";
        result_text.innerHTML = quiz_item["result"];
        if (quiz_item["result"].includes("Congratulations")) {
            result_text.style = "color: green"
            console.log('green')
        }
        else {
            result_text.style = "color: red"
            console.log('red')
        }
        console.log('done')
        result_div.appendChild(result_text);

        var result_text2 = document.createElement('p');
        result_text2.style = "font-size: 20px; color: red;text-align: center;";
        result_text2.innerHTML = "Also, you can click <a href='" + "/lesson/" + quiz_item["refer_id"] + "'> HERE</a> to go back to the corresponding lesson materials.";
        result_div.appendChild(result_text2);

        document.getElementById("quiz_submit").remove();
    }
}
function toPrev() {
    window.location.href = "/quiz/" + (quiz["id"] - 1);
};
function toNext() {
    if (quiz["id"] == total) {
        window.location.href = "/score";
    } else {
        window.location.href = "/quiz/" + (quiz["id"] + 1);
    }
};
function submitAnswer(answer) {
    $.ajax({
        type: "POST",
        url: "/grade",
        dataType : "json",
        contentType: "application/json; charset=utf-8",
        data : JSON.stringify(answer),
        success: function(result){
            document.getElementById("quiz_next_button").disabled = false;
            document.getElementById("quiz_submit_button").disabled = true;
            document.getElementById("quiz_submit").remove();
            var result_div =  document.getElementById("quiz_result");
            //result_div.appendChild(document.createElement('br'));

            var result_text = document.createElement('p');
            result_text.innerHTML = result["result"];
            if (result["result"].includes("Congratulations")) {
                result_text.style = "color: green;font-size: 20px;text-align: center;"
            }
            else {
                result_text.style = "color: red;font-size: 20px;text-align: center;"
            }
            result_div.appendChild(result_text);

            var result_text2 = document.createElement('p');
            result_text2.style = "font-size: 20px; color: red;text-align: center;";
            result_text2.innerHTML = "Also, you can click <a href='" + "/lesson/" + result["refer_id"] + "'> HERE</a> to go back to the corresponding lesson materials.";
            result_div.appendChild(result_text2);
        },
        error: function(request, status, error){
            console.log(request)
            console.log(status)
            console.log(error)
        }
    });
};
function toSubmit() {
    var checkedOption = 0;
    var found = 0
    for (var i = 1; i <= 4; i++) {
        if (document.getElementById("option" + i).checked == true) {
            checkedOption = i;
            found = 1;
            break;
        }
    }
    if (found == 0) {
        alert("Please select your answer before submitting it!");
        return;
    }

    var answer = {};
    answer["quiz_id"] = quiz["id"];
    answer["user_answer"] = checkedOption;

    submitAnswer(answer);
};
$(document).ready(function(){
    populateQuiz();
    if (document.getElementById("quiz_submit_button") != null) {
        document.getElementById("quiz_submit_button").addEventListener("click", toSubmit);
    }
    if (quiz["id"] == total) {
        document.getElementById("quiz_next_button").innerHTML = "Score";
    }
    document.getElementById("quiz_prev_button").addEventListener("click", toPrev);
    document.getElementById("quiz_next_button").addEventListener("click", toNext);
});