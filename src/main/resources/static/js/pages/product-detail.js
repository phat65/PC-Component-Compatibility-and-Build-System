document.addEventListener('DOMContentLoaded', () => {
    const stars = document.querySelectorAll('.rating-input span');
    const ratingInput = document.getElementById('ratingValue');
    if (!stars.length || !ratingInput) return;

    stars.forEach(star => {
        star.addEventListener('click', () => {
            const val = parseInt(star.getAttribute('data-value'), 10);
            ratingInput.value = val;
            stars.forEach(s => {
                s.classList.toggle('active', parseInt(s.getAttribute('data-value'), 10) <= val);
                s.classList.remove('hovered');
            });
        });

        star.addEventListener('mouseover', () => {
            const val = parseInt(star.getAttribute('data-value'), 10);
            stars.forEach(s => {
                s.classList.toggle('hovered', parseInt(s.getAttribute('data-value'), 10) <= val);
            });
        });

        star.addEventListener('mouseout', () => {
            stars.forEach(s => s.classList.remove('hovered'));
        });
    });
});

function validateFeedbackForm() {
    const rating = document.getElementById('ratingValue').value;
    const comment = document.querySelector('textarea[name="comment"]').value.trim();
    const errorMsg = document.getElementById('feedbackErrorMsg');

    errorMsg.classList.add('is-hidden');
    errorMsg.textContent = '';

    if (!rating) {
        showInlineError('Please select a star rating before submitting.');
        return false;
    }

    if (comment.length === 0) {
        showInlineError('Please enter your comment.');
        return false;
    }

    return true;
}

function showInlineError(message) {
    const errorMsg = document.getElementById('feedbackErrorMsg');
    errorMsg.textContent = message;
    errorMsg.classList.remove('is-hidden');
}

function setMainImage(src) {
    const mainImage = document.getElementById('mainImage');
    if (mainImage) mainImage.src = src;
}
