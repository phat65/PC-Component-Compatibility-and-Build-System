document.addEventListener("DOMContentLoaded", function () {
    const forms = document.querySelectorAll("form.ajax-add-to-cart");
    if (!forms.length) return;

    function getCsrfToken() {
        const csrfMeta = document.querySelector('meta[name="_csrf"]');
        const metaToken = csrfMeta ? csrfMeta.getAttribute("content") : "";
        if (metaToken) {
            return metaToken;
        }

        const cookieMatch = document.cookie.match(/(?:^|; )XSRF-TOKEN=([^;]+)/);
        if (cookieMatch) {
            return decodeURIComponent(cookieMatch[1]);
        }

        return "";
    }

    function getCsrfHeader() {
        const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
        return csrfHeaderMeta ? csrfHeaderMeta.getAttribute("content") : "X-XSRF-TOKEN";
    }

    function getCsrfParameter() {
        const csrfParameterMeta = document.querySelector('meta[name="_csrf_parameter"]');
        return csrfParameterMeta ? csrfParameterMeta.getAttribute("content") : "_csrf";
    }

    function updateHeaderCartCount(cartItemCount) {
        if (cartItemCount === undefined || cartItemCount === null) {
            return;
        }

        document.querySelectorAll(".cart-count").forEach(element => {
            element.textContent = cartItemCount;
        });
    }

    function getToastContainer() {
        let container = document.querySelector(".app-toast-container");
        if (!container) {
            container = document.createElement("div");
            container.className = "app-toast-container";
            container.setAttribute("aria-live", "polite");
            document.body.appendChild(container);
        }
        return container;
    }

    function showToast(message, type = "success") {
        const toast = document.createElement("div");
        toast.className = `app-toast app-toast-${type}`;
        toast.setAttribute("role", "status");
        toast.textContent = message;

        getToastContainer().appendChild(toast);
        window.setTimeout(() => toast.classList.add("is-visible"), 10);
        window.setTimeout(() => {
            toast.classList.remove("is-visible");
            window.setTimeout(() => toast.remove(), 220);
        }, 2600);
    }

    forms.forEach(form => {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const submitButton = form.querySelector('[type="submit"]');
            const headers = {
                "Accept": "application/json",
                "X-Requested-With": "XMLHttpRequest"
            };
            const formData = new FormData(form);
            const csrfToken = getCsrfToken();
            if (csrfToken) {
                headers[getCsrfHeader()] = csrfToken;
                if (!formData.has(getCsrfParameter())) {
                    formData.append(getCsrfParameter(), csrfToken);
                }
            }

            if (submitButton) {
                submitButton.disabled = true;
            }

            fetch(form.action, {
                method: "POST",
                headers: headers,
                body: formData
            })
                .then(response => {
                    if (response.redirected && response.url.includes("/auth/login")) {
                        window.location.href = response.url;
                        return null;
                    }

                    if (response.status === 401) {
                        window.location.href = "/auth/login?required";
                        return null;
                    }

                    if (!response.ok) {
                        return response.json().then(data => {
                            throw new Error(data.error || "Unable to add product to cart.");
                        });
                    }

                    return response.json();
                })
                .then(data => {
                    if (!data) return;

                    updateHeaderCartCount(data.cartItemCount);
                    showToast(data.message || "Product added to cart.", "success");
                })
                .catch(error => {
                    showToast(error.message || "Unable to add product to cart.", "danger");
                })
                .finally(() => {
                    if (submitButton) {
                        submitButton.disabled = false;
                    }
                });
        });
    });
});
