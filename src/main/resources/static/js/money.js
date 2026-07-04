(function (window) {
    "use strict";

    const formatter = new Intl.NumberFormat("vi-VN", {
        maximumFractionDigits: 0,
        minimumFractionDigits: 0
    });

    function toNumber(value) {
        const numeric = Number(value);
        return Number.isFinite(numeric) ? numeric : 0;
    }

    function formatVnd(value) {
        return formatter.format(Math.round(toNumber(value))) + " VND";
    }

    function parseMoney(value) {
        const raw = String(value || "").trim();
        const plainNumber = raw.replace(/,/g, "");
        if (/^-?\d+(\.\d+)?$/.test(plainNumber)) {
            return Number(plainNumber);
        }

        const cleaned = raw.replace(/[^\d-]/g, "");
        const numeric = Number.parseInt(cleaned, 10);
        return Number.isFinite(numeric) ? numeric : 0;
    }

    window.Money = {
        formatVnd,
        parseMoney
    };
})(window);
