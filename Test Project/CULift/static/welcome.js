function displayWelcome(){
    //empty old data

    console.log('Hey')

    var title_text = document.createElement('p')
    title_text.innerHTML = "Introduction to Resistance Training"
    document.getElementById("welcomeTitle").appendChild(title_text)

    var content_text = document.createElement('p')
    content_text.innerHTML = "The time one needs to see results from resistance training can make it frustrating to know if you are doing it correctly. Also with the possibilities of injuries make learning process daunting. Here we will teach you the basics of a push-up to troubleshoot the correct form and learn how to go about learning new exercises."
    document.getElementById("welcomeContent").appendChild(content_text)

    var image_text = document.createElement('img')
    image_text.src = "https://cdn.prod.openfit.com/uploads/2019/11/11131048/how-many-push-ups-in-a-day.jpg"
    image_text.alt = "Man and woman doing push-ups"
    image_text.className = ("welcome-image")
    document.getElementById("welcomeImage").appendChild(image_text)

    var next_text = document.createElement('p')
    next_text.style = "font-size: 20px; text-align: center;";
    next_text.innerHTML = "Click the button below to start your learning!"
    document.getElementById("welcomeNext").appendChild(next_text)

    var next_text_2 = document.createElement('p')
    next_text_2.style = "font-size: 20px; text-align: center;";
    next_text_2.innerHTML = "And remember you can always use the navigation bar on the top to navigate";
    document.getElementById("welcomeNext").appendChild(next_text_2)

    let nextButton='<button type="button" id="nextBtn" class="btn btn-primary center">Next: Lessons</button>'
    $("#buttonDiv").append(nextButton)
}

$(document).ready(function(){
    displayWelcome()
    //also handler for button click //
    $("#buttonDiv").on("click", "#nextBtn", function(){
        window.location.href = "/lesson/1"
    })
})