/**
 * Quiz functionality for the Learning Trainer application.
 */

document.addEventListener('DOMContentLoaded', function() {
    const checkBtn = document.getElementById('checkBtn');
    const radioButtons = document.querySelectorAll('input[name="answer"]');
    const form = document.getElementById('quizForm');
    
    // Enable check button when an answer is selected
    radioButtons.forEach(radio => {
        radio.addEventListener('change', function() {
            checkBtn.disabled = false;
        });
    });
    
    // Handle check button click
    checkBtn.addEventListener('click', function() {
        const selectedAnswer = document.querySelector('input[name="answer"]:checked');
        
        if (!selectedAnswer) {
            return;
        }
        
        // Disable button and show loading state
        checkBtn.disabled = true;
        checkBtn.textContent = 'Checking...';
        form.classList.add('loading');
        
        // Prepare request data
        const request = {
            questionId: document.getElementById('questionId').value,
            answerText: selectedAnswer.value,
            type: document.getElementById('questionType').value
        };
        
        // Send POST request to check answer
        fetch('/trainer/quiz/check', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(request)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (data.correct) {
                if (data.completed) {
                    // Quiz completed - show message and redirect to start
                    alert(data.message || 'Congratulations! You have completed all questions!');
                    window.location.href = '/trainer/';
                } else {
                    // Correct answer - load next question
                    window.location.href = '/trainer/quiz?type=' + request.type;
                }
            } else {
                // Wrong answer - show alert and allow retry
                alert(data.message || 'Wrong answer! Please try again.');
                
                // Re-enable button
                checkBtn.disabled = false;
                checkBtn.textContent = 'Check Answer';
                form.classList.remove('loading');
            }
        })
        .catch(error => {
            console.error('Error checking answer:', error);
            alert('An error occurred. Please try again.');
            
            // Re-enable button
            checkBtn.disabled = false;
            checkBtn.textContent = 'Check Answer';
            form.classList.remove('loading');
        });
    });
});
