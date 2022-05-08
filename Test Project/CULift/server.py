from flask import Flask
from flask import render_template
from flask import Response, request, jsonify, redirect, url_for
app = Flask(__name__)


#LESSON TEMPLATE DATA
lesson_data = {
    "1": {
        "id": 1,
        "title": "Setup",
        "content": "Hands should be slightly outside shoulder-width apart at chest level. Feet should be hip-width apart and parallel to each other, not turned inward or outward. Hips should be in line with the shoulders, and the lower back should have a neutral curve, not completely flat, but not overly curved either. To assist with keeping proper lower back alignment, slim your waistline by trying to pull your belly button in and tightening your abdominal muscles. The head should be positioned so the ears are in line with the shoulders. They should not drop down toward the floor or looking up in front of the body."
    },
    "2": {
        "id": 2,
        "title": "Start",
        "content": "Engage the core (belly button to spine and tighten abdominals). Squeeze the glutes. Keeping pressure through the hands, bend the elbows to lower the chest, hips, and head toward the ground- together as one."
    },
    "3": {
        "id": 3,
        "title": "Middle",
        "content": "Get as close to the floor or wall as possible (nose, chest and belly button should be at the same level/height). Continue to squeeze the glutes and engage the core. Keep constant pressure through the hands into the floor."
    },
    "4": {
        "id": 4,
        "title": "Finish",
        "content": "Engage the pectorals and imagine you're pushing the ground away from you. Keep your energy focused in your core and NOT in your toes. Continue to push up, ending, and at available range of motion in the arms while engaging your triceps."
    },
    "5": {
        "id": 5,
        "title": "Muscles",
        "content": "The following diagram shows all of the muscles groups mentioned in the previous slides. Click on them to review when they should be worked throughout the pushup!"
    }
}

lesson_pics = {
    "hands": {
        "narrow": "While narrow hands are not necessarily incorrect, this pushup variation is much too advanced for beginners. This hand placement will result in deeper activation of muscles, compared to the normal hand placement.",
        "neutral": "This is an example of perfect hand placement. You should strive for this position every time.",
        "wide": "While wide hands are not necessarily incorrect, this pushup variation results in only working your biceps, and nothing else. For beginners it is important to practice with the normal positioning first before moving into variations."
    },
    "elbows": {
        "normal": "This is an example of perfect elbow placement, right around a 45 degree angle from the shoulder. You should strive for this position every time.",
        "wide": "Although the elbows arre not too flared out, you are still putting unwanted stress on joints compared to the normal elbow positioning",
        "extrawide": "Flaring your elbows out in this way puts unwanted stress on the shoulder and elbow joint, and reduces chest involvement."
    },
    "back": {
        "arch": "This is a sign of a weak core, resulting in your glutes beings raised making it easier to hold the position. Remember that it is more important to have proper form rather than more reps, so you should stop the workout once your form goes awry.",
        "normal": "This is an example of a perfectly neutrual back. You should strive for this position every time.",
        "dip": "This is a sign of a weak core, or not engaging the core in general. Another thing here to remeber is that your glutes need to be activated as well, which should help straigten your back."
    }
}

# dict recording user's answer of each question
user_answers = {}

# quiz question detail information
quiz_data = {
    "1": {
        "id": 1,
        "content": "Watch the video below. Which part of the body (man on the right) is off here?",
        "options": {1: "Core", 2: "Neck", 3: "Glutes", 4: "Tricep"},
        "correct_answer": 1,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video":"core.mp4",
        "refer_id": 2
    },
    "2": {
        "id": 2,
        "content": "Watch the video below. Which part of the body (woman on the left) is off here?",
        "options": {1: "Hands", 2: "Glutes", 3: "Shoulders", 4: "Deltoids"},
        "correct_answer": 2,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video":"glutes.mp4",
        "refer_id": 2
    },
    "3": {
        "id": 3,
        "content": "Watch the video below. Which part of the body is off here?",
        "options": {1: "Shoulders", 2: "Pectorals", 3: "Neck", 4: "Hands"},
        "correct_answer": 2,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video": "chest.mp4",
        "refer_id": 4
    },
    "4": {
        "id": 4,
        "content": "Watch the video below. Which part of the body is off here?",
        "options": {1: "Shoulders", 2: "Glutes", 3: "Hands", 4: "Deltoids"},
        "correct_answer": 3,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video": "hand_elbow stance.mp4",
        "refer_id": 1
    },
    "5": {
        "id": 5,
        "content": "Watch the video below. Which part of the body is off here?",
        "options": {1: "Shoulders", 2: "Hands", 3: "Pectorals", 4: "Neck"},
        "correct_answer": 4,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video": "neck.mp4",
        "refer_id": 1
    },
    "6": {
        "id": 6,
        "content": "Watch the video below. Which part of the body is off here?",
        "options": {1: "Hands", 2: "Back", 3: "Shoulders", 4: "Glutes"},
        "correct_answer": 2,
        "is_answered": 0,
        "user_answer": 0,
        "result": "",
        "video": "back_hands.mp4",
        "refer_id": 2
    }
}

# recording how many questions the user answers correctly
correct_answer_count = 0

# recording the pending quiz id
current_quiz_id = 1

# ROUTES
@app.route('/', methods=['GET','POST'])
def welcome(): 
    return render_template('welcome.html') #insert var here if needed
    
@app.route('/lesson/<id>')
def lesson(id=None):
    global lesson_data

    lesson = lesson_data[str(id)]


    return render_template('lesson.html', lesson = lesson, lesson_pics = lesson_pics)


# quiz home page route
@app.route('/quizhome')
def quiz_home():
    return render_template('quizhome.html')

# quiz page route
@app.route('/quiz/<id>')
def quiz(id=None):

    global quiz_data
    global correct_answer_count
    global current_quiz_id
    
    quiz = quiz_data[str(id)]
    total = len(quiz_data)
    
    return render_template('quiz.html',quiz=quiz, total=total)

# submit quiz answer and record the answer
@app.route('/grade', methods=['POST'])
def grade():
    global quiz_data
    global correct_answer_count
    global current_quiz_id
    global user_answers

    request_data = request.get_json()
    quiz_id = int(request_data["quiz_id"])
    user_answer = int(request_data["user_answer"])
    current_quiz_id = quiz_id + 1
    result = ""

    if user_answer == quiz_data[str(quiz_id)]["correct_answer"]:
        if quiz_id == len(quiz_data):
            result = "Congratulations, you got the correct answer! Click [Score] to see what you did in the quiz."
        else:
            result = "Congratulations, you got the correct answer! Click [Next] to proceed to the next question."
        quiz_data[str(quiz_id)]["result"] = result
        correct_answer_count += 1
    else:
        if quiz_id == len(quiz_data):
            result = "You are almost there! The correct answer is [" + quiz_data[str(quiz_id)]["options"][quiz_data[str(quiz_id)]["correct_answer"]] + "]. Click [Score] to see what you did in the quiz."
        else:
            result = "You are almost there! The correct answer is [" + quiz_data[str(quiz_id)]["options"][quiz_data[str(quiz_id)]["correct_answer"]] + "]. Click [Next] to proceed to the next question."
        quiz_data[str(quiz_id)]["result"] = result

    quiz_data[str(quiz_id)]["is_answered"] = 1
    quiz_data[str(quiz_id)]["user_answer"] = user_answer
    user_answers[str(quiz_id)] = user_answer

    #need to determined dynamically later
    refer_id = quiz_data[str(quiz_id)]["refer_id"]

    return jsonify(result=result, refer_id=refer_id)

# restart the whole quiz
@app.route('/restart', methods=['GET'])
def restart():
    global quiz_data
    global correct_answer_count
    global current_quiz_id
    global user_answers

    correct_answer_count = 0
    current_quiz_id = 1
    user_answers.clear()
    for id, value in quiz_data.items():
        value["is_answered"] = 0
        value["user_answer"] = 0
        value["result"] = ""

    return jsonify(code=200)

# retrive the pending quiz id
@app.route('/getPendingQuizId', methods=['GET'])
def getPendingQuizId():
    
    global current_quiz_id
    global quiz_data

    if current_quiz_id > len(quiz_data):
        return jsonify(id=-1)

    return jsonify(id=current_quiz_id)

# retrive the overall score        
@app.route('/score')
def score():
    global quiz_data
    global correct_answer_count

    score = correct_answer_count
    total = len(quiz_data)
    
    return render_template('score.html',score=score, total=total) #insert var here if needed

@app.route('/display/<filename>')
def display_video(filename):
    return redirect(url_for('static',filename='videos/' + filename), code=301)

@app.route('/displayImg/<filename>')
def display_image(filename):
    return redirect(url_for('static',filename='photos/' + filename), code=301)

if __name__ == '__main__':
   app.run(debug = True)