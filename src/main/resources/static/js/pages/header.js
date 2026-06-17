(function () {
    if (window.__siteHeaderInit) return;
    window.__siteHeaderInit = true;

    function initDropdown(toggleId, menuId) {
        const toggle = document.getElementById(toggleId);
        const menu = document.getElementById(menuId);
        if (!toggle || !menu) return;

        function close() {
            menu.classList.remove('is-open');
            toggle.setAttribute('aria-expanded', 'false');
            menu.setAttribute('aria-hidden', 'true');
        }

        function open() {
            menu.classList.add('is-open');
            toggle.setAttribute('aria-expanded', 'true');
            menu.setAttribute('aria-hidden', 'false');
        }

        toggle.addEventListener('click', function (event) {
            event.stopPropagation();
            if (menu.classList.contains('is-open')) {
                close();
            } else {
                document.querySelectorAll('.site-header__dropdown.is-open').forEach(function (el) {
                    el.classList.remove('is-open');
                    el.setAttribute('aria-hidden', 'true');
                });
                document.querySelectorAll('.site-header__categories-toggle[aria-expanded="true"], .site-header__account-toggle[aria-expanded="true"]').forEach(function (btn) {
                    btn.setAttribute('aria-expanded', 'false');
                });
                open();
            }
        });

        document.addEventListener('click', function (event) {
            if (!menu.contains(event.target) && event.target !== toggle) {
                close();
            }
        });

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') close();
        });
    }

    function initMobileMenu() {
        const toggle = document.getElementById('mobileMenuToggle');
        const panel = document.getElementById('mobileMenu');
        if (!toggle || !panel) return;

        function close() {
            panel.classList.remove('is-open');
            toggle.setAttribute('aria-expanded', 'false');
            panel.setAttribute('aria-hidden', 'true');
            document.body.classList.remove('site-header-menu-open');
        }

        function open() {
            panel.classList.add('is-open');
            toggle.setAttribute('aria-expanded', 'true');
            panel.setAttribute('aria-hidden', 'false');
            document.body.classList.add('site-header-menu-open');
        }

        toggle.addEventListener('click', function () {
            if (panel.classList.contains('is-open')) {
                close();
            } else {
                open();
            }
        });

        panel.querySelectorAll('a').forEach(function (link) {
            link.addEventListener('click', close);
        });

        document.addEventListener('keydown', function (event) {
            if (event.key === 'Escape') close();
        });
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function () {
            initDropdown('categoriesToggle', 'categoriesMenu');
            initDropdown('profileToggle', 'profileDropdown');
            initMobileMenu();
        });
    } else {
        initDropdown('categoriesToggle', 'categoriesMenu');
        initDropdown('profileToggle', 'profileDropdown');
        initMobileMenu();
    }
})();
