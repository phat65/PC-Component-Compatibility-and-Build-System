document.addEventListener("DOMContentLoaded", function () {
    const resendButton = document.getElementById("resendButton");
    const resendHelp = document.getElementById("resendHelp");

    if (!resendButton) {
        return;
    }

    let remainingSeconds = Number.parseInt(resendButton.dataset.delay || "0", 10);
    if (!Number.isFinite(remainingSeconds) || remainingSeconds <= 0) {
        return;
    }

    const originalText = resendButton.textContent.trim() || "Send code again";
    setDisabledState();

    const countdown = window.setInterval(function () {
        remainingSeconds -= 1;

        if (remainingSeconds <= 0) {
            window.clearInterval(countdown);
            resendButton.disabled = false;
            resendButton.removeAttribute("aria-disabled");
            resendButton.textContent = originalText;

            if (resendHelp) {
                resendHelp.textContent = "You can request a new code now.";
            }
            return;
        }

        setDisabledState();
    }, 1000);

    function setDisabledState() {
        resendButton.disabled = true;
        resendButton.setAttribute("aria-disabled", "true");
        resendButton.textContent = `Send again in ${remainingSeconds}s`;

        if (resendHelp) {
            resendHelp.textContent = "Please wait before requesting another verification code.";
        }
    }
});
