document.addEventListener('DOMContentLoaded', () => {
    initProductGallery();
    initRatingInput();
});

function initProductGallery() {
    const mainImage = document.getElementById('mainImage');
    const thumbs = document.querySelectorAll('.product-detail__thumb');
    const currentCounter = document.getElementById('galleryCurrent');

    if (!mainImage || !thumbs.length) {
        return;
    }

    thumbs.forEach(thumb => {
        thumb.addEventListener('click', () => {
            const imageSrc = thumb.dataset.imageSrc;
            const imageIndex = parseInt(thumb.dataset.imageIndex || '0', 10);
            if (!imageSrc || mainImage.getAttribute('src') === imageSrc) {
                return;
            }

            mainImage.classList.add('is-switching');
            window.setTimeout(() => {
                mainImage.src = imageSrc;
                thumbs.forEach(item => item.classList.remove('is-active'));
                thumb.classList.add('is-active');
                if (currentCounter) {
                    currentCounter.textContent = String(imageIndex + 1);
                }
                mainImage.classList.remove('is-switching');
            }, 90);
        });
    });
}

function initRatingInput() {
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
}

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
