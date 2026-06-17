(function () {
    if (window.__homeEffectsInit) return;
    window.__homeEffectsInit = true;

    var prefersReduced = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    if (prefersReduced) return;

    function bindTilt(container, selector, maxDeg) {
        if (!container) return;

        var items = container.querySelectorAll(selector);
        if (!items.length) return;

        items.forEach(function (el) {
            el.addEventListener('mousemove', function (e) {
                var rect = el.getBoundingClientRect();
                var x = (e.clientX - rect.left) / rect.width - 0.5;
                var y = (e.clientY - rect.top) / rect.height - 0.5;
                var rotY = x * maxDeg;
                var rotX = -y * maxDeg;
                el.style.setProperty('--tilt-x', rotX + 'deg');
                el.style.setProperty('--tilt-y', rotY + 'deg');
                el.style.setProperty('--card-tilt-x', rotX + 'deg');
                el.style.setProperty('--card-tilt-y', rotY + 'deg');
            });

            el.addEventListener('mouseleave', function () {
                el.style.setProperty('--tilt-x', '0deg');
                el.style.setProperty('--tilt-y', '0deg');
                el.style.setProperty('--card-tilt-x', '0deg');
                el.style.setProperty('--card-tilt-y', '0deg');
            });
        });
    }

    function bindHeroParallax() {
        var hero = document.querySelector('.home-main .hero');
        var rig = document.getElementById('heroRig');
        if (!hero || !rig) return;

        hero.addEventListener('mousemove', function (e) {
            var rect = hero.getBoundingClientRect();
            var x = (e.clientX - rect.left) / rect.width - 0.5;
            var y = (e.clientY - rect.top) / rect.height - 0.5;
            rig.style.transform =
                'rotateY(' + (x * 18) + 'deg) rotateX(' + (-y * 14 + 12) + 'deg)';
        });

        hero.addEventListener('mouseleave', function () {
            rig.style.transform = '';
        });
    }

    function init() {
        var main = document.querySelector('.home-main');
        if (!main) return;

        bindHeroParallax();
        bindTilt(main, '.category-card', 10);
        bindTilt(main, '.product-card', 8);
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
